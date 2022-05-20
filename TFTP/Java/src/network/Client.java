package network;

import java.io.IOException;
import java.net.*;
import utils.*;

public class Client {
	private Monitor m = new Monitor();
	private final String className = "Client";
	
	private DatagramSocket socket = null;
	private DatagramPacket packet = null;
	private int PORT = -1;
	private byte[] buffer = null;
	private InetAddress target = null;
	private final int CheckTimeout = 5000;
	
	public Client() {
		try {
			target = InetAddress.getByName("localhost"); //Default target as localhost
			PORT = 69; //Default TFTP port
		} catch (UnknownHostException e) {
			target = null;
			this.PORT = -1;
			m.printMessage(this.className, "Client()", "TryCatch: " + e.getLocalizedMessage());
		}
	}
	
	public Client(String host, int port) {
		try {
			target = InetAddress.getByName(host);
			this.PORT = port;
		} catch (UnknownHostException e) {
			target = null;
			this.PORT = -1;
			m.printMessage(this.className, "Client(host)", "TryCatch: " + e.getLocalizedMessage());
		}
	}
	
	public Client(InetAddress target, int port) {
		this.target = target;
		this.PORT = port;
	}
	
	private String getConnectionDetails(DatagramSocket socket) {
		if(socket != null)
			return socket.getLocalAddress().getHostAddress() + ":" + socket.getLocalPort() + "<==>" + socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
		return null;
	}
	
	public boolean openConnection() {
		if(target != null && PORT != -1) {
			try {
				//Attempt connection
				m.printMessage(this.className,"createConnection()", "Creating DatagramSocket()...");
				this.socket = new DatagramSocket();
				socket.connect(this.target, this.PORT);
				m.printMessage(this.className,"createConnection()", "Socket connected!");
				m.printMessage(this.className,"createConnection()", "Socket: " + getConnectionDetails(this.socket));
				
				//Check if reachable;
				m.printMessage(this.className,"createConnection()", "Checking if target is online: " + targetIsOnline());
			} catch (SocketException e) {
				m.printMessage(this.className,"createConnection()", "TryCatch: Creating connection failed.");
				m.printMessage(this.className, "createConnection()", "TryCatch: " + e.getLocalizedMessage());
				return false;
			}
		}
		return socket.isConnected();
	}
	
	public boolean closeConnection() {
		this.socket.close();
		return socket.isClosed();
	}
	
	public boolean targetIsOnline() {
		return targetIsOnline(this.target);
	}
	
	public boolean targetIsOnline(InetAddress target) {
		if(target != null) {
			try {
				if(target.isReachable(this.CheckTimeout)) {
					m.printMessage(this.className, "targetIsOnline()", "target: " + target.getHostAddress() + " is reachable.");
					return true;
				}
			} catch (IOException e) {
				m.printMessage(this.className, "targetIsOnline()", "target: " + target.getHostAddress() + " is unreachable.");
			}
		}
		return false;
	}
}