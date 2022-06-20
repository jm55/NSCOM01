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
	private Utility u = new Utility();
	private final String className = "Client";
	private final int DATAPORT = 61001;
	private DatagramSocket socket = null;
	private DatagramPacket packet = null;
	private int PORT = -1, BUFFER_SIZE = 512, TSIZE = 0;
	private byte[] buffer = null;
	private InetAddress target = null;
	private final int CheckTimeout = 5000; //NETWORK TARGET CHECKING, NOT RELATED TO TFTP
	private TFTP tftp = new TFTP();
	private GUI gui = new GUI();
	private String[] globalOpts = null, globalVals = null;
	
	public Client() {
		setDefaults();
	}
	
	/**
	 * Builds a Client object
	 * @param host Target host
	 * @param port Target port
	 * @param BUFFER_SIZE buffersize of packets, set as <= 0 if default
	 */
	public Client(String host, int port, int BUFFER_SIZE) {
		String methodName = "Client(host,port,BUFFER_SIZE)";
		u.printMessage(this.className, methodName, "Building Client as " + host + ":" + port + "...");
		try {
			this.target = InetAddress.getByName(host);
			this.PORT = port;
			if(BUFFER_SIZE > 0)
				this.BUFFER_SIZE = BUFFER_SIZE;
		} catch (UnknownHostException e) {
			target = null;
			this.PORT = -1;
		}
		if(this.target == null)
			u.printMessage(this.className, methodName, "Building Client as " + host + ":" + port + " failed.");
		else 
			u.printMessage(this.className, methodName, "Building Client as " + host + ":" + port + " successful.");
	}
	
	public Client(InetAddress target, int port, int BUFFER_SIZE) {
		String methodName = "Client(target, port, BUFFER_SIZE)";
		this.target = target;
		this.PORT = port;
		this.BUFFER_SIZE = BUFFER_SIZE;
		u.printMessage(this.className, methodName, "Building Client as " + target.getCanonicalHostName() + ":" + port + "...");
	}
	
	/**
	 * Sets the Client's default connection target to: 'localhost:69' for a TFTP connection.
	 */
	public void setDefaults() {
		String methodName = "setDefaults()";
		u.printMessage(this.className, methodName, "Setting defaults: localhost:69...");
		try {
			this.target = InetAddress.getByName("localhost"); //Default target as localhost
			this.PORT = 69; //Default TFTP port
		} catch (UnknownHostException e) {
			target = null;
			this.PORT = -1;
			u.printMessage(this.className, methodName, "TryCatch: " + e.getLocalizedMessage());
		}
	}
	
	/**
	 * Sets the BUFFER SIZE of the TFTP transmission.
	 * @param BUFFER_SIZE
	 */
	public void setBufferSize(int BUFFER_SIZE) {
		u.printMessage(this.className, "setBufferSize(BUFFER_SIZE)", "Setting BUFFER_SIZE: " + BUFFER_SIZE);
		this.BUFFER_SIZE = BUFFER_SIZE;
	}
	
	/**
	 * Delegates sending of File to TFTP server connected by socket using the TFTP protocol instructions.
	 * @param f File to be sent.
	 * @param opts Options
	 * @param vals Option values
	 * @return True if successful, false if an valid/acceptable error occurs.
	 */
	public boolean send(File f, String[] opts, String[] vals) {
		String methodName = "send(f,opts,vals)";
		boolean state = false;
		if(f == null)
			return state;
		u.printMessage(this.className, methodName, "Connecting...");
		openConnection();
		if(f.exists() && socket.isConnected()) {
			u.printMessage(this.className, methodName, "File exists and client is connected");
			if(askWritePermission(f, opts, vals)) {
				u.printMessage(this.className, methodName, "Write Permission Accepted!");
				state = writeToServer(f, opts, vals);
			}else {
				u.printMessage(this.className, methodName, "Write Permission Failed!");
			}
		}	
		closeConnection();
		reset();
		return state;
	}
	
	/**
	 * Delegates receiving of File to TFTP server connected by socket using the TFTP protocol instructions.
	 * @param filename Filename of the file intended.
	 * @param saveAs Specified filename of user when downloaded.
	 * @param opts Options
	 * @param vals Option values
	 * @return Tempfile pointed at /downloads in program's folder.
	 */
	public File receive(String filename, String saveAs, String[] opts, String[] vals) {
		if(filename == null)
			return null;
		File tempFile = new File(saveAs); //To save on a temp folder of the program.
		openConnection();
		int tsize = askReadPermission(filename, opts, vals);
		u.printMessage(this.className, "receive(filename, saveAs, opts, vals)", "Returned tsize of " + filename + " = " + tsize + "bytes");
		if(tsize > -1) {
			tempFile = readFromServer(filename, tempFile, opts, vals);
		}
		closeConnection();
		reset();
		return tempFile;
	}
	
	/**
	 * Ask permission to write file on the server connected to this Client.
	 * @param f File to write permission to.
	 * @return True if allowed, false if otherwise.
	 */
	private boolean askWritePermission(File f, String[] opts, String[] vals) {
		String methodName = "askWritePermission(f,opts,vals)";
		
		boolean permission = false;
		if(!isConnected() || f == null)
			return permission;
		u.printMessage(this.className, methodName, "Building write request packet...");
		String mode = "octet";
		byte[] wrq = tftp.getWRQPacket(f, mode, opts, vals);
		packet = new DatagramPacket(wrq, wrq.length);
		try {
			//Send packet to serve
			u.printMessage(this.className, methodName, "Sending packet from " + socket.getLocalPort() + " to " + socket.getRemoteSocketAddress() + "...");
			socket.connect(target, this.PORT); //USE 'CONTROL' SOCKET OF TFTP
			socket.send(packet);
			
			//Receive ACK or ERROR packet
			u.printMessage(this.className, methodName, "Receiving OACK packet to: " + socket.getLocalPort() + "...");
			packet = new DatagramPacket(new byte[512], 512); //LIMITED TO 512 AS THE RFC DOCUMENT REVEALS THAT ANY REQUEST IS LIMITED TO 512 OCTETS, IMPLYING THAT ANY OACK MAY BE THE SAME.
			socket.connect(this.target, this.DATAPORT); //SWITCH OVER PACKET TO SPECIFIED DATAPORT AND NOT TO PORT 69
			socket.receive(packet);
			
			//Trim excess bytes from packets
			byte[] trimmedPacket = u.trimPacket(packet, this.className, methodName); //TRIMMED RCV

			//Confirm that the packet is an OACK and not an Error
			boolean isOACK = tftp.isOACK(trimmedPacket), isError = tftp.isError(trimmedPacket);
			u.printMessage(this.className, methodName, "isOACK: " + isOACK + ", isError: " + isError);	
			if(isOACK){
				String[][] checking = tftp.extractOACK(trimmedPacket);
				int match = 0;	
				u.printOptsValsComparison(this.className, methodName, opts, vals, checking);
				
				//Revamped design which now considers the insisted blksize of the TFTP server as noted on a Wireshark test.
				for(int i = 0; i < vals.length; i++) {
					for(int j = 0;  j < checking[0].length; j++) {
						if(opts[i].equalsIgnoreCase(checking[0][j])) { //Same Item
							if(checking[0][j].equalsIgnoreCase("blksize")){ //Check if TFTP asserts a blksize
								this.BUFFER_SIZE = Integer.parseInt(checking[1][j]);
								match++;
								u.printMessage(this.className, methodName, "Server's blksize: " + this.BUFFER_SIZE);
							}
							if(checking[1][j].equalsIgnoreCase(vals[i]) && !checking[0][j].equalsIgnoreCase("blksize")){
								match++;
								u.printMessage(this.className, methodName, "Matched @ " + checking[1][j]);
							}
						}
					}
				}		
				if(match == vals.length)
					permission = true;
			}else if(isError){
				//Confirm that the packet is an Error
				String[] error = tftp.extractError(trimmedPacket);
				displayError(error, methodName);
			}else {
				u.printMessage(this.className, methodName, "Unexpected packet received.");
			}
		}catch (Exception e) {
			gui.popDialog("Exception occured:\n" + e.getLocalizedMessage(),"Error", JOptionPane.ERROR_MESSAGE);
			u.printMessage(this.className, methodName, "Exception: " + e.getLocalizedMessage());
		}
		return permission;
	}
	
	/**
	 * Ask permission to read file on the server connected to this Client.
	 * @param filename Filename of the File requested.
	 * @return True if allowed/possible, false if otherwise.
	 */
	private int askReadPermission(String filename, String[] opts, String[] vals) {
		String methodName = "askReadPermission(filename,opts,vals)";
		this.TSIZE = -1;
		this.BUFFER_SIZE = 512;
		if(!isConnected() || filename == null || filename.length() == 0)
			return this.TSIZE;
		u.printMessage(this.className, methodName, "Building write request packet...");
		String mode = "octet";
		byte[] rrq = tftp.getRRQPacket(filename, mode, opts, vals);
		packet = new DatagramPacket(rrq, rrq.length);
		try {
			//Send packet to serve
			u.printMessage(this.className, methodName, "Sending packet from " + socket.getLocalPort() + " to " + socket.getRemoteSocketAddress() + "...");
			socket.connect(target, this.PORT); //USE 'CONTROL' SOCKET OF TFTP
			socket.send(packet);
			
			//Receive ACK or ERROR packet
			u.printMessage(this.className, methodName, "Receiving OACK packet to: " + socket.getLocalPort() + "...");
			packet = new DatagramPacket(new byte[512], 512); //LIMITED TO 512 AS THE RFC DOCUMENT REVEALS THAT ANY REQUEST IS LIMITED TO 512 OCTETS, IMPLYING THAT ANY OACK MAY BE THE SAME.
			socket.connect(this.target, this.DATAPORT); //SWITCH OVER PACKET TO SPECIFIED DATAPORT AND NOT TO PORT 69
			socket.receive(packet);
			
			//Trim excess bytes from packets
			byte[] trimmedPacket = u.trimPacket(packet, this.className, methodName); //TRIMMED RCV

			//Confirm that the packet is an OACK and not an Error
			boolean isOACK = tftp.isOACK(trimmedPacket), isError = tftp.isError(trimmedPacket);
			u.printMessage(this.className, methodName, "isOACK: " + isOACK + ", isError: " + isError);
			if(isOACK){
				u.printMessage(this.className, methodName, "isOACK and !isError");
				String[][] checking = tftp.extractOACK(trimmedPacket);
				
				int match = 0;
				u.printOptsValsComparison(this.className, methodName, opts, vals, checking);
				
				//Revamped design which now considers the insisted blksize and tsize by the TFTP server as noted on a Wireshark test.
				for(int i = 0; i < vals.length; i++) {
					for(int j = 0;  j < checking[0].length; j++) {
						if(opts[i].equalsIgnoreCase(checking[0][j])) { //Same Item
							if(checking[0][j].equalsIgnoreCase("blksize")){ //Check if TFTP asserts a blksize
								this.BUFFER_SIZE = Integer.parseInt(checking[1][j]);
								u.printMessage(this.className, methodName, "Server's blksize: " + this.BUFFER_SIZE);
								match++;
							}else if(checking[0][j].equalsIgnoreCase("tsize")){ //Check if what tsize TFTP returns
								this.TSIZE = Integer.parseInt(checking[1][j]);
								u.printMessage(this.className, methodName, "Server's tsize: " + this.TSIZE);
								match++;
							}else if(checking[1][j].equalsIgnoreCase(vals[i]) && (!checking[0][j].equalsIgnoreCase("blksize") && !checking[0][j].equalsIgnoreCase("tsize"))){
								u.printMessage(this.className, methodName, "Matched @ " + checking[1][j]);
								match++;
							}
						}
					}
				}		
				if(match != vals.length)
					this.TSIZE = -1;
			}else if(isError){
				//Confirm that the packet is an Error
				String[] error = tftp.extractError(trimmedPacket);
				displayError(error, methodName);
				this.TSIZE = -1;
			}else {
				u.printMessage(this.className, methodName, "Not an expected packet with opCode: " + tftp.getOpCode(packet));
			}
		}catch (Exception e) {
			gui.popDialog("Exception occured:\n" + e.getLocalizedMessage(),"Error", JOptionPane.ERROR_MESSAGE);
			u.printMessage(this.className, methodName, "Exception: " + e.getLocalizedMessage());
		}
		return this.TSIZE; //Modify freely when needed.
	}
	
	/**
	 * Write file to the server.
	 * File Bytestream Reference: https://www.codejava.net/java-se/file-io/java-io-fileinputstream-and-fileoutputstream-examples
	 * @param f File to be transferred.
	 * @return True if transfer completed, false if otherwise or fatal error/exception occurred.
	 */
	private boolean writeToServer(File f, String[] opts, String[] vals) {
		String methodName = "writeToServer(f,opts,vals)";
		u.printMessage(this.className, methodName, "f.exists()...");

		u.printMessage(this.className, methodName, "Checking if still connected...");
		if(!socket.isConnected())
			return false;
		
		try {
			Integer SIZE = (int)Files.size(f.toPath()); //SIZE OF FILE
			Integer bytesRead = -1; //FOR FILE STREAMING
			
			InputStream inputStream = new FileInputStream(f.getAbsolutePath()); //FILE STREAMING
			
			int tsize = this.TSIZE, blocksize = this.BUFFER_SIZE, timeout = -1; //FOR CONFIGURATION
			
			//BUFFER BYTE[] CONFIGURATION
			u.printMessage(this.className, methodName, "SIZE: " + SIZE + ", " + "BUFFER_SIZE: " + BUFFER_SIZE);
			byte[] buffer = new byte[blocksize]; //DATA SEGMENT OF PACKET
            if(SIZE < blocksize) //IF FILE SIZE IS INITIALLY SMALLER THAN BUFFER SIZE THEN SET BUFFER TO JUST FILE'S SIZE
            	buffer = new byte[SIZE];
            
            u.printMessage(this.className, methodName, "Sending file to target...");            
            int ctr = 1, ACKval = 0; //COUNTERS FOR BLOCK#
            boolean error = false, validACK = false;
            while((bytesRead = inputStream.read(buffer)) > 0) { //While file not done streaming.
            	validACK = false;
            	do {
            		//Sending byte of file
                	byte[] packetByte = tftp.getDataPacket(ctr, buffer); //BUILD A DATA TFTP PACKET
                	packet = new DatagramPacket(packetByte,packetByte.length);
                	socket.send(packet);
                	
                	//Await for response
                	//socket.connect(this.target, this.DATAPORT); //WHEN RECEIVING
                	packet = new DatagramPacket(new byte[blocksize], blocksize);
                	socket.receive(packet);
                	
                	boolean isACK = tftp.isACK(packet.getData()), isError = tftp.isError(packet.getData());
                	u.printMessage(this.className, methodName, "isACK: " + isACK + ", isError: " + isError);
                	if(isACK) {
                		ACKval = tftp.extractBlockNumber(packetByte);
                		u.printMessage(this.className, methodName,"ACK Block#: " + tftp.extractBlockNumber(packet.getData()) + " = " + ACKval + ", Remaining bytes: " + inputStream.available());
                		if(true/**ACKval == ctr*/) { //Change to comparing received block number in ACK to ctr TODO
                			validACK = true;
                			ctr++;
                		}
                	}else if(isError){
                		u.printMessage(this.className, methodName, "Possible Error @ OPVal: " + tftp.getOpCode(packet.getData()));
                			u.printMessage(this.className, methodName, "Error: " + u.arrayToString(tftp.extractError(packet.getData())));
                    		String[] err = tftp.extractError(packet.getData()); //Structure at {Error Code, Error Message}
                    		displayError(err, methodName);
                	}else {
                		u.printMessage(this.className, methodName, "Not an expected packet with opCode: " + tftp.getOpCode(packet));
                	}
            	}while(!validACK && !error); //Repeat if sending if the ACK received is not a 'new' block of data.
            	
            	//RECOMPUTES BLKSIZE IF AVAILABLE DATA IS LESS THAN THE BLKSIZE; DO NOT MOVE THIS. LET IT BE PLACED LAST.
            	if(inputStream.available() < blocksize) {
					blocksize = inputStream.available();
					buffer = new byte[blocksize];
					u.printMessage(this.className, methodName, "blksize adjusted to: " + buffer.length);
				}
			}
			u.printMessage(this.className, methodName, "Closing stream...");
			inputStream.close();
			return true;
		} catch (Exception e) {
			u.printMessage(this.className, methodName, "Exception: " + e.getLocalizedMessage());
			gui.popDialog("Exception occured:\n" + e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		return false; //A fatal error (non-TFTP) occurs.
	}
	
	/**
	 * Read File from server.
	 * File Bytestream Reference: https://www.codejava.net/java-se/file-io/java-io-fileinputstream-and-fileoutputstream-examples
	 * @param filename Filename of target file on server.
	 * @param tempFile File where the bytes will be placed.
	 * @return File pointer with the TFTP bytes.
	 */
	private File readFromServer(String filename, File tempFile, String[] opts, String[] vals) {
		String methodName = "readFromServer()";
		
		if(!socket.isConnected())
			return null;
		
		if(!tempFile.exists())
			try {
				tempFile.createNewFile();
			} catch (Exception e1) {
				gui.popDialog("Error creating new file for the download!", "Error", JOptionPane.ERROR_MESSAGE);
				u.printMessage(this.className, methodName, "Error creating new file for the download: " + e1.getLocalizedMessage());
				return null;
			}
		
		OutputStream outputStream = null; //BYTE STREAM FILE WRITING
		int tsize = this.TSIZE, blocksize = this.BUFFER_SIZE + 4, timeout = -1; //FOR CONFIGURATION
		
		int subtotal = 0;
		try {
			outputStream = new FileOutputStream(tempFile);
			u.printMessage(this.className,methodName,"Receiving file from target...");
			
			int ctr = 0;
			boolean validBlock = false, error = false;
			do {
				do {
					validBlock = false;
					byte[] ackbyte = tftp.getACK(ctr);
					
					//RECOMPUTE BLKCSIZE IF COMPUTED REMAINING BYTES OF FILE IS LESS THAN BUFFER/BLKSIZE; DO NOT MOVE
					if(tsize-subtotal < blocksize) {
						u.printMessage(this.className, methodName, "Adjusting blksize: " + (tsize-subtotal));
						blocksize = tsize-subtotal + 4;
					}
					
					//SEND AN ACK FIRST
					if(ctr == 0) { //TO SYNC WITH FUTURE DATAPACKETS THAT START AT 1	
						packet = new DatagramPacket(ackbyte, ackbyte.length);
						socket.send(packet);
						ctr++;
					}
					
					//AWAIT FOR DATA RESPONSE
					byte[] buffer = new byte[blocksize];
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
					socket.receive(packet);
					
					boolean isData = tftp.isData(packet.getData()), isError = tftp.isError(packet.getData());
	            	u.printMessage(this.className, methodName, "isData: " + isData + ", isError: " + isError);
					if(isData) {
						//SAVE BUFFER TO FILE
						byte[] data = tftp.extractData(packet);
						int block = tftp.extractBlockNumber(packet);
						subtotal += data.length;
						u.printMessage(this.className, methodName, "Received data, ACK#: " + block + ", Curr#: " + ctr + ", DataLen: " + data.length + ", Completed: " + subtotal + "/" + tsize + " (" + u.percentageValue(subtotal,tsize)+ "%)");
						
						if(true/*block == ctr*/) { //CLIENT AND SERVER ARE IN SYNC TODO
							validBlock = true;
							int bytesRead = data.length; //BYTE LENGTH OF PACKET'S DATA SEGMENT
							outputStream.write(data, 0, bytesRead);
						}if(block > ctr) { //SERVER IS ADVANCED THAN CLIENT
							String[] e = {"0", tftp.getErrMsg(0)};
							displayError(e, methodName);
							return null;
						}else{ // SERVER IS LATE THAN CLIENT
							packet = new DatagramPacket(ackbyte, ackbyte.length);
							socket.send(packet);
						}
					}else if(isError){
						u.printMessage(this.className, methodName, "Possible Error @ OPVal: " + tftp.getOpCode(packet.getData()));
            			u.printMessage(this.className, methodName, "Error: " + u.arrayToString(tftp.extractError(packet.getData())));
                		String[] err = tftp.extractError(packet.getData()); //Structure at {Error Code, Error Message}
                		displayError(err, methodName);
                		tempFile.delete();
					}else
            			u.printMessage(this.className, methodName, "Not an expected packet with opCode: " + tftp.getOpCode(packet));
				}while(!validBlock && !error);
				byte[] ackbyte = tftp.getACK(ctr);
				packet = new DatagramPacket(ackbyte, ackbyte.length);
				socket.send(packet);
				u.printMessage(this.className, methodName, "ACK: " + ctr + " sent");
				ctr++;
			}while(subtotal < tsize);
			outputStream.close();
		} catch (Exception e) {
			u.printMessage(this.className, methodName, "Exception: " + e.getLocalizedMessage());
			gui.popDialog("Exception occured:\n" + e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			byte[] errPacket = tftp.getErrPacket(0);
			packet = new DatagramPacket(errPacket, errPacket.length);
			try {
				socket.send(packet);
			} catch (Exception e1) {
				gui.popDialog("Exception occured:\n" + e1.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
			return null;
		}
		return tempFile;
	}
	
	
	/**
	 * ==========================================================
	 * 				AUXILLIARY NETWORK FUNCTIONS
	 * ==========================================================
	 */
	
	public void reset() {
		u.printMessage(this.className, "reset()","");
		this.BUFFER_SIZE = 512;
		this.TSIZE = 0;
	}
	
	/**
	 * Opens the connection of this Client's socket.
	 * @return True if successful, false if otherwise.
	 */
	public boolean openConnection() {
		String methodName = "openConnection()";
		u.printMessage(this.className,methodName, "Opening connection...");
		if(target != null && PORT != -1) {
			try {
				//Attempt connection
				u.printMessage(this.className,methodName, "Creating DatagramSocket()...");
				this.socket = new DatagramSocket(69);
				socket.setSoTimeout(3000);
				socket.connect(this.target, this.PORT);
				u.printMessage(this.className,methodName, "Socket connected!");
				u.printMessage(this.className,methodName, "Socket: " + getConnectionDetails(this.socket));
				//Check if reachable;
				u.printMessage(this.className,methodName, "Checking if target is online: " + targetIsOnline());
			} catch (SocketException e) {
				u.printMessage(this.className,methodName, "Exception: " + e.getLocalizedMessage());
				gui.popDialog("Exception occured:\n" + e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		return this.socket.isConnected();
	}
	
	/**
	 * Closes the connection of this Client's socket.
	 * @return True if closed, false if otherwise.
	 */
	public boolean closeConnection() {
		String methodName = "closeConnection()";
		u.printMessage(this.className, methodName, "Closing connection...");
		this.socket.close();
		u.printMessage(this.className, methodName, "" + socket.isClosed());
		return socket.isClosed();
	}
	
	/**
	 * Checks if the socket is connected or not.
	 * @return True if connected and bound, false if otherwise.
	 */
	public boolean isConnected() {
		String methodName = "isConnected()";
		if(this.socket == null) { //this.socket is null
			u.printMessage(this.className, methodName, "this.socket is null");
			return false;
		}else if(!this.socket.isBound()) { //this.socket is !bound/binded
			u.printMessage(this.className, methodName, "this.socket is not bound");
			return false;
		}else { //check if this.socket is connected
			u.printMessage(this.className, methodName, ""+this.socket.isConnected());
			return this.socket.isConnected();
		}
	}
	
	/**
	 * Checks if the Client's specified target is online.
	 * @return True if online, false if otherwise.
	 */
	public boolean targetIsOnline() {
		if(this.target != null)
			return targetIsOnline(this.target);
		else
			return false;
	}
	
	/**
	 * Checks if the specified target is online
	 * @param target Target to be pinged.
	 * @return True if online, false if otherwise.
	 */
	public boolean targetIsOnline(InetAddress target) {
		String methodName = "targetIsOnline(target)";
		if(target != null) {
			try {
				u.printMessage(this.className, methodName, "Pinging " + target.getHostAddress() + "...");
				if(target.isReachable(this.CheckTimeout)) {
					u.printMessage(this.className, methodName, "target: " + target.getHostAddress() + " is reachable.");
					return true;
				}
			} catch (Exception e) {
				u.printMessage(this.className, methodName, "Exception: " + e.getLocalizedMessage());
				u.printMessage(this.className, methodName, target.getHostAddress() + " is unreachable.");
				gui.popDialog("Exception occured:\n" + e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		u.printMessage(this.className, methodName, "Specified target is cannot be reached");
		return false;
	}
	
	/**
	 * Get the connection details of this Client instance.
	 * @return Connection details of this Client.
	 */
	public String getConnectionDetails() {
		return getConnectionDetails(this.socket);
	}
	
	/**
	 * Displays the error message as a pop-up and a console printout.
	 * @param error Error String[] containing {Error Code, Error Value}
	 * @param methodName Function Caller
	 */
	private void displayError(String[] error, String methodName) {
		gui.popDialog("Error: " + tftp.getErrMsg(Integer.parseInt(error[0])), "Error", JOptionPane.ERROR_MESSAGE);
		u.printMessage(this.className, methodName, "Error: " + tftp.getErrMsg(Integer.parseInt(error[0])));	
	}
	
	/**
	 * Get the connection details of the socket specified.
	 * @param socket Socket to check connection.
	 * @return Connection details of this Client.
	 */
	private String getConnectionDetails(DatagramSocket socket) {
		String methodName = "getConnectionDetails(socket)";
		u.printMessage(this.className, methodName, "Getting socket connection details...");
		if(socket != null)
			return socket.getLocalAddress().getHostAddress() + ":" + socket.getLocalPort() + " <==> " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
		u.printMessage(this.className, methodName, "Socket is null.");
		return null;
	}
	
	/**
	 * Force send packet over the network
	 * @param packet
	 * @param target Custom target
	 * @param port
	 */
	public void forceSend(DatagramPacket packet, InetAddress target, Integer port) {
		String methodName = "forceSend(packet,target,port)";
		if(target == null || port == null) {
			
		}else {
			try {
				if(isConnected()) {
					u.printMessage(this.className, methodName, "Socket already in use: " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort()  + ", saving current state...");
					InetAddress tempTarget = socket.getInetAddress();
					int tempPort = socket.getPort();
					
					u.printMessage(this.className, methodName, "Closing current connection...");
					closeConnection();
					
					u.printMessage(this.className, methodName, "Opening temporary connection...");
					socket = new DatagramSocket(port);
					socket.connect(target,port);
					
					u.printMessage(this.className, methodName, "Force sending packet...");
					socket.send(packet);
					u.printMessage(this.className, methodName, "Packet sent!");
					
					u.printMessage(this.className, methodName, "Restoring original connection...");
					socket.connect(tempTarget, tempPort);
				}else {
					u.printMessage(this.className, methodName, "Socket not connected, creating connection...");
					socket = new DatagramSocket();
					socket.connect(target, port);
					if(socket.isConnected()) {
						u.printMessage(this.className, methodName, "Socket: " + getConnectionDetails());
						u.printMessage(this.className, methodName, "Force sending packet...");
						socket.send(packet);
						u.printMessage(this.className, methodName, "Packet sent!");
					}else
						u.printMessage(this.className, methodName, "Cannot connect to newly created socket!");
					u.printMessage(this.className, methodName, "Closing connection...");
					socket.close();
				}
			}catch(Exception e) {
				u.printMessage(this.className, methodName, "Error occured: " + e.getLocalizedMessage());
				socket.close();
			}
		}
	}
}