package network;

import java.io.*;
import java.net.*;
import java.nio.file.Files;

import data.*;
import utils.*;

public class Client {
	private Utility u = new Utility();
	private final String className = "Client";
	
	private DatagramSocket socket = null;
	private DatagramPacket packet = null;
	private int PORT = -1, BUFFER_SIZE = 512;
	private byte[] buffer = null;
	private InetAddress target = null;
	private final int CheckTimeout = 5000; //NETWORK TARGET CHECKING, NOT RELATED TO TFTP
	private TFTP tftp = new TFTP();
	
	public Client() {
		setDefaults();
	}
	
	public Client(String host, int port, int BUFFER_SIZE) {
		u.printMessage(this.className, "Client(host,port)", "Building Client as " + host + ":" + port + "...");
		try {
			this.target = InetAddress.getByName(host);
			this.PORT = port;
			this.BUFFER_SIZE = BUFFER_SIZE;
		} catch (UnknownHostException e) {
			target = null;
			this.PORT = -1;
			u.printMessage(this.className, "Client(host,port)", "TryCatch: " + e.getLocalizedMessage());
		}
		if(this.target == null)
			u.printMessage(this.className, "Client(host,port)", "Building Client as " + host + ":" + port + " failed.");
		else 
			u.printMessage(this.className, "Client(host,port)", "Building Client as " + host + ":" + port + " successful.");
		u.printMessage(this.className, "Client(host,port)", "Connection to " + host + ":" + port + "=" + isConnected());
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
		openConnection();
		if(f.exists() && socket.isConnected())
			if(askWritePermission(f, opts, vals))
				state = writeToServer(f, opts, vals);
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
	 * TODO
	 * Ask permission to write file on the server connected to this Client.
	 * @param f File to write permission to.
	 * @return True if allowed, false if otherwise.
	 */
	private boolean askWritePermission(File f, String[] opts, String[] vals) {
		if(!isConnected() || f == null)
			return false;
		/**
		 * 
		 * BUILD WRITE REQUEST via TFTP.getWRQPacket();
		 * 
		 * WHERE OPTSVALS ARE:
		 * String[] OPTS = {'tsize'}
		 * String[] VALS = {filesize of file in bytes}
		 * 
		 * BLOCKSIZE OPTION:
		 * OPTIONAL TO INCLUDE OPTS = {'blocksize'} and VALS {BLOCK_SIZE} (SET BY USER)
		 * IF BLOCK_SIZE != 512, BUT CONSIDER AS LOW PRIORITY.
		 * 
		 * TIMEOUT OPTION: 
		 * OPTIONAL TO INCLUDE OPTS = {'timeout'} and VALS {vals['timeout']} (SET BY USER/SYSTEM CONFIG)
		 * 
		 * IF ERROR WAS RECEIVED RETURN FALSE
		 * ELSE CHECK OACK AND CONFIRM IF VALS SET WAS WHAT THE OACK CONTAINS
		 */		
		return true; //Modify freely when needed.
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
		 * 
		 * BUILD READ REQUEST via TFTP.getRRQPacket();
		 * 
		 * WHERE OPTSVALS ARE:
		 * String[] OPTS = {tsize}
		 * String[] VALS = {0}
		 * 
		 * BLOCKSIZE OPTION:
		 * OPTIONAL TO INCLUDE OPTS = {'blocksize'} and VALS {vals[blocksize]} (SET BY USER)
		 * IF BLOCK_SIZE != 512, BUT CONSIDER AS LOW PRIORITY.
		 * 
		 * TIMEOUT OPTION: 
		 * OPTIONAL TO INCLUDE OPTS = {'timeout'} and VALS {vals['timeout']} (SET BY USER/SYSTEM CONFIG)
		 * 
		 * IF ERROR WAS RECEIVED RETURN -1
		 * ELSE CHECK OACK AND CONFIRM IF VALS SET WAS WHAT THE OACK CONTAINS (IF IT EVEN EXISTS)
		 * AND RETURN VALS OF OPTS-tsize.
		 */	
		return 0; //Modify freely when needed.
	}
	
	/**
	 * TODO
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
					if(opts[i].equals("blocksize"))
						blocksize = Integer.parseInt(vals[i]);
					if(opts[i].equals("timeout"))
						timeout = Integer.parseInt(vals[i]);
				}
			}
			
			//BLOCKCOUNT
			if(blocksize != -1)
				this.BUFFER_SIZE = blocksize;
			double blockcountD = SIZE/BUFFER_SIZE;
			BLOCKCOUNT = (int)Math.ceil(blockcountD);
			
			//BUFFER BYTE[] CONFIGURATION
			byte[] buffer = new byte[BUFFER_SIZE]; //DATA SEGMENT OF PACKET
            if(SIZE < BUFFER_SIZE) //IF FILE SIZE IS INITIALLY SMALLER THAN BUFFER SIZE THEN SET BUFFER TO JUST FILE'S SIZE
            	buffer = new byte[SIZE];
            
            u.printMessage(this.className, "writeToServer(File)", "Reading through f and transmitting to target...");            
            int ctr = 1; //COUNTER FOR BLOCK#
            while((bytesRead = inputStream.read(buffer)) != -1) { //While file not done streaming.
            	/***
            	 * 
            	 * TODO: SEND BYTES HERE
            	 * EACH DATA BYTES ARE HELD IN buffer AND THE LENGTH IS 0-bytesRead 
            	 * 
            	 * IT IS A MATTER OF SWITCHING BETWEEN SOCKET.SEND AND SOCKET.RECEIVE
            	 * WITH THE FIRST (ODD NUMBERED) ACTION BEING SEND DATA AND RECEIVE ACKS
            	 * 
            	 * INCREMENT CTR FOR EVER ACK RECEIVED (AND IF ACK'S BLOCK# EQUAL TO CTR)
            	 * 
            	 * WATCH OUT FOR ERROR(!) AND ACKS
            	 * 
            	 * ERRORS TO WATCH OUT FOR: 
            	 * 1. Timeout for unresponsive server
            	 * 2. Handling of duplicate ACK
            	 * 3. User prompt for file not found, access violation, and disk full errors
            	 */
            	//DO NOT MOVE THIS. LET IT BE PLACED LAST.
            	if(inputStream.available() < BUFFER_SIZE) {
					BUFFER_SIZE = inputStream.available();
					buffer = new byte[BUFFER_SIZE];
				}
			}
			u.printMessage(this.className, "writeToServer(File)", "Closing stream...");
			inputStream.close();
			return true;
		} catch (IOException e) {
			u.printMessage(this.className, "writeToServer(File)", "IOException: " + e.getLocalizedMessage());
		} catch (NullPointerException e) {
			u.printMessage(this.className, "writeToServer(File)", "NullPointerException: " + e.getLocalizedMessage());
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
		    	 * 
		    	 * TODO: RECEIVE BYTES HERE
		    	 * EACH DATA BYTES ARE HELD IN buffer AND THE LENGTH IS 0-bytesRead 
		    	 * 
		    	 * IT IS A MATTER OF SWITCHING BETWEEN SOCKET.RECEIVE AND SOCKET.SEND
		    	 * WITH THE FIRST (ODD NUMBERED) ACTION BEING RECEIVE DATA AND SEND ACKS
		    	 * 
		    	 * COPY BLOCK# OF DATA RECEIVED AS ACK
		    	 * 
		    	 * WATCH OUT FOR ERROR(!) AND ACKS
		    	 * 
		    	 * ERRORS TO WATCH OUT FOR: 
            	 * 1. Timeout for unresponsive server
            	 * 2. Handling of duplicate ACK
            	 * 3. User prompt for file not found, access violation, and disk full errors
		    	 */
				int bytesRead = 0; //BYTE LENGTH OF PACKET'S DATA SEGMENT
				outputStream.write(buffer, 0, bytesRead);
			}while(false);
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tempFile;
	}
	
	/**
	 * ==========================================================
	 * 
	 * AUXILLIARY NETWORK FUNCTIONS
	 * 
	 * ==========================================================
	 */
	
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
				this.socket = new DatagramSocket();
				socket.connect(this.target, this.PORT);
				u.printMessage(this.className,"openConnection()", "Socket connected!");
				u.printMessage(this.className,"openConnection()", "Socket: " + getConnectionDetails(this.socket));
				//Check if reachable;
				u.printMessage(this.className,"openConnection()", "Checking if target is online: " + targetIsOnline());
			} catch (SocketException e) {
				u.printMessage(this.className,"openConnection()", "TryCatch: Creating connection failed.");
				u.printMessage(this.className, "openConnection()", "TryCatch: " + e.getLocalizedMessage());
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
				u.printMessage(this.className, "targetIsOnline()", "target: " + target.getHostAddress() + " is unreachable.");
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