package network;

import java.io.*;
import java.net.*;

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
	private final int CheckTimeout = 5000;
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
	 * @return True if successful, false if an valid/acceptable error occurs.
	 */
	public boolean send(File f, String[] opts, String[] vals) {
		boolean state = false;
		if(f == null)
			return state;
		openConnection();
		if(f.exists() && socket.isConnected())
			if(askWritePermission(f, opts, vals))
				state = writeToServer(f);
		closeConnection();
		return state;
	}
	
	/**
	 * Delegates receiving of File to TFTP server connected by socket using the TFTP protocol instructions.
	 * @param filename Filename of the file intended.
	 * @return Tempfile pointed at /downloads in program's folder.
	 */
	public File receive(String filename, String[] opts, String[] vals) {
		if(filename == null)
			return null;
		File tempFile = new File(u.getTempOutPath(filename)); //To save on a temp folder of the program.
		
		int tsize = askReadPermission(filename, opts, vals);
		if(tsize > -1) {
			openConnection();
			tempFile = readFromServer(filename, tempFile);
			closeConnection();
		}
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
		 * BLOCKSIZE OPTION (MUST COME FIRST BEFORE TSIZE):
		 * OPTIONAL TO INCLUDE OPTS = {'blocksize'} and VALS {BLOCK_SIZE} (SET BY USER)
		 * IF BLOCK_SIZE != 512, BUT CONSIDER AS LOW PRIORITY.
		 * 
		 * IF ERROR WAS RECEIVED RETURN FALSE
		 * ELSE CHECK OACK AND CONFIRM IF VALS SET WAS WHAT THE OACK CONTAINS
		 * 
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
		 * BLOCKSIZE OPTION (MUST COME FIRST BEFORE TSIZE):
		 * OPTIONAL TO INCLUDE OPTS = {'blocksize'} and VALS {BLOCK_SIZE} (SET BY USER)
		 * IF BLOCK_SIZE != 512, BUT CONSIDER AS LOW PRIORITY.
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
	 * @param f File to be transferred.
	 * @return True if transfer completed, false if otherwise or fatal error/exception occurred.
	 */
	private boolean writeToServer(File f) {
		u.printMessage(this.className, "writeToServer(File)", "f.exists()...");
		try {
			int blocksize = 0;
			/**
			 * TODO: COMPUTE FOR BLOCKSIZE (FILE_SIZE/BUFFER_SIZE);
			 */
			
			InputStream inputStream = new FileInputStream(f.getAbsolutePath());
			Integer BUFFER_SIZE = 512, SIZE = inputStream.available(), bytesRead = -1;
			byte[] buffer = new byte[BUFFER_SIZE];
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
            	 */
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
	 * @param filename Filename of target file on server.
	 * @param tempFile File where the bytes will be placed.
	 * @return File pointer with the TFTP bytes.
	 */
	private File readFromServer(String filename, File tempFile) {
		if(tempFile == null)
			return null;
		if(!tempFile.exists())
			return null;
		OutputStream outputStream = null; //BYTE STREAM FILE WRITING
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
		    	 */
				int bytesRead = 0; //BYTE LENGTH OF PACKET'S DATA SEGMENT
				if(bytesRead != -1)
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