package scratch;

import java.io.*;
import java.nio.*;
import java.security.*;

import data.*;
import network.Client;
import utils.*;

public class Scratch {
	private static Monitor m = new Monitor();
	private static final String className = "Scratch";
	public static void main(String[] args) {
		m.setState(true);
		RunScratch(args);
		//System.gc();
		System.exit(0);
	}
	
	public static void RunScratch(String[] args) {
		testErrPacket1350();
		testDataPacket1350();
	}
	
	private static void testErrPacket1350() {
		Integer[] errCode = {0,1,2,3,4,5,6,7};
		String[] errMsg = {	"Not defined", "Access violation",
							"File not found", "Disk full or Quota exceeded",
							"Illegal TFTP operation","Unknown port number",
							"File exists", "No such user"
						  };
		for(int i = 0; i < errCode.length; i++) {
			m.printBytes(buildErrPacket1350(errCode[i], errMsg[i]));
			//m.printByteAsString(buildErrPacket1350(errCode[i], errMsg[i]));
		}
	}
	//Follows RFC 1350
	private static byte[] buildErrPacket1350(Integer err, String emsg) {
		//Error Packet 
		byte[] opcode = {0,5}, errcode = {err.byteValue(), 0}, errMsg = emsg.getBytes(), padding = new byte[0];
		byte[][] combined = {opcode, errcode, errMsg, padding};
		return combineBytes(combined);
	}
	
	public static void testDataPacket1350() {
		for(int i = 1; i <= 10; i++) {
			//m.printByteAsString(buildDataPacket1350(i, ("This is test " + i).getBytes()));
			m.printBytes(buildDataPacket1350(i, ("This is test " + i).getBytes()));
		}
	}
	//Follows RFC 1350
	private static byte[] buildDataPacket1350(Integer block, byte[] data) {
		byte[] opcode = {0,3}, blockNum = {block.byteValue(),0};
		byte[][] preDataPacket = {opcode,blockNum,data};
		return combineBytes(preDataPacket);
	}
	
	//Follows RFC 1350
	private static byte[] buildAckPacket(Integer block) {
		byte[] ack = {0,4,0,block.byteValue()};
		return ack;
	}
	
	private static byte[] combineBytes(byte[][] bytes){
		int size = 0, ctr = 0;
		for(int i = 0; i < bytes.length; i++)
			size += bytes[i].length;
		byte[] combinedBytes = new byte[size];
		for(byte[] byteArr: bytes) {
			for(byte b: byteArr) {
				combinedBytes[ctr] = b;
				ctr++;
			}
		}
		return combinedBytes;
	}
	
	private static void FileHandlersTest() {
		FileHandlers fh = new FileHandlers();
		fh.openFile();
		File in = fh.getFile();
		System.out.println("File Size: " + fh.getFileSize());
		System.out.println("Chunks: " + fh.getChunks(512));
		//System.out.println("File Extension: " + fh.getFileExtension());
		File out = new FileHandlers().saveFile();
		//Hasher h = new Hasher();
		//String inHash = h.getSHA256(in);
		//streamFile(in,out);
		//String outHash = h.getSHA256(out);
		//System.out.println("Streamfile Results: " + inHash.equals(outHash));
		
		Client c = new Client();
		c.setDefaults();
		if(c.openConnection()) {
			c.send(in);
			c.closeConnection();
		}
	}
	
	/**
	 * Reference: https://www.codejava.net/java-se/file-io/java-io-fileinputstream-and-fileoutputstream-examples
	 */
	private static void streamFile(File in, File out) {
		String inputFile = in.getAbsolutePath();
        String outputFile = out.getAbsolutePath();
        try {
        	m.printMessage(className, "streamFile(in, out)", "Opening file streams...");
        	InputStream inputStream = new FileInputStream(inputFile);
            OutputStream outputStream = new FileOutputStream(outputFile);
 
            int BUFFER_SIZE = 512, SIZE = inputStream.available(); //512 bytes max for TFTP
            byte[] buffer = new byte[BUFFER_SIZE];
            if(SIZE < BUFFER_SIZE)
            	buffer = new byte[SIZE];
            
            Integer bytesRead = -1;
            
            m.printMessage(className, "streamFile(in, out)", "Streaming and Writing to file...");
            m.printMessage(className, "streamFile(in, out)", "File total size: " + SIZE);
            while ((bytesRead = inputStream.read(buffer)) != -1) { //File Streaming
                //Do what you want with this data;
            	//Say build a packet?
            	System.out.println(bytesRead + "/" + inputStream.available());
            	//outputStream.write(buffer, 0, bytesRead);
            	//m.printBytes(buffer);
            }            
            m.printMessage(className, "streamFile(in, out)", "Streaming and Writing to file complete!");
            inputStream.close();
            outputStream.close();
        }catch(IOException e) {
        	m.printMessage(className, "streamFile(in, out)", "IOException: " + e.getLocalizedMessage());
        } 	
	}
}
