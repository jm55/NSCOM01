package scratch;

import java.io.*;
import java.nio.*;
import java.security.*;
import java.util.ArrayList;

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
		System.out.println("Error Packets");
		testErrPacket();
		System.out.println("Data Packets");
		testDataPacket();
		System.out.println("ACK Packets");
		testAckPacket();
		System.out.println("RRQ (RFC2347) Packets");
		testRQPacket();
	}
	
	private static void testErrPacket() {
		Integer[] errCode = {0,1,2,3,4,5,6,7};
		String[] errMsg = {	"Not defined", "Access violation",
							"File not found", "Disk full or Quota exceeded",
							"Illegal TFTP operation","Unknown port number",
							"File exists", "No such user"
						  };
		for(int i = 0; i < errCode.length; i++) {
			m.printBytes(buildErrPacket(errCode[i], errMsg[i]));
		}
	}
	private static void testAckPacket(){
		for(int i = 0; i <= 10; i++)
			m.printBytes(buildAckPacket(i));
	}
	private static void testDataPacket() {
		for(int i = 1; i <= 10; i++) {
			//m.printByteAsString(buildDataPacket(i, ("This is test " + i).getBytes()));
			m.printBytes(buildDataPacket(i, ("This is test " + i).getBytes()));
		}
	}
	private static void testRQPacket() {
		//Read
		System.out.println("Read");
		byte[] woOptVals = buildRQ(1, "This is filename", "This is mode", null, null), wOptVals = buildRQ(1,"This is filename", "This is mode", "1234".getBytes(), "1234".getBytes());
		m.printByteAsString(woOptVals); //Without OptVals
		m.printByteAsString(wOptVals); //With OptVals
		
		//Write
		System.out.println("Write");
		woOptVals = buildRQ(2, "This is filename", "This is mode", null, null);
		wOptVals = buildRQ(2,"This is filename", "This is mode", "1234".getBytes(), "1234".getBytes());
		m.printByteAsString(woOptVals); //Without OptVals
		m.printByteAsString(wOptVals); //With OptVals
	}
	
	//Follows RFC 2347
	private static byte[] buildRQ(Integer type, String filename, String mode, byte[] opts, byte[] vals) {
		if(type > 2 || type < 1)
			return null;
		//Check if given file or mode is null, return null if so.
		if(filename == null || mode == null) {
			return null;
		}
		//Prepare opcode for Read Request.
		byte[] opcode = {0,type.byteValue()};
		
		if(opts != null && vals != null) { //Check if opts and vals are not null.
			if(opts.length != vals.length) { //Check if lengths of opts and vals are not equal.
				return null;
			}else { //Lengths of opts and vals are equal.
				byte[] optsVals = buildOptsVals(opts, vals); //Combines opts & vals into one byte[]; Includes the last padding for valsN
				byte[][] combined = {opcode, filename.getBytes(), getPaddingByteArr(), mode.getBytes(), getPaddingByteArr(), optsVals};
				return combineBytes(combined);
			}
		}else {
			//Follows bytes: {0,1,filename.bytes,0,mode.bytes,0};
			byte[][] combined = {opcode,filename.getBytes(),getPaddingByteArr(), mode.getBytes(), getPaddingByteArr()};
			return combineBytes(combined);
		}
	}
	
	//Follows RFC 1350
	private static byte[] buildAckPacket(Integer block) {
		byte[] ack = {0,4,block.byteValue(),0};
		return ack;
	}
	
	//Follows RFC 1350
	private static byte[] buildDataPacket(Integer block, byte[] data) {
		byte[] opcode = {0,3}, blockNum = {block.byteValue(),0};
		byte[][] preDataPacket = {opcode,blockNum,data};
		return combineBytes(preDataPacket);
	}
	
	//Follows RFC 1350
	private static byte[] buildErrPacket(Integer err, String emsg) {
		//Error Packet 
		byte[] opcode = {0,5}, errcode = {err.byteValue(), 0}, errMsg = emsg.getBytes(), padding = getPaddingByteArr();
		byte[][] combined = {opcode, errcode, errMsg, padding};
		return combineBytes(combined);
	}
	
	//==========================================================================================================
	
	private static byte[] getPaddingByteArr() {
		byte[] arr = {new Integer(0).byteValue()};
		return arr;
	}
	
	private static byte getPaddingByte() {
		return new Integer(0).byteValue();
	}
	
	private static byte[] buildOptsVals(byte[] opts, byte[] vals) {
		if(opts == null || vals == null) {
			return null;
		}else {
			if(opts.length != vals.length) {
				return null;
			}else {
				ArrayList<Byte> optsvals = new ArrayList<Byte>();
				for(int i = 0; i < opts.length; i++) {
					optsvals.add(opts[i]);
					optsvals.add(getPaddingByte());
					optsvals.add(vals[i]);
					optsvals.add(getPaddingByte());
				}
				byte[] rawoptsvals = new byte[optsvals.size()];
				Byte[] optsvalsArr = new Byte[optsvals.size()];
				optsvals.toArray(optsvalsArr);
				for(int i = 0; i < optsvals.size(); i++)
					rawoptsvals[i] = optsvalsArr[i].byteValue();
				return rawoptsvals;
			}
		}
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
