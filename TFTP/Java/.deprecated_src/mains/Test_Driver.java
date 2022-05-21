package mains;

import test.*;
import utils.Monitor;

public class Test_Driver {
	private static boolean automated = false;
	private static Monitor m;
	public static void main(String[] args) {
		m = new Monitor();
    	m.setState(true);
		if(args.length == 1)
			if(args[0].equals("-A") || args[0].equals("--automatic"))
				Test_Driver.automated = true;
		Test_Suite();
		System.exit(0);
	}

	public static void Test_Suite() {
    	System.out.println("=================");
		System.out.println("    TEST MODE    ");
		System.out.println("=================");
    	if(automated) {
    		new Automated_Test();	
    	}else{
			new Test_FileByte();
        	new Test_FileHandlers();
        	new Test_Hasher();
        	new Test_FileCompression();
        	new Test_GUI();
    	}
    }
}
