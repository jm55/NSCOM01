package mains;

import data.*;
import gui.GUI;
import utils.Utility;

import java.awt.event.*;

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
	private Utility m = new Utility();
	private final String className = "Controller";
	
	private GUI gui;
	
	public Controller(GUI g) {
		this.gui = g;
		this.gui.setListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String act = e.getActionCommand();
		
		m.printMessage(this.className, "actionPerformed(e)", act);
		
		if(act.equals("ServerConnection")) {
			
		}
		
		if(act.equals("OpenFile")) {
			
		}
		
		if(act.equals("SendFile")) {
			
		}
		
		if(act.equals("AboutProgram")) {
			
		}
		
		if(act.equals("Reset")) {
			
		}
		
		if(act.equals("EndProgram")) {
			if(this.gui.confirmDialog("Exit Program?","Exit Program",JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
				m.printMessage(this.className, "actionPerformed(e) > EndProgram", "Exiting program...");
				System.exit(0);
			}
		}
	}
	
	private void printConsole(String message) {
		this.gui.appendOutputText(m.getGUIConsoleMessage(message));
	}
}
