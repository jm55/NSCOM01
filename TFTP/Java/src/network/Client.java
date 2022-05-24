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
		
	}
	
	public Client(String host, int port) {
		u.printMessage(this.className, "Client(host,port)", "Building Client as " + host + ":" + port + "...");
		try {
			this.target = InetAddress.getByName(host);
			this.PORT = port;
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
	
	public Client(InetAddress target, int port) {
		this.target = target;
		this.PORT = port;
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
	 * Delegates sending of File to TFTP server connected by socket using the TFTP protocol instructions.
	 * @param f File to be sent.
	 * @return True if successful, false if an valid/acceptable error occurs.
	 */
	public boolean send(File f) {
		if(f == null)
			return false;
		if(f.exists() && socket.isConnected())
			if(askWritePermission(f))
				return writeToServer(f);
		return false;
	}
	
	/**
	 * TODO
	 * Write file to the server.
	 * @param f File to be transferred.
	 * @return True if transfer completed, false if otherwise or fatal error/exception occured.
	 */
	private boolean writeToServer(File f) {
		u.printMessage(this.className, "writeToServer(File)", "f.exists()...");
		try {
			InputStream inputStream = new FileInputStream(f.getAbsolutePath());
			Integer BUFFER_SIZE = 512, SIZE = inputStream.available(), bytesRead = -1;
			byte[] buffer = new byte[BUFFER_SIZE];
            if(SIZE < BUFFER_SIZE)
            	buffer = new byte[SIZE];
            int ctr = 0;
            boolean isTerminating = false;
            DatagramPacket serverReplyPacket = null;
            u.printMessage(this.className, "writeToServer(File)", "Reading through f and transmitting to target...");
			while((bytesRead = inputStream.read(buffer)) != -1) { //While file not done streaming.
				do{
					u.writeMonitor(this.className,"writeToServer(File)", bytesRead, inputStream.available(), 2500); //DO NOT DELETE, FOR MONITORING PURPOSES.
					/**
					 * PROCESS PSEUDOCODE:
					 * 1. BUILD A PACKET FROM TFTP().getDataPacket(ctr,buffer) (THOUGH CONVERT BYTE[] => DATAGRAMPACKET).
					 * 2. SEND DATA PACKET THEN INCREMENT ctr.
					 * 3. LISTEN TO SOCKET FOR ANY ACK OR ERROR. PLACE RECEIVED VALUE TO serverReplyPacket.
					 * 4. CHECK FOR SOCKET IF ACK OR ERROR. RECOMMEND TO USE TFTP().isError(packet) and TFTP().isACK(packet)
					 * 5. 
					 * 
					 * Note:	Suggested to be done in a loop where the ending condition is if the ACK Block# equals to the Packet Block.
					 * 			Refer to RFC Files for a much clearer instructions.
					 */
					serverReplyPacket = null; //Place the output of socket.receive() here.
				}while(isTerminating || !tftp.isError(serverReplyPacket));
				//Check if remaining file bytes is <512. Resize buffer accordingly if so.
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
	 * Ask permission to write file on the server connected to this Client.
	 * @param f File to write permission to.
	 * @return True if allowed, false if otherwise.
	 */
	private boolean askWritePermission(File f) {
		if(!isConnected() || f == null)
			return false;
		/**
		 * Ask for permission to write and await for ACK.
		 * 
		 * If ACK grants writing, return true.
		 * If ACK is ERROR such that it does not grant writing or timeout has been reached return false.
		 * 
		 * Recommended to use TFTP for packet building and checking for both in and outgoing packets.
		 */
		return true; //Modify freely when needed.
	}
	
	/**
	 * TODO
	 * Ask permission to read file on the server connected to this Client.
	 * @param filename Filename of the File requested.
	 * @return True if allowed/possible, false if otherwise.
	 */
	private boolean askReadPermission(String filename) {
		if(!isConnected() || filename == null || filename.length() == 0)
			return false;
		/**
		 * Ask for permission to read and await for ACK.
		 * 
		 * If ACK grants reading, return true.
		 * If ACK is ERROR such that it does not grant reading, file does not exists, or timeout has been reached return false.
		 * 
		 * Recommended to use TFTP for packet building and checking for both in and outgoing packets.
		 */
		return true; //Modify freely when needed.
	}
	
	/**
	 * TODO
	 * Receive the File pointed by filename from server.
	 * @param filename Filename of the file intended.
	 * @return
	 */
	public File receive(String filename) {
		if(filename == null)
			return null;
		File tempFile = new File(u.getTempOutPath(filename)); //To save on a temp folder of the program.
		if(askReadPermission(filename)) {
			return readFromServer(filename, tempFile);
		}else
			return null;
	}
	
	/**
	 * TODO
	 * Read File from server.
	 * @param filename Filename of target file on server.
	 * @param tempFile
	 * @return
	 */
	private File readFromServer(String filename, File tempFile) {
		if(tempFile == null)
			return null;
		if(!tempFile.exists())
			return null;
		/**
		 * WRITE RECEIVING METHOD FOR TFTP
		 * SUGGESTED TO USE FILESTREAMING METHOD. REFER TO https://www.codejava.net/java-se/file-io/java-io-fileinputstream-and-fileoutputstream-examples#:~:text=Example%20%231%3A%20Copy%20a%20File
		 */
		return tempFile;
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