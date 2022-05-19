package mains;

import test.*;
import gui.*;

public class Driver {
	private static GUI g;
	private static Controller c;
	
    public static void main(String[] args){
    	Test_Suite();
    	//System.exit(0);
    }
    
    public static void Test_Suite() {
    	//new Test_FileByte();
        //new Test_Hasher();
    	//new Test_GUI();
    	new Test_FileCompression();
    }
}
