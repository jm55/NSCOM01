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
        System.out.println("Driver_FileByte");
        scan = new Scanner(System.in);
        
        fb = new FileByte();
        fh = new FileHandlers();
        
        //Menu Loop
        int c = -1;
        do {
        	System.out.println("===============================");
        	System.out.println("1 - Open File & Read Bytes\n2 - Split File Simulate\n3 - Save File");
        	System.out.print("Enter choice: ");
        	c = Integer.parseInt(scan.nextLine());
        	if(c == 1)
        		openFile(); //Sample of opening a File and turning it into bytes
        	else if(c == 2)
        		splitFile(); //Sample of splitting a File to ArrayList<bytes[]> and then reassembling+saving it back to File
        	else if(c == 3)
        		saveFile(); //Sample of saving a byte[] into a File
        }while(c != 0);
        
        scan.close();
        System.exit(0);
    }

    private static void saveFile(){
    	System.out.println("Save File");

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
    	System.out.println("Open File");
    	
    	fh = new FileHandlers();
    	String p = fh.getNewFilePath(false);
        fb = new FileByte(p);
        
        fb.printRawContents();
    }

    private static void splitFile(){
    	System.out.println("Split File");
    	
    	fh = new FileHandlers();
    	System.out.println("Loading file...");
    	fb = new FileByte(fh.openFile());
    	
    	
    	System.out.println("Splitting file...");
        ArrayList<byte[]> split = fb.splitByBytes(512);
        
        //Simulate writing collected byte[] into machine
        System.out.println("Reassembling file...");
        if(fh.saveFile(fb.reassembleBytes(split),  "."+fh.getFileExt()))
        	System.out.println("File successfully saved at " + fh.getFilePath(false));
        else
        	System.out.println("Error occured saving file.");
        split = null; //just to free memory for demo
    }
}
