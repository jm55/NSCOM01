package mains;

import data.*;
import gui.GUI;
import network.Client;
import utils.Utility;

import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;
/**
 * GUI Controller for the program.
 * Carried over from previous project for CSARCH2
 *
 */
public class Controller implements ActionListener{
	private Utility u = new Utility();
	private final String className = "Controller";
	private Client c = null;
	private GUI gui;
	private FileHandlers fh = new FileHandlers();
	
	public Controller(GUI g) {
		this.gui = g;
		this.gui.setListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String act = e.getActionCommand();
		String methodName = "actionPerformed(e)";
		u.printMessage(this.className, methodName, act);
		
		if(act.equals("ServerConnection")) {
			if(!validNetwork())
				return;
			pingServer();
		}
		
		if(act.equals("OpenFile")) {
			u.printMessage(this.className, act, "Opening File...");
			if(fh.openFile())
				gui.setLocalSelectedFileText(fh.getFile().getAbsolutePath());
			else {
				printConsole(u.getGUIConsoleMessage("Error opening file"));
				gui.popDialog("Error opening file", "Open File", JOptionPane.WARNING_MESSAGE);
			}
		}
		
		if(act.equals("SendFile")) {
			if(!validNetwork())
				return;
			if(fh.getFile()==null)
				if(!fh.openFile()) {
					u.getGUIConsoleMessage("Error opening file");
					return;
				}
			if(fh.getFile() == null)
				return;
			gui.setLocalSelectedFileText(fh.getFile().getAbsolutePath());
			File f = fh.getFile(); //USE THIS FILE TO SEND ON CLIENT
			printConsole("Sending \'" + f.getName() + "\' to " + gui.getServerIPInput()+ "...");
			gui.popDialog("Uploading file...\nClick OK to continue", "Upload", JOptionPane.INFORMATION_MESSAGE);
			if(sendFile(f)) {
				printConsole("Sending \'" + f.getName() + "\' to " + gui.getServerIPInput()+ " successful!");
			}else {
				printConsole("Sending \'" + f.getName() + "\' to " + gui.getServerIPInput()+ " not successful!");
			}
		}
		
		if(act.equals("RecvFile")) {
			if(!validNetwork())
				return;
			String targetFile = gui.getRemoteSelectedFileText();
			File saveAs = fh.saveFile();
			if(targetFile.equals("")) {
				printConsole("No Remote File Specified");
				gui.popDialog("No Remote File Specified", "Receive File", JOptionPane.WARNING_MESSAGE);
			}else {
				printConsole("Receiving \'" + targetFile + "\' from " + gui.getServerIPInput()+ "...");
				gui.popDialog("Downloading file...\nClick OK to continue", "Download", JOptionPane.INFORMATION_MESSAGE);
				File recvFile = receiveFile(targetFile, saveAs);
				if(recvFile != null) {
					printConsole("Receiving \'" + targetFile + "\' from " + gui.getServerIPInput()+ " successful!");
				}else {
					printConsole("Receiving \'" + targetFile + "\' from " + gui.getServerIPInput()+ " not successful!");
				}
			}
		}
		
		if(act.equals("AboutProgram")) {
			String message = "©2022\n\nNSCOM01 - TFTP Client Project\nS12\n\n"
					+ "Balcueva, J.\n"
					+ "Escalona, J.M.\n"
					+ "Fadrigo, J.A.M.\n"
					+ "Fortiz, P.R.\n";
			String title = "About";
			gui.popDialog(message, title, JOptionPane.PLAIN_MESSAGE);
		}
		
		if(act.equals("Reset")) {
			gui.clearIO();
		}
		
		if(act.equals("EndProgram")) {
			if(this.gui.confirmDialog("Exit Program?","Exit Program",JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
				u.printMessage(this.className, "actionPerformed(e) > " + act, "Exiting program...");
				System.exit(0);
			}
		}
		
		if(act.equals("BlockSelector")) {
			u.printMessage(this.className, "actionPerformed(e) > " + act, "Value change: " + gui.getBlockSize());
			printConsole("Blocksize Set to: " + gui.getBlockSize());
		}
	}
	/**
	 * Conduct sending of file to the TFTP server. Return boolean value indicating success or failure.
	 * @param f File to be sent
	 * @return True if successful, false if otherwise.
	 */
	private boolean sendFile(File f) {
		String methodName = "sendFile(f)";
		if(!validNetwork())
			return false;
		u.printMessage(this.className, methodName, "Network is valid!");
		u.printMessage(this.className, methodName, "File to send is: " + f.getName());
		boolean state = false;
		try {
			//Ping Server
			if(pingServer()) {
				u.printMessage(this.className, methodName, "Target does respond to ping");
				//CREATE SEND CLIENT
				c = new Client(gui.getServerIPInput(),Integer.parseInt(gui.getServerPortInput()),Integer.parseInt(gui.getBlockSize()));
				
				//SEND PARAMETERS
				Integer setBlkSize = Integer.parseInt(gui.getBlockSize());
				String blkSize = "512";
				if(setBlkSize != 512)
					blkSize = setBlkSize + "";
				String[] opts = {"blksize", "tsize"};
				String[] vals = {blkSize,Files.size(f.toPath())+""};
				
				//DELEGATE RECEIVE
				state = c.send(f, opts, vals);
			}else {
				u.printMessage(this.className, methodName, "Target does not respond to ping");
			}
		}catch(NumberFormatException e) {
			gui.popDialog("Error parsing inputs, please check again.", "Error", JOptionPane.ERROR_MESSAGE);
			u.printMessage(this.className, "sendFile(f) > NumberFormatException: ", e.getLocalizedMessage());
			return state;
		}catch (IOException e) {
			gui.popDialog("Error on selected file.", "Error", JOptionPane.ERROR_MESSAGE);
			u.printMessage(this.className, "sendFile(f) > IOException: ", e.getLocalizedMessage());
		}
		return state;
	}
	
	/**
	 * Conduct receiving file from the TFTP server. Return File object or null if successful or not respectively.
	 * @param f Filename of file to be received.
	 * @return True if successful, false if otherwise.
	 */
	private File receiveFile(String f, File saveAs) {
		String methodName = "receiveFile(f)";
		if(!validNetwork())
			return null;
		u.printMessage(this.className, methodName, "Network is valid!");
		u.printMessage(this.className, methodName, "File to receive is: " + f);
		File receivedFile = null;
		try {
			if(pingServer()) {
				u.printMessage(this.className, methodName, "Target does respond to ping");
				//CREATE SEND CLIENT
				c = new Client(gui.getServerIPInput(),Integer.parseInt(gui.getServerPortInput()),Integer.parseInt(gui.getBlockSize()));
				
				//RECEIVE PARAMETER
				Integer setBlkSize = Integer.parseInt(gui.getBlockSize());
				String blkSize = "512";
				if(setBlkSize != 512)
					blkSize = setBlkSize + "";
				String[] opts = {"tsize","blksize"};
				String[] vals = {"0",blkSize};
				
				//DELEGATE RECEIVE
				receivedFile = c.receive(f, saveAs.getAbsolutePath(), opts, vals);
			}else {
				u.printMessage(this.className, methodName, "Target does not respond to ping");
			}
		}catch(NumberFormatException e) {
			gui.popDialog("Error parsing inputs, please check again.", "Error", JOptionPane.ERROR_MESSAGE);
			u.printMessage(this.className, "sendFile(f) > NumberFormatException: ", e.getLocalizedMessage());
			return receivedFile;
		}
		return receivedFile;
	}
	
	/**
	 * Checks if network configuration on GUI is valid or not.
	 * This in terms if there is input or not.
	 * Displays warning if invalid.
	 * @return True if fields for IP and port are filled, false if not.
	 */
	private boolean validNetwork() {
		String[] conn = gui.getServerConfigInput();
		if(conn[0].equals("")||conn[1].equals("")) {
			gui.popDialog("Please check your network configuration.", "Network", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		return true;
	}
	
	/**
	 * Print text to GUI's console
	 * @param message
	 */
	private void printConsole(String message) {
		this.gui.appendOutputText(u.getGUIConsoleMessage(message));
	}
	
	/**
	 * Pings the server
	 * @return True if online, false if otherwise
	 */
	private boolean pingServer() {
		boolean state = false;
		String methodString = "pingServer()";
		String host = gui.getServerIPInput();
		int port = Integer.parseInt(gui.getServerPortInput());
		Client pingClient = new Client(host,port,Integer.parseInt(gui.getBlockSize()));
		u.printMessage(this.className, methodString, "Opening connection...");
		pingClient.openConnection();
		u.printMessage(this.className, methodString, "Pinging: " + pingClient.getConnectionDetails());
		printConsole("Target " + pingClient.getConnectionDetails() + " online: " + pingClient.targetIsOnline());
		state = pingClient.targetIsOnline();
		u.printMessage(this.className, methodString, "Closing connection...");
		pingClient.closeConnection();
		return state;
	}
}
