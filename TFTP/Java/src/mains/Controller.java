package mains;

import data.*;
import gui.GUI;
import network.Client;
import utils.Utility;

import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.JOptionPane;
/**
 * GUI Controller for the program.
 * Carried over from previous project for CSARCH2
 *
 */
public class Controller implements ActionListener{
	private final String className = "Controller";
	private Client c = null;
	private GUI gui;

	private File file = null;
	private int DATAPORT = 61001;
	
	public Controller(GUI g) {
		this.gui = g;
		this.gui.setListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		
		switch(action) {
		case "SetDataPort":
			setDataPort();
			break;
		case "ServerConnection":
			serverConnection();
			break;
		case "OpenFile":
			openFile();
			break;
		case "SendFile":
			sndFile();
			break;
		case "RecvFile":
			recvFile();
			break;
		case "AboutProgram":
			aboutProgram();
			break;
		case "Reset":
			reset();
			break;
		case "EndProgram":
			if(GUI.confirmDialog(this.gui, "Exit Program?","Exit Program",JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
				System.gc();
				System.exit(0);
			}
			break;
		case "BlockSelector":
			this.gui.printConsole("Blocksize has been set to: " + this.gui.getBlockSize());
			break;
		default:
			break;
		}
	}

	private void aboutProgram() {
		String message = "©2022\n\nNSCOM01 - TFTP Client Project\nS12\n\n"
				+ "Balcueva, J.\n"
				+ "Escalona, J.M.\n"
				+ "Fadrigo, J.A.M.\n"
				+ "Fortiz, P.R.\n";
		String title = "About";
		GUI.popDialog(this.gui,message, title, JOptionPane.PLAIN_MESSAGE);
	}
	
	private void recvFile() {
		if(!validNetwork())
			return;
		String targetFile = this.gui.getRemoteSelectedFileText();
		if(targetFile.equals("")) {
			this.gui.printConsole("No Remote File Specified");
			GUI.warningDialog(this.gui,"No Remote File Specified");
		}else {
			this.gui.printConsole("Receiving \'" + targetFile + "\' from " + this.gui.getServerIPInput()+ "...");
			File saveAs = receiveFile(targetFile, FileHandlers.saveAsFile());
			if(saveAs != null)
				this.gui.printConsole("Receiving \'" + targetFile + "\' from " + this.gui.getServerIPInput()+ " successful!");
			else
				this.gui.printConsole("Receiving \'" + targetFile + "\' from " + this.gui.getServerIPInput()+ " not successful!");
		}
	}
	
	private void sndFile() {
		if(!validNetwork())
			return;
		if(this.file==null) {
			GUI.warningDialog(this.gui, "No file found, please choose a file.");
			this.file = FileHandlers.openFile();
			if(this.file == null) //If still no file, just stop asking.
				return;
		}
		this.gui.setLocalSelectedFileText(file.getAbsolutePath());
		this.gui.printConsole("Sending \'" + this.file.getAbsolutePath() + "\' to " + this.gui.getServerIPInput()+ "...");
		GUI.popDialog(this.gui, "Uploading file...\nClick OK to start", "Upload", JOptionPane.INFORMATION_MESSAGE);
		if(sendFile(file)) {
			this.gui.printConsole("Sending \'" + file.getName() + "\' to " + this.gui.getServerIPInput()+ " successful!");
		}else {
			this.gui.printConsole("Sending \'" + file.getName() + "\' to " + this.gui.getServerIPInput()+ " not successful!");
		}
	}
	
	private void openFile() {
		file = FileHandlers.openFile();
		if(file.exists())
			this.gui.setLocalSelectedFileText(file.getAbsolutePath());
		else {
			this.gui.printConsole(Utility.getGUIConsoleMessage("File \"" + file.getName() + "\" not found."));
			GUI.errorDialog(this.gui, "File \"" + file.getName() + "\" not found.\nPlease try again.");
		}
	}
	
	private void serverConnection() {
		if(!validNetwork())
			return;	
		pingServer();
	}
	
	private void setDataPort() {
		try{
			this.DATAPORT = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter specified data port: "));
			this.gui.printConsole("Data port changed: " + this.DATAPORT);
		}catch (NumberFormatException nf){
			Utility.printMessage(this.className, "setDataPort(action)", "Exception occured setting DataPort: " + nf.getMessage());
			GUI.errorDialog(this.gui, "Invalid input. Please try again.");
		}
	}
	
	protected void reset(){
		this.gui.clearIO();
		this.DATAPORT = 61001;
		this.gui.printConsole("DataPort has been set to: " + this.DATAPORT);
	}
	
	private boolean sendFile(File f) {
		String method = "sendFile(f)";
		if(!validNetwork())
			return false;
		Utility.printMessage(this.className, method, "Network is valid!");
		Utility.printMessage(this.className, method, "File to send is: " + f.getName());
		boolean state = false;
		try {
			//Ping Server
			if(pingServer()) {
				c = new Client(this.gui.getServerIPInput(),Integer.parseInt(this.gui.getServerPortInput()),this.DATAPORT,Integer.parseInt(this.gui.getBlockSize()));
				//SEND PARAMETERS
				Integer setBlkSize = Integer.parseInt(this.gui.getBlockSize());
				String blkSize = "512";
				if(setBlkSize != 512)
					blkSize = setBlkSize + "";
				String[] opts = {"blksize", "tsize"};
				String[] vals = {blkSize,Files.size(f.toPath())+""};
				
				//DELEGATE RECEIVE
				state = c.send(f, opts, vals);
			}else {
				Utility.printMessage(this.className, method, "Unable to connect to target (ping).");
			}
		}catch(NumberFormatException e) {
			GUI.errorDialog(this.gui, "Error parsing inputs.\nPlease check your inputs.");
			Utility.printMessage(this.className, "sendFile(f): NumberFormatException: ", e.getMessage());
			return state;
		}catch (IOException e) {
			GUI.errorDialog(this.gui, "Error opening file.\nPlease try again.");
			Utility.printMessage(this.className, "sendFile(f): IOException: ", e.getMessage());
		}
		return state;
	}
	
	private File receiveFile(String f, File saveAs) {
		if(!validNetwork())
			return null;
		File receivedFile = null;
		try {
			if(!pingServer()) {
				GUI.popDialog(this.gui,"Target does not respond to client.", "Network Issue", JOptionPane.ERROR_MESSAGE);
				return null;
			}
			//CREATE SEND CLIENT
			this.c = new Client(this.gui.getServerIPInput(),Integer.parseInt(this.gui.getServerPortInput()),this.DATAPORT, Integer.parseInt(this.gui.getBlockSize()));
			//RECEIVE PARAMETERS
			Integer setBlkSize = Integer.parseInt(this.gui.getBlockSize());
			String blkSize = "512";
			if(setBlkSize != 512)
				blkSize = setBlkSize + "";
			String[][] optsvals = {{"tsize","blksize"}, {"0", blkSize}};
			//DELEGATE RECEIVE
			receivedFile = this.c.receive(f, saveAs.getAbsolutePath(), optsvals[0], optsvals[1]);
		}catch(NumberFormatException e) {
			GUI.popDialog(this.gui,"Error parsing inputs, please check again.", "Error", JOptionPane.ERROR_MESSAGE);
			Utility.printMessage(this.className, "receiveFile(f): NumberFormatException: ", e.getMessage());
			return receivedFile;
		}
		return receivedFile;
	}
	
	private boolean validNetwork() {
		String[] conn = this.gui.getServerConfigInput();
		if(conn[0].equals("")||conn[1].equals("")) {
			GUI.warningDialog(this.gui, "Please check your network configuration.");
			return false;
		}
		return true;
	}
	
	private boolean pingServer() {
		int port = Integer.parseInt(this.gui.getServerPortInput());
		Client pingClient = new Client(this.gui.getServerIPInput(), port, this.DATAPORT,Integer.parseInt(this.gui.getBlockSize()));
		pingClient.openConnection();
		this.gui.printConsole("Target " + pingClient.getConnectionDetails() + " online: " + pingClient.targetIsOnline());
		boolean state = pingClient.targetIsOnline();
		pingClient.closeConnection();
		return state;
	}
}
