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
 * 
 * Carried over from previous project for CSARCH2
 * 
 * @author Escalona, Jose Miguel
 *
 */
public class Controller implements ActionListener {
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
		
		u.printMessage(this.className, "actionPerformed(e)", act);
		
		if(act.equals("ServerConnection")) {
			if(!validNetwork())
				return;
			String host = gui.getServerIPInput();
			int port = Integer.parseInt(gui.getServerPortInput());
			c = new Client(host,port,Integer.parseInt(gui.getBlockSize()));
			u.printMessage(this.className, act, "Opening connection...");
			c.openConnection();
			u.printMessage(this.className, act, "Pinging: " + c.getConnectionDetails());
			String ping = "Target " + c.getConnectionDetails() + " online: " + c.targetIsOnline();
			this.printConsole(ping);
			u.printMessage(this.className, act, "c.targetIsOnline(): " + c.targetIsOnline());
			u.printMessage(this.className, act, "Closing connection...");
			c.closeConnection();
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
			if(fh.getFile()==null)
				if(!fh.openFile()) {
					u.getGUIConsoleMessage("Error opening file");
					return;
				}
			if(fh.getFile() == null)
				return;
			gui.setLocalSelectedFileText(fh.getFile().getAbsolutePath());
			//============================================================================
			
			File f = fh.getFile(); //USE THIS FILE TO SEND ON CLIENT
			sendFile(f);
		}
		
		if(act.equals("RecvFile")) {
			String targetFile = gui.getRemoteSelectedFileText();
			if(targetFile.equals("")) {
				gui.popDialog("No Remote File Specified", "Receive File", JOptionPane.WARNING_MESSAGE);
			}else
				receiveFile(targetFile);
		}
		
		if(act.equals("AboutProgram")) {
			String message = "©2022\n\nNSCOM01 - TFTP Client Project\nS12\n\nEscalona, J.M.\nFadrigo, A.";
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
	 * TODO
	 * @param f File to be sent
	 * @return True if successful, false if otherwise.
	 */
	private boolean sendFile(File f) {
		if(!validNetwork())
			return false;
		u.printMessage(this.className, "sendFile(f)", "Network is valid!");
		u.printMessage(this.className, "sendFile(f)", "File to send is: " + f.getName());
		boolean state = false;
		try {
			//CREATE SEND CLIENT
			c = new Client(gui.getServerIPInput(),Integer.parseInt(gui.getServerPortInput()),Integer.parseInt(gui.getBlockSize()));
			
			//SEND PARAETERS
			File saveAs = fh.openAsFile();
			Integer setBlkSize = Integer.parseInt(gui.getBlockSize());
			String blkSize = "";
			if(setBlkSize != 512)
				blkSize = setBlkSize + "";
			String[] opts = {"tsize","blocksize","timeout"};
			String[] vals = {Files.size(f.toPath())+"",blkSize,"1"};
			
			//DELEGATE RECEIVE
			state = c.send(f, opts, vals);
		}catch(NumberFormatException e) {
			gui.popDialog("Error parsing inputs, please check again.", "Error", JOptionPane.ERROR_MESSAGE);
			u.printMessage(this.className, "sendFile(f) > NumberFormatException: ", e.getLocalizedMessage());
			return state;
		} catch (IOException e) {
			gui.popDialog("Error on selected file.", "Error", JOptionPane.ERROR_MESSAGE);
			u.printMessage(this.className, "sendFile(f) > IOException: ", e.getLocalizedMessage());
		}
		
		state = true; //====REMOVE BEFORE FLIGHT====
		
		return state;
	}
	
	/**
	 * TODO
	 * @param f Filename of file to be received.
	 * @return True if successful, false if otherwise.
	 */
	private File receiveFile(String f) {
		if(!validNetwork())
			return null;
		u.printMessage(this.className, "receiveFile(f)", "Network is valid!");
		u.printMessage(this.className, "sendFile(f)", "File to receive is: " + f);
		File receivedFile = null;
		try {
			//CREATE SEND CLIENT
			c = new Client(gui.getServerIPInput(),Integer.parseInt(gui.getServerPortInput()),Integer.parseInt(gui.getBlockSize()));
			
			//RECEIVE PARAETERS
			File saveAs = fh.openAsFile();
			Integer setBlkSize = Integer.parseInt(gui.getBlockSize());
			String blkSize = "";
			if(setBlkSize != 512)
				blkSize = setBlkSize + "";
			String[] opts = {"tsize","blocksize","timeout"};
			String[] vals = {"0",blkSize,"1"};
			
			//DELEGATE RECEIVE
			receivedFile = c.receive(f, saveAs.getAbsolutePath(), opts, vals);
		}catch(NumberFormatException e) {
			gui.popDialog("Error parsing inputs, please check again.", "Error", JOptionPane.ERROR_MESSAGE);
			u.printMessage(this.className, "sendFile(f) > NumberFormatException: ", e.getLocalizedMessage());
			return receivedFile;
		}
		
		receivedFile = new File("loremipsum.txt"); //====REMOVE BEFORE FLIGHT====
		
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
	
	private void printConsole(String message) {
		this.gui.appendOutputText(u.getGUIConsoleMessage(message));
	}
}
