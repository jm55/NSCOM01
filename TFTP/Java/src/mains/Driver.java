package mains;

import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

import javax.swing.JOptionPane;

import utils.*;
import gui.*;

/**
 * Driver class of the program.
 * Allows for arguments if executed via terminal.
 */
public class Driver {
	private static Utility u;
	private static GUI g;
	private static Controller c;
    public static void main(String[] args){
    	u = new Utility();
    	
    	Properties p = System.getProperties();
    	String jre =  p.getProperty("java.version");
    	
    	if(jre.contains("."))
    		jre = jre.substring(0,jre.indexOf('.'));
    	
    	if(Integer.parseInt(jre) < 17) {
    		int ans = new GUI().confirmDialog("The program was not tested for the version of Java installed (version " + jre +  ").\nProgram requires at least Java 17.\nDo you want to continue?", "Java Compatibility Check", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    		if(ans == JOptionPane.NO_OPTION)
    			exit(0);
    	}
    	
    	try {
        	if(args.length > 0) {
        		for(String a: args) {
        			if(a.equals("-V") || a.equals("--verbose")) {
        				u.setState(true);
        				Production();
        			}
        			if(a.equals("-C") || a.equals("--console"))
        				Console();
        			if(a.equals("-T") || a.equals("--testing")) {
        				System.out.println("THIS FUNCTION IS DEPRECATED!\nEXITING...");
        				exit(0);
        				Testing();
        			}
        			if(a.equals("-P") || a.equals("--production"))
        				Production();
        			if(a.equals("-H") || a.equals("--help"))
        				Help();
        		}
        	}else
        		Production();
    	}catch(Exception e) {
    		u.printMessage("Driver", "main()", "Error: " + e.getMessage());
    		exit(0);
    	}
    }
    
    private static void Help() {
    	System.out.println("TFTP Client");
    	System.out.println("NSCOM01 - S12 (Term 2, 2022)");
    	System.out.println("Escalona & Fadrigo");
    	System.out.println("=========================================");
    	System.out.println("-V or --verbose: " + "Verbose mode.");
    	System.out.println("-T or --testing: " + "Testing mode. [DEPRECATED]");
    	System.out.println("-P or --production: " + "Production mode.");
    	System.out.println("-H or --help: " + "Help (you're looking at this).");
    	System.out.println("=========================================");
    }
    
    private static void Testing() {
    	//new Testing();
    }
    
    private static void Production() {
    		g = new GUI(true);
    		c = new Controller(g);
    		g.setDefaultDisplay();
    		c.reset();
    		String[] bugs = {};
    		if(bugs.length > 0) {
    			String strBugs = "";
        		int ctr = 1;
    			for(String b: bugs) {
        			strBugs += ctr + ": " + b + "\n";
        			ctr++;
        		}
    			g.popDialog("Program Bugs:\n" + strBugs, "Bugs List", JOptionPane.WARNING_MESSAGE);
    		}
    }
    
    private static void Console() {
    	Scanner scan = new Scanner(System.in);
    	int select = -1;
    	do {
    		select = Menu(scan);
    		cls();
    	}while(select != 0);
    	exit(0);
    }
    
    private static int Menu(Scanner scan) {
    	System.out.println("TFTP Client");
    	System.out.println("Console Mode");
    	System.out.println("============");
    	System.out.println("0 - Exit");
    	System.out.println("============");
    	System.out.print("Enter choice: ");
    	return Integer.parseInt(scan.nextLine());
    }
    
    private static void exit(int status) {
    	System.gc();
    	System.exit(status);
    }
    
    //Reference: https://stackoverflow.com/a/38365871
    private static void cls() {
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                Runtime.getRuntime().exec("clear");
        } catch (IOException | InterruptedException ex) {}
    }
}