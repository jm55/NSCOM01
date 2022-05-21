package scratch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import data.*;
import utils.Hasher;
import utils.Monitor;

public class Scratch {

	public static void main(String[] args) {
		Monitor m = new Monitor();
		m.setState(true);
		System.out.println("Scratch Tester");
		RunScratch(args);
		//System.gc();
		System.exit(0);
	}
	
	/**
	 * Reference: https://www.codejava.net/java-se/file-io/java-io-fileinputstream-and-fileoutputstream-examples
	 */
	public static void RunScratch(String[] args) {
		final int BUFFER_SIZE = 512;
		File in = new FileHandlers().openFile(), out = new FileHandlers().openFile();
        String inputFile = in.getAbsolutePath();
        String outputFile = out.getAbsolutePath();
        //System.gc();
 
        try{
        	InputStream inputStream = new FileInputStream(inputFile);
            OutputStream outputStream = new FileOutputStream(outputFile);
 
            System.out.println("Bytes available: " + inputStream.available());
            System.out.println("Buffer size: " + BUFFER_SIZE);
            
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;
 
            System.out.println("Writing file...");
            while ((bytesRead = inputStream.read(buffer)) != -1) { //File Streaming
                //Do what you want with this data;
            	//Say build a packet?
            	printBytes(buffer);
            	outputStream.write(buffer, 0, bytesRead);
            }
            System.out.println("File Writing Complete!");
            inputStream.close();
            outputStream.close();
        } catch (Exception ex) {
            //ex.getLocalizedMessage();
        }
        System.out.println("Hasher: " + new Hasher().quickCompare(in, out));
	}
	
	private static void printBytes(byte[] bytes) {
		for(byte b: bytes) {
			System.out.print(b);
		}
		System.out.println("");
	}
}
