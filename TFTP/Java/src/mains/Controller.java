package mains;

import data.*;
import gui.GUI;
import utils.Monitor;

import java.awt.event.*;
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
		m.printMessage(this.className, "actionPerformed(e)", e.getActionCommand());
		/**
		 * You can just check 
		 * 	if(e.getActionCommand() == {foo.action}){
		 *		foo();
		 * 	}
		 * 
		 * 	if(e.getActionCommand() == {bar.action}){
		 *		bar();
		 * 	}
		 */
	}
}
