package network;

import java.io.*;
import java.net.*;

import data.FileHandlers;
import data.TFTP;
import utils.*;

public class Client {
	private Monitor m = new Monitor();
	private final String className = "Client";
	
	private DatagramSocket socket = null;
	private DatagramPacket packet = null;
	private int PORT = -1, BUFFER_SIZE = 512;
	private byte[] buffer = null;
	private InetAddress target = null;
	private final int CheckTimeout = 5000;
	
	public Client() {
		
	}
	
	public Client(String host, int port) {
		m.printMessage(this.className, "Client(host,port)", "Building Client as " + host + ":" + port + "...");
		try {
			this.target = InetAddress.getByName(host);
			this.PORT = port;
		} catch (UnknownHostException e) {
			target = null;
			this.PORT = -1;
			m.printMessage(this.className, "Client(host,port)", "TryCatch: " + e.getLocalizedMessage());
		}
		if(this.target == null)
			m.printMessage(this.className, "Client(host,port)", "Building Client as " + host + ":" + port + " failed.");
		else 
			m.printMessage(this.className, "Client(host,port)", "Building Client as " + host + ":" + port + " successful.");
		m.printMessage(this.className, "Client(host,port)", "Connection to " + host + ":" + port + "=" + isConnected());
	}
	
	public Client(InetAddress target, int port) {
		this.target = target;
		this.PORT = port;
		m.printMessage(this.className, "Client(host,port)", "Connection to " + this.target.getHostAddress() + ":" + port + "=" + isConnected());
	}
	
	/**
	 * Sets this object's target and PORT to localhost:69.
	 * Port specified for TFTP default port.
	 */
	public void setDefaults() {
		try {
			this.target = InetAddress.getByName("localhost"); //Default target as localhost
			this.PORT = 69; //Default TFTP port
		} catch (UnknownHostException e) {
			target = null;
			this.PORT = -1;
			m.printMessage(this.className, "Client()", "TryCatch: " + e.getLocalizedMessage());
		}
	}
	
	public boolean send(File f) {
		if(f.exists() && socket.isConnected()) {
			m.printMessage(this.className, "send(File)", "f.exists()...");
			try {
				m.printMessage(this.className, "send(File)", "Streaming f...");
				InputStream inputStream = new FileInputStream(f.getAbsolutePath());
				Integer BUFFER_SIZE = 512, SIZE = inputStream.available(), bytesRead = -1;
				byte[] buffer = new byte[BUFFER_SIZE];
	            if(SIZE < BUFFER_SIZE)
	            	buffer = new byte[SIZE];
	            m.printMessage(this.className, "send(File)", "Reading through f and transmitting to target...");
				while((bytesRead = inputStream.read(buffer)) != -1) {
					do{
						if(bytesRead == 512) {
							/**
							 * Send Normal Data
							 */
						}else{
							/**
							 * Send Last Data
							 */
						}
					}while(true); //Check for ACKs here
				}
				m.printMessage(this.className, "send(File)", "Closing stream...");
				inputStream.close();
				return true;
			} catch (IOException e) {
				m.printMessage(this.className, "send(File)", "IOException: " + e.getLocalizedMessage());
			} catch (NullPointerException e) {
				m.printMessage(this.className, "send(File)", "NullPointerException: " + e.getLocalizedMessage());
			}
		}
		return false;
	}
	
	public byte[] receive() {
		return null;
	}
	
	/**
	 * Opens a connection specified by its target and port.
	 * @return Socket connection status if connected or false if error occured on connection creation.
	 */
	public boolean openConnection() {
		if(target != null && PORT != -1) {
			try {
				//Attempt connection
				m.printMessage(this.className,"openConnection()", "Creating DatagramSocket()...");
				this.socket = new DatagramSocket();
				socket.connect(this.target, this.PORT);
				m.printMessage(this.className,"openConnection()", "Socket connected!");
				m.printMessage(this.className,"openConnection()", "Socket: " + getConnectionDetails(this.socket));
				//Check if reachable;
				m.printMessage(this.className,"openConnection()", "Checking if target is online: " + targetIsOnline());
			} catch (SocketException e) {
				m.printMessage(this.className,"openConnection()", "TryCatch: Creating connection failed.");
				m.printMessage(this.className, "openConnection()", "TryCatch: " + e.getLocalizedMessage());
				return false;
			}
		}
		return this.socket.isConnected();
	}
	
	/**
	 * Closes the connection this Client object.
	 * @return True if socket is closed, false if otherwise.
	 */
	public boolean closeConnection() {
		m.printMessage(this.className, "closeConnection()", "Closing connection...");
		this.socket.close();
		m.printMessage(this.className, "closeConnection()", "" + socket.isClosed());
		return socket.isClosed();
	}
	
	/**
	 * Returns connected status.
	 * @return True if connected, false if otherwise.
	 */
	public boolean isConnected() {
		if(this.socket == null) { //this.socket is null
			m.printMessage(this.className, "isConnected()", "this.socket is null");
			return false;
		}else if(!this.socket.isBound()) { //this.socket is !bound/binded
			m.printMessage(this.className, "isConnected()", "this.socket is not bound");
			return false;
		}else { //check if this.socket is connected
			m.printMessage(this.className, "isConnected()", ""+this.socket.isConnected());
			return this.socket.isConnected();
		}
	}
	
	/**
	 * Checks if Client's host is online.
	 * @return True if online, false if object's target is null or offline.
	 */
	public boolean targetIsOnline() {
		if(this.target != null)
			return targetIsOnline(this.target);
		else
			return false;
	}
	
	/**
	 * Checks if the specified target is online or not.
	 * @param target Target host to be checked.
	 * @return True if online, false if target is offline.
	 */
	public boolean targetIsOnline(InetAddress target) {
		if(target != null) {
			try {
				m.printMessage(this.className, "targetIsOnline(target)", "Pinging " + target.getHostAddress());
				if(target.isReachable(this.CheckTimeout)) {
					m.printMessage(this.className, "targetIsOnline()", "target: " + target.getHostAddress() + " is reachable.");
					return true;
				}
			} catch (IOException e) {
				m.printMessage(this.className, "targetIsOnline()", "target: " + target.getHostAddress() + " is unreachable.");
			}
		}
		m.printMessage(this.className, "targetIsOnline(target)", "Specified target is cannot be reached");
		return false;
	}
	
	/**
	 * Get the connection details of the client connection.
	 * @return String[] containing (local:port, remote:port), null if socket is not connected.
	 */
	public String getConnectionDetails() {
		return getConnectionDetails(this.socket);
	}
	
	private String getConnectionDetails(DatagramSocket socket) {
		if(socket != null)
			return socket.getLocalAddress().getHostAddress() + ":" + socket.getLocalPort() + " <==> " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
		return null;
	}
}