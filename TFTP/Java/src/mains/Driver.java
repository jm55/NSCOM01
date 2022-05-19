package mains;

import test.*;
import gui.*;

public class Driver {
	private static GUI g;
	private static Controller c;
	private static final boolean testMode = true;
    public static void main(String[] args){
    	Test_Suite();
    	//System.exit(0);
    }
    
    public static void Test_Suite() {
    	if(testMode) {
    		System.out.println("=================");
    		System.out.println("    TEST MODE    ");
    		System.out.println("=================");
    		System.out.println("Test Functions Last Updated: May 19, 2022");
    		new Test_FileByte();
        	new Test_FileHandlers();
        	new Test_Hasher();
        	new Test_FileCompression();
        	new Test_GUI();
    	}
    }
}