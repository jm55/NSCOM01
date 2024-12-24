package network;

import java.io.*;
import java.net.*;
import java.nio.file.Files;

import data.*;
import utils.*;
import gui.*;

import javax.swing.*;

/**
 * Client network object of the program.
 * Contains functions for sending, receiving, and pinging target host.
 * Also handles file writing.
 */
public class Client{
	private final String className = "Client";
	private int DATAPORT = 61001;
	private DatagramSocket socket = null;
	private DatagramPacket packet = null;
	private int PORT = -1, BUFFER_SIZE = 512;
	private long TSIZE = 0;
	private InetAddress target = null;
	private final int CheckTimeout = 5000; //NETWORK TARGET CHECKING, NOT RELATED TO TFTP
	
	public Client() {
		setDefaults();
	}
	
	public Client(String targetIP, int controlPort, int dataPort, int BUFFER_SIZE) {
		try {
			this.target = InetAddress.getByName(targetIP);
			this.PORT = controlPort;
			if(BUFFER_SIZE > 0)
				this.BUFFER_SIZE = BUFFER_SIZE;
		} catch (UnknownHostException e) {
			this.target = null;
			this.PORT = -1;
		}
	}
	
	public Client(InetAddress target, int controlPort, int dataPort, int BUFFER_SIZE) {
		this.target = target;
		this.PORT = controlPort;
		this.BUFFER_SIZE = BUFFER_SIZE;
	}
	
	public void setDefaults() {
		try {
			this.target = InetAddress.getByName("localhost"); //Default target as localhost
			this.PORT = 69; //Default TFTP port
		} catch (UnknownHostException e) {
			this.target = null;
			this.PORT = -1;
			Utility.printMessage(this.className, "setDefaults()", "TryCatch: " + e.getMessage());
			GUI.errorDialog(null, "UnknownHostException occured!Please try again or restart the application.");
		} finally{
			this.DATAPORT = 61001;
		}
	}
	
	public void setDataPort(int dataPort){
		this.DATAPORT = dataPort;
	}
	
	public void setBufferSize(int BUFFER_SIZE) {
		this.BUFFER_SIZE = BUFFER_SIZE;
	}
	
	public boolean send(File f, String[] opts, String[] vals) {
		boolean state = false;
		if(f == null)
			return state;
		openConnection();
		if(f.exists() && this.socket.isConnected())
			if(askWritePermission(f, opts, vals))
				state = writeToServer(f, opts, vals);
			else
				GUI.errorDialog(null, "Writing to server failed, please try again.");
		closeConnection();
		reset();
		return state;
	}
	
	public File receive(String filename, String saveAs, String[] opts, String[] vals) {
		if(filename == null)
			return null;
		openConnection();
		Long tsize = askReadPermission(filename, opts, vals);
		//Check if value for tsize was given as per TFTP protocols
		if(tsize == 0) {
			closeConnection();
			reset();
			return null;
		}
		//Check available disk space
		File tempFile = new File(saveAs); //To save on a temp folder of the program.
		if(tempFile.getFreeSpace() >= tsize)
			tempFile = readFromServer(filename, tempFile, opts, vals);
		else {
			this.packet = new DatagramPacket(TFTP.getErrPacket(3), TFTP.getErrPacket(3).length);
			try {
				this.socket.send(this.packet);
				GUI.errorDialog(null, "Disk Full!\nCannot Write to File.");
			} catch (IOException e) {
				Utility.printMessage(this.className, "receive(filename, saveAs, opts, vals)", "Exception: " + e.getMessage());
				GUI.errorDialog(null, "IOException occured!\nPlease try again.");
			}
		}
		closeConnection();
		reset();
		return tempFile;
	}
	
	private int readParams(String[][] checking, String[] opts, String[] vals) {
		int match = 0;
		//Revamped design which now considers the insisted blksize of the TFTP server as noted on a Wireshark test.
		for(int i = 0; i < vals.length; i++) {
			for(int j = 0;  j < checking[0].length; j++) {
				if(opts[i].equalsIgnoreCase(checking[0][j])) { //Same Item
					if(checking[0][j].equalsIgnoreCase("blksize")){ //Check if TFTP asserts a blksize
						this.BUFFER_SIZE = Integer.parseInt(checking[1][j]);
						match++;
					}
					else if(checking[0][j].equalsIgnoreCase("tsize")){ //Check if TFTP asserts a tsize
						this.TSIZE = Long.parseLong(checking[1][j]);
						match++;
					}
					else if(checking[1][j].equalsIgnoreCase(vals[i]) && !checking[0][j].equalsIgnoreCase("blksize")){
						match++;
					}
				}
			}
		}
		return match;
	}
	
	private byte[] exchangeRequestPackets(byte[] requestPacket, String[] opts, String[] vals) {
		this.packet = new DatagramPacket(requestPacket, requestPacket.length);
		try {
			//Send this.packet to serve
			this.socket.connect(this.target, this.PORT); //USE 'CONTROL' this.socket OF TFTP
			this.socket.send(this.packet);
			
			//Receive ACK or ERROR this.packet
			this.packet = new DatagramPacket(new byte[512], 512); //Limited to 512 as per RFC document which states that any request is limited to 512 bytes which implies that OACK may be the same.
			this.socket.connect(this.target, this.DATAPORT); //SWITCH OVER this.packet TO SPECIFIED DATAPORT AND NOT TO PORT 69
			this.socket.receive(this.packet);
			
			//Return trimmed byte[]
			return Utility.trimPacket(this.packet, this.className, "exchangethis.packets()");
		}catch(IOException e) {
			Utility.printMessage(this.className, "exchangethis.packets(f, opts, vals)", "IOException" + e.getMessage());
			GUI.errorDialog(null, "IOException occured!\nPlease try again.");
			return null;
		}
	}
	
	private boolean askWritePermission(File f, String[] opts, String[] vals) {
		String methodName = "askWritePermission(f,opts,vals)";
		if(!isConnected() || f == null)
			return false;
		byte[] receivedPacket = exchangeRequestPackets(TFTP.getWRQPacket(f, "octet", opts, vals), opts, vals);
		if(receivedPacket == null)
			return false;
		boolean isOACK = TFTP.isOACK(receivedPacket), isError = TFTP.isError(receivedPacket);
		if(isOACK){ //Confirm that the this.packet is an OACK and not an Error
			if(readParams(TFTP.extractOACK(receivedPacket), opts, vals) == vals.length)
				return true;
		}else if(isError){ //Confirm that the this.packet is an Error
			String[] error = TFTP.extractError(receivedPacket);
			displayError(error, methodName);
		}else {
			Utility.printMessage(this.className, methodName, "Unexpected this.packet with opCode: " + TFTP.getOpCode(this.packet));
			GUI.warningDialog(null, "An unexpected this.packet was received!");
		}
		return false;
	}
	
	private long askReadPermission(String filename, String[] opts, String[] vals) {
		String methodName = "askReadPermission(filename,opts,vals)";
		this.TSIZE = 0;
		this.BUFFER_SIZE = 512;
		if(!isConnected() || filename == null || filename.length() == 0)
			return this.TSIZE;
		byte[] receivedPacket = exchangeRequestPackets(TFTP.getRRQPacket(filename, "octet", opts, vals), opts, vals);
		boolean isOACK = TFTP.isOACK(receivedPacket), isError = TFTP.isError(receivedPacket);
		if(isOACK){
			if(readParams(TFTP.extractOACK(receivedPacket), opts, vals) == vals.length)
				this.TSIZE = -1;
		}else if(isError){ //Confirm that the this.packet is an Error
			displayError(TFTP.extractError(receivedPacket), methodName);
			this.TSIZE = -1;
		}else {
			Utility.printMessage(this.className, methodName, "Unexpected this.packet with opCode: " + TFTP.getOpCode(this.packet));
			GUI.warningDialog(null, "An unexpected this.packet was received!");
		}
		return this.TSIZE; //Modify freely when needed.
	}
	
	private void sendPacket(DatagramPacket packet) {
		try {
			if(this.socket.isConnected()) {
				this.packet = packet;
				this.socket.send(this.packet);	
			}
		}catch(IOException e) {
			Utility.printMessage(this.className, "sendthis.packet(newthis.packet)", "IOException: " + e.getMessage());
			GUI.errorDialog(null, "Exception occured!\nPlease try again.");
		}
	}
	
	private void receivePacket(int blockSize) {
		try {
			if(this.socket.isConnected()) {
				this.packet = new DatagramPacket(new byte[blockSize], blockSize);
		    	this.socket.receive(this.packet);
			}
		}catch(IOException e) {
			Utility.printMessage(this.className, "receivethis.packet(blockSize)", "IOException: " + e.getMessage());
			GUI.errorDialog(null, "Exception occured!\nPlease try again.");
		}
		
	}
	
	private boolean uploadFile(File f) {
		String methodName = "uploadFile()";
		try {
			long SIZE = Files.size(f.toPath()); //SIZE OF FILE
			InputStream inputStream = new FileInputStream(f.getAbsolutePath()); //FILE STREAMING
			int blocksize = this.BUFFER_SIZE; //FOR CONFIGURATION
			//BUFFER BYTE[] CONFIGURATION
			byte[] buffer = new byte[blocksize]; //DATA SEGMENT OF this.packet
            if(SIZE < blocksize) //IF FILE SIZE IS INITIALLY SMALLER THAN BUFFER SIZE THEN SET BUFFER TO JUST FILE'S SIZE
            	buffer = new byte[(int)SIZE];
            int ctr = 1, ACKval = 0, cycle = 0; //COUNTERS FOR BLOCK#
            boolean error = false, validACK = false;
            while(inputStream.read(buffer) > 0) { //While file not done streaming.
            	validACK = false;
            	do {
            		//Sending byte of file
            		byte[] packetByte = TFTP.getDataPacket(ctr, buffer); //BUILD A DATA TFTP this.packet
            		sendPacket(new DatagramPacket(packetByte,packetByte.length));
                	//Await for response
                	receivePacket(blocksize);
                	boolean isACK = TFTP.isACK(this.packet.getData()), isError = TFTP.isError(this.packet.getData());
                	if(isACK) {
                		ACKval = TFTP.extractBlockNumber(packetByte);
                		//Succeeding if statement is implicitly non-duplicate ACK. 
                		//If the case is otherwise then it will just repeat the entire process from sending byte of file.
                		if(ACKval == ctr) { //Change to comparing received block number in ACK to ctr
                			validACK = true;
                			if(ACKval == 65535) {
                				cycle++; //Cycle over beyond 65535.
                				ctr=0;
                			}else
                				ctr++;
                		}
                	}else if(isError){
                		Utility.printMessage(this.className, methodName, "Possible Error @ OPVal: " + TFTP.getOpCode(this.packet.getData()));
            			Utility.printMessage(this.className, methodName, "Error: " + Utility.arrayToString(TFTP.extractError(this.packet.getData())));
                		displayError(TFTP.extractError(this.packet.getData()), methodName);
                	}
            	}while(!validACK && !error); //Repeat if sending if the ACK received is not a 'new' block of data.
            	//RECOMPUTES BLKSIZE IF AVAILABLE DATA IS LESS THAN THE BLKSIZE; DO NOT MOVE THIS. LET IT BE PLACED LAST.
            	if(inputStream.available() < blocksize) {
					blocksize = inputStream.available();
					buffer = new byte[blocksize];
				}
			}
			inputStream.close();
			return true;
		} catch(IOException ie) {
			Utility.printMessage(this.className, methodName, "IOException: " + ie.getMessage());
			GUI.errorDialog(null, "IOException occured!\nPlease try again.");
		}
		return false; //A fatal error (non-TFTP) occurs.
	}
	
	private boolean writeToServer(File f, String[] opts, String[] vals) {
		if(!this.socket.isConnected())
			return false;	
		return uploadFile(f);
	}
	
	private File downloadFile(File tempFile) {
		String methodName = "streamFile(tempFile)";
		long tsize = this.TSIZE;
		int blocksize = this.BUFFER_SIZE + 4; //FOR CONFIGURATION
		int subtotal = 0;
		try {
			FileOutputStream outputStream = new FileOutputStream(tempFile); //BYTE STREAM FILE WRITING
			int ctr = 0;
			boolean validBlock = false, error = false;
			do {
				do {
					validBlock = false;
					byte[] ackbyte = TFTP.getACK(ctr);
					
					//RECOMPUTE BLKCSIZE IF COMPUTED REMAINING BYTES OF FILE IS LESS THAN BUFFER/BLKSIZE; DO NOT MOVE
					if(tsize-subtotal < blocksize) 
						blocksize = (int)tsize-subtotal + 4;
					
					//SEND AN ACK FIRST
					if(ctr == 0) { //TO SYNC WITH FUTURE DATAthis.packetS THAT START AT 1
						sendPacket(new DatagramPacket(ackbyte, ackbyte.length));
						ctr++;
					}
					//AWAIT FOR DATA RESPONSE
					receivePacket(blocksize);
					
					//Check if a data packet.
					if(TFTP.isData(this.packet.getData())) {
						//SAVE BUFFER TO FILE
						byte[] data = TFTP.extractData(this.packet);
						int block = TFTP.extractBlockNumber(this.packet);
						//Succeeding if statement is implicitly non-duplicate ACK. If the case is otherwise then it will just repeat the entire process from sending byte of file.
						if(block == 65535)
							ctr = 0;
						subtotal += data.length;
						if(block == ctr) {
							validBlock = true;
							outputStream.write(data, 0, data.length); //Write data with size from 0 to its length
						}else if(block > ctr) { //SERVER IS ADVANCED THAN CLIENT
							String[] e = {"0", TFTP.getErrMsg(0)};
							displayError(e, methodName);
							return null;
						}else{ // SERVER IS LATE THAN CLIENT
							this.socket.send(new DatagramPacket(ackbyte, ackbyte.length));
						}
					}
					//Check if an error
					if(TFTP.isError(this.packet.getData())){
                		String[] err = TFTP.extractError(this.packet.getData()); //Structure at {Error Code, Error Message}
                		displayError(err, methodName);
                		tempFile.delete();
					}
				}while(!validBlock && !error);
				byte[] ackbyte = TFTP.getACK(ctr);
				this.packet = new DatagramPacket(ackbyte, ackbyte.length);
				this.socket.send(this.packet);
				ctr++;
			}while(subtotal < tsize);
			outputStream.close();
		} catch(FileNotFoundException fe) {
			Utility.printMessage(this.className, methodName, "FileNotFoundException: " + fe.getMessage());
			GUI.errorDialog(null, "FileNotFoundException occured!\nPlease try again.");
		} catch (Exception e) {
			Utility.printMessage(this.className, methodName, "Exception: " + e.getMessage());
			GUI.errorDialog(null, "Exception occured!\nPlease try again.");
			try {
				byte[] errPacket = TFTP.getErrPacket(0);
				this.packet = new DatagramPacket(errPacket, errPacket.length);
				this.socket.send(this.packet);
			} catch (IOException ie) {
				Utility.printMessage(this.className, methodName, "IOException: " + ie.getMessage());
				GUI.errorDialog(null, "IOException occured!\nPlease try again");
			}
			return null;
		}
		return tempFile;
	}
	
	private File readFromServer(String filename, File tempFile, String[] opts, String[] vals) {
		String methodName = "readFromServer()";
		if(!this.socket.isConnected())
			return null;
		if(!tempFile.exists()) {
			try {
				tempFile.createNewFile();
			} catch (Exception e1) {
				GUI.popDialog(null,"Error creating new file for the download!", "Error", JOptionPane.ERROR_MESSAGE);
				Utility.printMessage(this.className, methodName, "Error creating new file for the download: " + e1.getMessage());
				return null;
			}
		}
		return downloadFile(tempFile);
	}
	
	public void reset() {
		Utility.printMessage(this.className, "reset()","");
		this.BUFFER_SIZE = 512;
		this.TSIZE = 0;
	}
	
	public boolean openConnection() {
		String methodName = "openConnection()";
		Utility.printMessage(this.className,methodName, "Opening connection...");
		if(target != null && PORT != -1) {
			try {
				//Attempt connection
				Utility.printMessage(this.className,methodName, "Creating DatagramSocket()...");
				this.socket = new DatagramSocket(69);
				this.socket.setSoTimeout(3000);
				this.socket.connect(this.target, this.PORT);
			} catch (SocketException e) {
				GUI.errorDialog(null, "this.socketException occured!\nPlease try again");
				Utility.printMessage(this.className,methodName, "this.socketException: " + e.getMessage());
				return false;
			}
		}
		return this.socket.isConnected();
	}
	
	public boolean closeConnection() {
		this.socket.close();
		return this.socket.isClosed();
	}
	
	public boolean isConnected() {
		if(this.socket == null || !this.socket.isBound()) //this.socket is null
			return false;
		return this.socket.isConnected();
	}
	
	public boolean targetIsOnline() {
		if(this.target == null)
			return false;
		return targetIsOnline(this.target);
	}
	
	public boolean targetIsOnline(InetAddress target) {
		String methodName = "targetIsOnline(target)";
		if(target != null) {
			try {
				return target.isReachable(this.CheckTimeout);
			} catch (IOException e) {
				Utility.printMessage(this.className, methodName, "IOException: " + e.getMessage());
				Utility.printMessage(this.className, methodName, target.getHostAddress() + " is unreachable.");
				GUI.errorDialog(null, "IOException occured!\nPlease try again");
			}
		}
		return false;
	}
	
	public String getConnectionDetails() {
		return getConnectionDetails(this.socket);
	}
	
	private void displayError(String[] error, String methodName) {
		Utility.printMessage(this.className, methodName, "Error: " + TFTP.getErrMsg(Integer.parseInt(error[0])));	
		GUI.errorDialog(null, "TFTP Error Received: " + TFTP.getErrMsg(Integer.parseInt(error[0])));
	}
	
	private String getConnectionDetails(DatagramSocket socket) {
		String methodName = "getConnectionDetails(this.socket)";
		Utility.printMessage(this.className, methodName, "Getting this.socket connection details...");
		if(this.socket != null)
			return this.socket.getLocalAddress().getHostAddress() + ":" + this.socket.getLocalPort() + " <==> " + this.socket.getInetAddress().getHostAddress() + ":" + this.socket.getPort();
		Utility.printMessage(this.className, methodName, "this.socket is null.");
		return null;
	}
	
	public void forceSend(DatagramPacket packet, InetAddress target, Integer port) {
		String methodName = "forceSend(this.packet,target,port)";
		if(target == null || port == null) {
			GUI.warningDialog(null, "Please set a target address.");
			return;
		}
		try {
			if(!isConnected()) {
				this.socket = new DatagramSocket();
				this.socket.connect(target, port);
				if(this.socket.isConnected())
					this.socket.send(this.packet);
				this.socket.close();
				return;
			}
			//this.socket already in use, force send on another connection instead.
			InetAddress tempTarget = this.socket.getInetAddress();
			int tempPort = this.socket.getPort();
			//Close Connection
			closeConnection();
			//Opening temporary connection to force send this.packet
			this.socket = new DatagramSocket(port);
			this.socket.connect(target,port);
			this.socket.send(this.packet);
			//Restoring original connection
			this.socket.connect(tempTarget, tempPort);
		}catch(IOException e) {
			Utility.printMessage(this.className, methodName, "Error occured: " + e.getMessage());
			GUI.errorDialog(null, "IOException occured!\nPlease try again.");
			this.socket.close();
		}
	}
}