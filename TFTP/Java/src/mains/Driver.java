package mains;

import test.*;
import gui.*;

public class Driver {
	private static GUI g;
	private static Controller c;
	
    public static void main(String[] args){
    	//new Test_FileByte();
        //new Test_Hasher();
    	
    	buildGUI();
    	showGUI();
    	
    	//System.exit(0);
    }
    
    private static void showGUI() {
    	g.setDefaultDisplay();
    	g.updateConnectBtn(false);
    }
    
    private static void buildGUI() {
    	System.out.println("Initializing GUI...");
		g = new GUI();
		c = new Controller(g);
    }
}
