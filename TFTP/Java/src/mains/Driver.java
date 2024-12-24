package mains;

import javax.swing.JOptionPane;

import utils.*;
import gui.*;

public class Driver {
	private static GUI g;
	private static Controller c;
    
	public static void main(String[] args){
		checkJRE();
		try {
			Production();
		}catch(Exception e) {
			Utility.printMessage("Driver", "main()", "Error: " + e.getMessage());
			exit(0);
		}
    }
    
    private static void checkJRE() {
    	String jre =  System.getProperties().getProperty("java.version");
    	if(jre.contains("."))
    		jre = jre.substring(0,jre.indexOf('.'));
    	if(Integer.parseInt(jre) < 17) {
    		int ans = GUI.confirmDialog(null, "The program was not tested for the version of Java installed (version " + jre +  ").\n"
    											+ "Program requires at least Java 17.\nDo you want to continue?", 
    											"Java Compatibility Check", 
    											JOptionPane.YES_NO_OPTION, 
    											JOptionPane.QUESTION_MESSAGE);
    		if(ans == JOptionPane.NO_OPTION)
    			exit(0);
    	}
    }

    private static void Production() {
		g = new GUI(true);
		c = new Controller(g);
		g.setDefaultDisplay();
		c.reset();
    }
    
    private static void exit(int status) {
    	System.gc();
    	System.exit(status);
    }
}