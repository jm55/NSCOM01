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
	private int PORT = -1, BUFFER_SIZE = 512;
	private byte[] buffer = null;
	private InetAddress target = null;
	private final int CheckTimeout = 5000; //NETWORK TARGET CHECKING, NOT RELATED TO TFTP
	private TFTP tftp = new TFTP();
	private GUI gui = new GUI();
	
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
		u.printMessage(this.className, "Client(host,port)", "Building Client as " + host + ":" + port + "...");
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
			u.printMessage(this.className, "Client(host,port,BUFFER_SIZE)", "Building Client as " + host + ":" + port + " failed.");
		else 
			u.printMessage(this.className, "Client(host,port,BUFFER_SIZE)", "Building Client as " + host + ":" + port + " successful.");
	}
	
	public Client(InetAddress target, int port, int BUFFER_SIZE) {
		this.target = target;
		this.PORT = port;
		this.BUFFER_SIZE = BUFFER_SIZE;
		u.printMessage(this.className, "Client(host,port)", "Connection to " + this.target.getHostAddress() + ":" + port + "=" + isConnected());
	}
	
	/**
	 * Sets the Client's default connection target to: 'localhost:69' for a TFTP connection.
	 */
	public void setDefaults() {
		try {
			this.target = InetAddress.getByName("localhost"); //Default target as localhost
			this.PORT = 69; //Default TFTP port
		} catch (UnknownHostException e) {
			target = null;
			this.PORT = -1;
			u.printMessage(this.className, "Client()", "TryCatch: " + e.getLocalizedMessage());
		}
	}
	
	/**
	 * Sets the BUFFER SIZE of the TFTP transmission.
	 * @param BUFFER_SIZE
	 */
	public void setBufferSize(int BUFFER_SIZE) {
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
		boolean state = false;
		if(f == null)
			return state;
		u.printMessage(this.className, "send(File, String[], String[])", "Connecting...");
		openConnection();
		if(f.exists() && socket.isConnected()) {
			u.printMessage(this.className, "send(File, String[], String[])", "File exists and client is connected");
			if(askWritePermission(f, opts, vals)) {
				u.printMessage(this.className, "send(File, String[], String[])", "Write Permission Accepted!");
				state = writeToServer(f, opts, vals);
			}else {
				u.printMessage(this.className, "send(File, String[], String[])", "Write Permission Failed!");
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
		
		int tsize = askReadPermission(filename, opts, vals);
		if(tsize > -1) {
			openConnection();
			tempFile = readFromServer(filename, tempFile, opts, vals);
			closeConnection();
		}
		reset();
		return tempFile;
	}
	
	/**
	 * Ask permission to write file on the server connected to this Client.
	 * @param f File to write permission to.
	 * @return True if allowed, false if otherwise.
	 */
	private boolean askWritePermission(File f, String[] opts, String[] vals) {
		if(!isConnected() || f == null)
			return false;
		u.printMessage(this.className, "askWritePermission(File f, String[] opts, String[] vals)", "Building write request packet...");
		String mode = "octet";
		byte[] wrq = tftp.getWRQPacket(f, mode, opts, vals);
		packet = new DatagramPacket(wrq, wrq.length);
		try {
			//Send packet to serve
			u.printMessage(this.className, "askWritePermission(File f, String[] opts, String[] vals)", "Sending packet from " + socket.getLocalPort() + " to " + socket.getRemoteSocketAddress() + "...");
			socket.connect(target, this.PORT); //USE 'CONTROL' SOCKET OF TFTP
			socket.send(packet);
			
			//Receive ACK or ERROR packet
			u.printMessage(this.className, "askWritePermission(File f, String[] opts, String[] vals)", "Receiving OACK packet to: " + socket.getLocalPort() + "...");
			packet = new DatagramPacket(new byte[512], 512); //LIMITED TO 512 AS THE RFC DOCUMENT REVEALS THAT ANY REQUEST IS LIMITED TO 512 OCTETS, IMPLYING THAT ANY OACK MAY BE THE SAME.
			socket.connect(this.target, this.DATAPORT); //SWITCH OVER PACKET TO SPECIFIED DATAPORT AND NOT TO PORT 69
			socket.receive(packet);
			
			//Trim excess bytes from packets
			u.printMessage(this.className, "askWritePermission(File f, String[] opts, String[] vals)", "Trimming OACK packet...");
			byte[] trimmedPacket = new byte[packet.getLength()]; //TRIMMED RCV
			System.arraycopy(packet.getData(), packet.getOffset(), trimmedPacket, 0, packet.getLength());

			//Confirm that the packet is an OACK and not an Error
			if(tftp.isOACK(trimmedPacket) && !tftp.isError(trimmedPacket)){
				u.printMessage(this.className, "askWritePermission(File f, String[] opts, String[] vals)", "isOACK and !isError");
				String[][] checking = tftp.extractOACK(trimmedPacket);
				
				int match = 0;	
				u.printMessage(this.className, "askWritePermission(File f, String[] opts, String[] vals)", "Checking matches...");
				u.printMessage(this.className, "askWritePermission(File f, String[] opts, String[] vals)", "Sent opts: " + u.arrayToString(opts));
				u.printMessage(this.className, "askWritePermission(File f, String[] opts, String[] vals)", "Sent vals: " + u .arrayToString(vals));
				u.printMessage(this.className, "askWritePermission(File f, String[] opts, String[] vals)", "OACK opts: " + u.arrayToString(checking[0]));
				u.printMessage(this.className, "askWritePermission(File f, String[] opts, String[] vals)", "OACK vals: " + u .arrayToString(checking[1]));
				
				//Revamped design which now considers the insisted blksize of the TFTP server as noted on a Wireshark test.
				for(int i = 0; i < vals.length; i++) {
					for(int j = 0;  j < checking[0].length; j++) {
						if(opts[i].equalsIgnoreCase(checking[0][j])) {
							if(checking[0][j].equalsIgnoreCase("blksize")){ //Check if TFTP asserts a blksize
								this.BUFFER_SIZE = Integer.parseInt(checking[1][j]);
								u.printMessage(this.className, "askWritePermission(File f, String[] opts, String[] vals)", "Server's blksize: " + this.BUFFER_SIZE);
							}
							match++;
						}
					}
				}		
				
				if(match == vals.length)
					return true;
			}else{
				//Confirm that the packet is an Error
				if(tftp.isError(trimmedPacket)){
					String[] error = tftp.extractError(trimmedPacket);
					if(error[0] == "1") //FILE NOT FOUND
						gui.popDialog("Error: File Not Found", "Error", JOptionPane.ERROR_MESSAGE);
					else if(error[0] == "2") //ACCESS VIOLATION
						gui.popDialog("Error: Access Violation", "Error", JOptionPane.ERROR_MESSAGE);
					else if(error[0] == "3") //DISK FULL
						gui.popDialog("Error: Disk Full", "Error", JOptionPane.ERROR_MESSAGE);
					else //FATAL ERROR (AS PER SPECIFICATION)
						gui.popDialog("Error: Fatal Error Occurred", "Error", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
		}catch (IOException e) {
			gui.popDialog("IOException occured:\n" + e.getLocalizedMessage(),"Error", JOptionPane.ERROR_MESSAGE);
			u.printMessage(this.className, "askWritePermission(*)", "IOException: " + e.getLocalizedMessage());
		}
		return false;
	}
	
	/**
	 * TODO
	 * Ask permission to read file on the server connected to this Client.
	 * @param filename Filename of the File requested.
	 * @return True if allowed/possible, false if otherwise.
	 */
	private int askReadPermission(String filename, String[] opts, String[] vals) {
		if(!isConnected() || filename == null || filename.length() == 0)
			return -1;
		/**
		 * BUILD READ REQUEST via TFTP.getRRQPacket();
		 * WHERE OPTSVALS ARE:
		 * String[] OPTS = {tsize}
		 * String[] VALS = {0}
		 * BLOCKSIZE OPTION:
		 * OPTIONAL TO INCLUDE OPTS = {'blksize'} and VALS {vals[blksize]} (SET BY USER)
		 * IF BLOCK_SIZE != 512, BUT CONSIDER AS LOW PRIORITY.
		 * IF CONDUCTING socket.send(<packet>), DO THIS:
		 * socket.connect(target, this.PORT); //USE 'CONTROL' SOCKET OF TFTP
		 * socket.send(packet);
		 * IF CONDUCTING socket.receive(<packet>, DO THIS:
		 * socket.connect(this.target, this.DATAPORT); //SWITCH OVER PACKET TO SPECIFIED DATAPORT (61001) AND NOT TO PORT 69
		 * socket.receive(packet);
		 * IF ERROR WAS RECEIVED RETURN -1
		 * ELSE CHECK OACK AND CONFIRM IF VALS SET WAS WHAT THE OACK CONTAINS (IF IT EVEN EXISTS)
		 * AND RETURN VALS OF OPTS-tsize.
		 */
		return 0; //Modify freely when needed.
	}
	
	/**
	 * Write file to the server.
	 * File Bytestream Reference: https://www.codejava.net/java-se/file-io/java-io-fileinputstream-and-fileoutputstream-examples
	 * @param f File to be transferred.
	 * @return True if transfer completed, false if otherwise or fatal error/exception occurred.
	 */
	private boolean writeToServer(File f, String[] opts, String[] vals) {
		u.printMessage(this.className, "writeToServer(File)", "f.exists()...");
		
		try {
			Integer BUFFER_SIZE = 512; //BUFFER/BLOCKSIZE DEFAULT TO 512
			Integer SIZE = (int)Files.size(f.toPath()); //SIZE OF FILE
			Integer bytesRead = -1; //FOR FILE STREAMING
			Integer BLOCKCOUNT = 0; //NUMBER OF BLOCKS TO BE TRANSMITTED.
			
			InputStream inputStream = new FileInputStream(f.getAbsolutePath()); //FILE STREAMING
			
			int tsize = -1, blocksize = -1, timeout = -1; //FOR CONFIGURATION BY USER
			if(tftp.validOptVal(opts, vals)) {
				for(int i = 0; i < opts.length; i++) {
					if(opts[i].equals("tsize"))
						tsize = Integer.parseInt(vals[i]);
					if(opts[i].equals("blksize"))
						blocksize = this.BUFFER_SIZE;
					if(opts[i].equals("timeout"))
						timeout = Integer.parseInt(vals[i]);
				}
			}
			
			//BLOCKCOUNT
			if(blocksize != -1)
				BUFFER_SIZE = blocksize;
			double blockcountD = SIZE/BUFFER_SIZE;
			BLOCKCOUNT = (int)Math.ceil(blockcountD);
			
			//BUFFER BYTE[] CONFIGURATION
			u.printMessage(this.className, "writeToServer()", "SIZE: " + SIZE + ", " + "BUFFER_SIZE: " + BUFFER_SIZE);
			byte[] buffer = new byte[BUFFER_SIZE]; //DATA SEGMENT OF PACKET
            if(SIZE < BUFFER_SIZE) //IF FILE SIZE IS INITIALLY SMALLER THAN BUFFER SIZE THEN SET BUFFER TO JUST FILE'S SIZE
            	buffer = new byte[SIZE];
            
            u.printMessage(this.className, "writeToServer(File)", "Reading through f and transmitting to target...");            
            int ctr = 1; //COUNTER FOR BLOCK#
            while((bytesRead = inputStream.read(buffer)) > 0) { //While file not done streaming.
            	//Sending byte of file
            	u.printMessage(this.className, "writeToServer()", "Sending part of file...");
            	socket.connect(this.target, this.DATAPORT); //WHEN SENDING
            	byte[] packetByte = tftp.getDataPacket(ctr, buffer); //BUILD A DATA TFTP PACKET
            	packet = new DatagramPacket(packetByte,packetByte.length);
            	socket.send(packet);
            	
            	//Await for response
            	u.printMessage(this.className, "writeToServer()", "Awaiting for response...");
            	//socket.connect(this.target, this.DATAPORT); //WHEN RECEIVING
            	packet = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
            	socket.receive(packet);
            	
            	if(tftp.isACK(packet.getData()) && !tftp.isError(packet.getData())) {
            		//u.printMessage(this.className, "writeToServer()", "isACK && !isError");
            		u.printMessage(this.className, "writeToServer()","ACK Block#: " + tftp.extractACK(packet.getData()));
            		/**
            		 * Not a necessary TODO but can help on error management. Specifically ACK handling.
            		 * TODO (?): Verify the ACK value that the packet has. 
            		 * 			 Current issue being, on tftp.extractACK, that it reads it in an overflow once the values
            		 * 			 are >= 128.
            		 */
            		if(true) //Change to comparing received block number in ACK to ctr
            			ctr++;
            	}else {
            		u.printMessage(this.className, "writeToServer()", "Possible Error @ OPVal: " + tftp.getOpCode(packet.getData()));
            		u.printMessage(this.className, "writeToServer()", "Error: " + u.arrayToString(tftp.extractError(packet.getData())));
            		String[] err = tftp.extractError(packet.getData());
            		/**
            		 * TODO:
            		 * HANDLE ERRORS HERE
            		 * SUGGESTED TO FOLLOW THE ERROR HANDLING ON askWritePermission();
            		 */
            	}
            	//DO NOT MOVE THIS. LET IT BE PLACED LAST.
            	u.printMessage(this.className, "writeToServer()", "Remaining bytes: " + inputStream.available());
            	if(inputStream.available() < BUFFER_SIZE) {
            		u.printMessage(this.className, "writeToServer()", "Adjusting buffer size...");
					BUFFER_SIZE = inputStream.available();
					buffer = new byte[BUFFER_SIZE];
					u.printMessage(this.className, "writeToServer()", "Buffersize adjusted to: " + buffer.length);
				}
			}
			u.printMessage(this.className, "writeToServer(File)", "Closing stream...");
			inputStream.close();
			return true;
		} catch (IOException | NullPointerException e) {
			u.printMessage(this.className, "writeToServer(File)", "Exception: " + e.getLocalizedMessage());
			gui.popDialog("Error", "Exception occured!", JOptionPane.ERROR_MESSAGE);
		}
		return false; //A fatal error (non-TFTP) occurs.
	}
	
	/**
	 * TODO
	 * Read File from server.
	 * File Bytestream Reference: https://www.codejava.net/java-se/file-io/java-io-fileinputstream-and-fileoutputstream-examples
	 * @param filename Filename of target file on server.
	 * @param tempFile File where the bytes will be placed.
	 * @return File pointer with the TFTP bytes.
	 */
	private File readFromServer(String filename, File tempFile, String[] opts, String[] vals) {
		if(tempFile == null)
			return null;
		if(!tempFile.exists())
			return null;
		OutputStream outputStream = null; //BYTE STREAM FILE WRITING
		int tsize = -1, blocksize = -1, timeout = -1; //FOR CONFIGURATION
		if(tftp.validOptVal(opts, vals)) {
			for(int i = 0; i < opts.length; i++) {
				if(opts[i].equals("tsize"))
					tsize = Integer.parseInt(vals[i]);
				if(opts[i].equals("blocksize"))
					blocksize = Integer.parseInt(vals[i]);
				if(opts[i].equals("timeout"))
					timeout = Integer.parseInt(vals[i]);
			}
		}
		if(blocksize != -1)
			this.BUFFER_SIZE = blocksize;
		try {
			outputStream = new FileOutputStream(tempFile);
			do {
				/***
		    	 * TODO: RECEIVE BYTES HERE
		    	 * EACH DATA BYTES ARE HELD IN buffer AND THE LENGTH IS 0-bytesRead 
		    	 * IT IS A MATTER OF SWITCHING BETWEEN SOCKET.RECEIVE AND SOCKET.SEND
		    	 * WITH THE FIRST (ODD NUMBERED) ACTION BEING RECEIVE DATA AND SEND ACKS
		    	 * COPY BLOCK# OF DATA RECEIVED AS ACK
		    	 * WATCH OUT FOR ERROR(!) AND ACKS
		    	 * ERRORS TO WATCH OUT FOR: 
            	 * 1. Timeout for unresponsive server
            	 * 2. Handling of duplicate ACK
            	 * 3. User prompt for file not found, access violation, and disk full errors
		    	 */
				int bytesRead = 0; //BYTE LENGTH OF PACKET'S DATA SEGMENT
				outputStream.write(buffer, 0, bytesRead);
			}while(false);
			outputStream.close();
		} catch (IOException e) {
			u.printMessage(this.className, "readFromServer(String, File, String[], String[]", "Exception: " + e.getLocalizedMessage());
			gui.popDialog("Error", "Exception occured!", JOptionPane.ERROR_MESSAGE);
		}
		return tempFile;
	}
	
	/**
	 * ==========================================================
		 * AUXILLIARY NETWORK FUNCTIONS
		 * ==========================================================
	 */
	
	public boolean sendScratch(byte[] sample) {
		if(socket.isConnected()){
			try {
				DatagramPacket p = new DatagramPacket(sample, sample.length);
				socket.send(p);
				return true;
			} catch (IOException e) {
				u.printMessage(this.className,"sendScratch(byte[])",e.getLocalizedMessage());
				gui.popDialog("Error", "Exception occured!", JOptionPane.ERROR_MESSAGE);
			}
		}
		return false;
	}
	
	public void reset() {
		this.BUFFER_SIZE = 512;
	}
	
	/**
	 * Opens the connection of this Client's socket.
	 * @return True if successful, false if otherwise.
	 */
	public boolean openConnection() {
		if(target != null && PORT != -1) {
			try {
				//Attempt connection
				u.printMessage(this.className,"openConnection()", "Creating DatagramSocket()...");
				this.socket = new DatagramSocket(61000);
				socket.connect(this.target, this.PORT);
				u.printMessage(this.className,"openConnection()", "Socket connected!");
				u.printMessage(this.className,"openConnection()", "Socket: " + getConnectionDetails(this.socket));
				//Check if reachable;
				u.printMessage(this.className,"openConnection()", "Checking if target is online: " + targetIsOnline());
			} catch (SocketException e) {
				u.printMessage(this.className,"openConnection()", "Exception: " + e.getLocalizedMessage());
				gui.popDialog("Error", "Exception occured!", JOptionPane.ERROR_MESSAGE);
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
		u.printMessage(this.className, "closeConnection()", "Closing connection...");
		this.socket.close();
		u.printMessage(this.className, "closeConnection()", "" + socket.isClosed());
		return socket.isClosed();
	}
	
	/**
	 * Checks if the socket is connected or not.
	 * @return True if connected and bound, false if otherwise.
	 */
	public boolean isConnected() {
		if(this.socket == null) { //this.socket is null
			u.printMessage(this.className, "isConnected()", "this.socket is null");
			return false;
		}else if(!this.socket.isBound()) { //this.socket is !bound/binded
			u.printMessage(this.className, "isConnected()", "this.socket is not bound");
			return false;
		}else { //check if this.socket is connected
			u.printMessage(this.className, "isConnected()", ""+this.socket.isConnected());
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
		if(target != null) {
			try {
				u.printMessage(this.className, "targetIsOnline(target)", "Pinging " + target.getHostAddress() + "...");
				if(target.isReachable(this.CheckTimeout)) {
					u.printMessage(this.className, "targetIsOnline()", "target: " + target.getHostAddress() + " is reachable.");
					return true;
				}
			} catch (IOException e) {
				u.printMessage(this.className, "targetIsOnline()", "Exception: " + e.getLocalizedMessage());
				u.printMessage(this.className, "targetIsOnline()", target.getHostAddress() + " is unreachable.");
				gui.popDialog("Error", "Exception occured!", JOptionPane.ERROR_MESSAGE);
			}
		}
		u.printMessage(this.className, "targetIsOnline(target)", "Specified target is cannot be reached");
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
	 * Get the connection details of the socket specified.
	 * @param socket Socket to check connection.
	 * @return Connection details of this Client.
	 */
	private String getConnectionDetails(DatagramSocket socket) {
		u.printMessage(this.className, "getConnectionDetails(DatagramSocket)", "Getting socket connection details...");
		if(socket != null)
			return socket.getLocalAddress().getHostAddress() + ":" + socket.getLocalPort() + " <==> " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
		u.printMessage(this.className, "getConnectionDetails(DatagramSocket)", "Socket is null.");
		return null;
	}
}