package mains;

import data.*;
import gui.GUI;
import utils.Monitor;

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
	private Monitor m = new Monitor(true);
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
			if(this.gui.confirmDialog("Exit Program?") == JOptionPane.YES_OPTION) {
				m.printMessage(this.className, "actionPerformed(e) > EndProgram", "Exiting program...");
				System.exit(0);
			}
		}
	}
}
