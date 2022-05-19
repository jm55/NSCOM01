package mains;

import test.*;
import utils.Monitor;
import gui.*;

public class Driver {
	private static Monitor m;
	private static GUI g;
	private static Controller c;
    public static void main(String[] args){
    	m = new Monitor();
    	m.setState(false);
    	
    	Production();
    	//System.exit(0);
    }
    
    public static void Production() {
    		g = new GUI();
    		c = new Controller(g);
    		
    		g.setDefaultDisplay();
        	g.updateConnectBtn(false);
    }
}