package mains;

import utils.Monitor;

import java.io.IOException;
import java.util.Scanner;

import gui.*;

public class Driver {
	private static Monitor m;
	private static GUI g;
	private static Controller c;
    public static void main(String[] args){
    	m = new Monitor();
    	if(args.length > 0) {
    		for(String a: args) {
    			if(a.equals("-V") || a.equals("--verbose")) {
    				m.setState(true);
    			}
    			if(a.equals("-C") || a.equals("--console")) {
    				Console();
    			}
    		}
    	}
    	Production();
    }
    
    private static void Production() {
    		g = new GUI();
    		c = new Controller(g);
    		g.setDefaultDisplay();
        	g.updateConnectBtn(false);
    }
    
    private static void Console() {
    	Scanner scan = new Scanner(System.in);
    	int select = -1;
    	do {
    		select = Menu(scan);
    		cls();
    	}while(select != 0);
    	System.exit(0);
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