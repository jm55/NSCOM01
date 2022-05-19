package test;

/**
 * Driver for FileByte.java
 * 
 * Shows how it can be used.
 */

import data.FileByte;
import data.FileHandlers;

import java.util.ArrayList;
import java.util.Scanner;

public class Test_FileByte {
    static FileByte fb;
    static FileHandlers fh;
    static Scanner scan;
    
    public Test_FileByte(){
    	System.out.println("Test_FileByte");
        scan = new Scanner(System.in);
        
        fb = new FileByte();
        fh = new FileHandlers();
        
		openFile(); //Sample of opening a File and turning it into bytes
		splitFile(); //Sample of splitting a File to ArrayList<bytes[]> and then reassembling+saving it back to File
		saveFile(); //Sample of saving a byte[] into a File
    }

    private static void saveFile(){
    	System.out.println("saveFile");

        System.out.println("Enter text that will be saved as text file:");
        byte[] text = scan.nextLine().getBytes();
        String fileExtension = ".txt";
        
        fh = new FileHandlers();
        
        if(fh.saveFile(text, fileExtension)) {
        	System.out.println("File write successful!");
        	System.out.println("File contents found at: " + fh.getFilePath(false));
        }else
            System.out.println("File write !successful!");
    }

    private static void openFile(){
    	System.out.println("openFile");
    	
    	fh = new FileHandlers();
    	String p = fh.getNewFilePath(false);
        fb = new FileByte(p);
        
        fb.printRawContents();
    }

    private static void splitFile(){
    	System.out.println("splitFile");
    	
    	fh = new FileHandlers();
    	System.out.println("Loading file...");
    	fb = new FileByte(fh.openFile());
    	
    	
    	System.out.println("Splitting file...");
        ArrayList<byte[]> split = fb.splitByBytes(512);
        
        //Simulate writing collected byte[] into machine
        System.out.println("Reassembling file...");
        if(fh.saveFile(fb.reassembleBytes(split), fh.getFileExt()))
        	System.out.println("File successfully saved at " + fh.getFilePath(false));
        else
        	System.out.println("Error occured saving file.");
        split = null; //just to free memory for demo
    }
}
