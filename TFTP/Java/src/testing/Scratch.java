package testing;


import java.io.*;
import java.nio.file.Files;
import java.util.Properties;
import java.util.Scanner;

import javax.swing.JOptionPane;

import data.*;
import gui.GUI;
import network.Client;
import utils.*;

/**
 * Used for testing functions/methods before actual implementation.
 * Executable on its own.
 */
public class Scratch {
	private static final String className = "Scratch";
	private static Scanner scan = null;
	private static String benchFile = "";
	public static void main(String[] args) {
		
		Properties p = System.getProperties();
    	String jre =  p.getProperty("java.version");
    	
    	if(jre.contains("."))
    		jre = jre.substring(0,jre.indexOf('.'));
    	
    	if(Integer.parseInt(jre) < 17) {
    		System.out.println("The program was not tested for the version of Java installed (version \" + jre +  \").\\nProgram requires at least Java 17.");
    		System.exit(0);
    	}
		
    	System.gc();
		
		scan = new Scanner(System.in);
		long time_diff = RunScratch();
		System.out.print("\nTesting time elapsed: " + (double)(time_diff/1000) + "seconds");
		//System.gc();
		scan.close();
		
		System.gc();
		System.exit(0);
	}
	
	public static long RunScratch() {
		
		System.out.println("Running scratch tester...");
		
		String[] target = {"",""};
		System.out.print("Enter target IP: ");
		target[0] = scan.nextLine();
		System.out.print("Enter target port: ");
		target[1] = scan.nextLine();
		System.out.print("Enter bench filename (including file extension): ");
		benchFile = scan.nextLine();
		
		long start = System.currentTimeMillis();
		
		packetAssemblyTest();
		networkTest(target);
		
		return System.currentTimeMillis() - start;
	}
	
	private static void packetAssemblyTest() {
		//Wireshark
		String hex_raw = "";
		byte[] hex = null;
		//System
		byte[] syspacket = null;
		String syshex = "";
		byte[] sysbyte = null;
		String[] opts = {"tsize"}, vals = {"81967"};
		
		/**
		 * PACKET ASSEMBLY ZONE
		 */
		
		System.out.println("\n\n");
		System.out.println("================================================");
		System.out.println("========THIS IS THE PACKET ASSEMBLY ZONE========");
		System.out.println("================================================");
		System.out.println("\n\n");

		//THIS TEST CONTAINS ISSUES ON BYTE TO HEX DECODING, THUS THE OUTPUTS MAY SEEM OF FROM THE ORIGINAL.
		//HOWEVER, SOME OF THE COMPONENTS FOR THE TRUE RESULT CAN BE FOUND (THOUGH OBSCURED) IN THE SYSTEM OUTPUT.
		System.out.println("Data Packet");
		String data = "9452d14009452d14009452d14009452d14009452d14009452d14009452d14009452d14009452d14009452d1401ffd9";
		byte[] data_hexbyte = Utility.hexStringToByteArray(data);
		System.out.println("System: ");
		syspacket = TFTP.getDataPacket(161,data_hexbyte);
		syshex  = Utility.getBytesHex(syspacket);
		System.out.println("System Hex from Processed Byte: " + syshex);
		sysbyte = Utility.hexStringToByteArray(syshex);
		System.out.println("System Bits: " + Utility.getBytesAsBits(sysbyte,true));
		//=============================================
		System.out.println("Wireshark: ");
		hex_raw = "000300a1" + data;
		hex = Utility.hexStringToByteArray(hex_raw);
		System.out.println("Wireshark Hex Raw: " + hex_raw);
		System.out.println("Wireshark Bits: " + Utility.getBytesAsBits(hex,true));
		
		System.out.println("\n\n");
		
		System.out.println("WRQ Packet Without Opts & Vals");
		System.out.println("System: ");
		syspacket = TFTP.getWRQPacket(new File("test.png"), "octet", null, null);
		syshex  = Utility.getBytesHex(syspacket);
		System.out.println("System Hex from Processed Byte: " + syshex);
		sysbyte = Utility.hexStringToByteArray(syshex);
		System.out.println("System Bits: " + Utility.getBytesAsBits(sysbyte,true));
		//=============================================
		System.out.println("Wireshark: ");
		hex_raw = "0002746f74655f74696c742e6a7067006f6374657400";
		System.out.println("Wireshark Hex Raw: " + hex_raw);
		hex = Utility.hexStringToByteArray(hex_raw);
		System.out.println("RQHasOACK: " + TFTP.RQHasOACK(hex));
		System.out.println("Wireshark Bits: " + Utility.getBytesAsBits(hex,true));
		
		System.out.println("\n\n");
		
		System.out.println("WRQ Packet");
		System.out.println("System: ");
		opts[0] = "tsize";
		vals[0] = "81967";
		syspacket = TFTP.getWRQPacket(new File("test.png"), "octet", opts, vals);
		syshex  = Utility.getBytesHex(syspacket);
		System.out.println("System Hex from Processed Byte: " + syshex);
		sysbyte = Utility.hexStringToByteArray(syshex);
		System.out.println("System Bits: " + Utility.getBytesAsBits(sysbyte,true));
		//=============================================
		System.out.println("Wireshark: ");
		hex_raw = "0002746f74655f74696c742e6a7067006f63746574007473697a6500383139363700";
		System.out.println("Wireshark Hex Raw: " + hex_raw);
		hex = Utility.hexStringToByteArray(hex_raw);
		String[][] oacks = TFTP.extractOACKFromRQ(hex);
		System.out.println("RQHasOACK: " + TFTP.RQHasOACK(hex));
		System.out.println("extractOACKFromRQ: " + Utility.stringArrToString(oacks[0]) + ", " + Utility.stringArrToString(oacks[1]));
		System.out.println("Wireshark Bits: " + Utility.getBytesAsBits(hex,true));
		
		System.out.println("\n\n");
		
		System.out.println("RRQ Packet Without Opts & Vals");
		System.out.println("System: ");
		syspacket = TFTP.getRRQPacket("nenechi.png", "octet", null, null);
		syshex  = Utility.getBytesHex(syspacket);
		System.out.println("System Hex from Processed Byte: " + syshex);
		sysbyte = Utility.hexStringToByteArray(syshex);
		System.out.println("System Bits: " + Utility.getBytesAsBits(sysbyte,true));
		//=============================================
		System.out.println("Wireshark: ");
		hex_raw = "00016e656e656368692e706e67006f6374657400";
		System.out.println("Wireshark Hex Raw: " + hex_raw);
		hex = Utility.hexStringToByteArray(hex_raw);
		System.out.println("RQHasOACK: " + TFTP.RQHasOACK(hex));
		System.out.println("Wireshark Bits: " + Utility.getBytesAsBits(hex,true));
		
		System.out.println("\n\n");
		
		System.out.println("RRQ Packet");
		System.out.println("System: ");
		opts[0] = "tsize";
		vals[0] = "0";
		syspacket = TFTP.getRRQPacket("nenechi.png", "octet", opts, vals);
		syshex  = Utility.getBytesHex(syspacket);
		System.out.println("System Hex from Processed Byte: " + syshex);
		sysbyte = Utility.hexStringToByteArray(syshex);
		System.out.println("System Bits: " + Utility.getBytesAsBits(sysbyte,true));
		//=============================================
		System.out.println("Wireshark: ");
		hex_raw = "00016e656e656368692e706e67006f63746574007473697a65003000";
		System.out.println("Wireshark Hex Raw: " + hex_raw);
		hex = Utility.hexStringToByteArray(hex_raw);
		System.out.println("Wireshark Bits: " + Utility.getBytesAsBits(hex,true));
		oacks = TFTP.extractOACKFromRQ(hex);
		System.out.println("RQHasOACK: " + TFTP.RQHasOACK(hex));
		System.out.println("extractOACKFromRQ: " + Utility.stringArrToString(oacks[0]) + ", " + Utility.stringArrToString(oacks[1]));
		
		System.out.println("\n\n");
		
		System.out.println("OACK Packet");
		System.out.println("System: ");
		opts[0] = "tsize";
		vals[0] = "81967";
		syspacket = TFTP.getOACK(opts, vals);
		syshex  = Utility.getBytesHex(syspacket);
		System.out.println("System Hex from Processed Byte: " + syshex);
		sysbyte = Utility.hexStringToByteArray(syshex);
		System.out.println("System Bits: " + Utility.getBytesAsBits(sysbyte,true));
		//=============================================
		System.out.println("Wireshark: ");
		hex_raw = "00067473697a6500383139363700";
		System.out.println("Wireshark Hex Raw: " + hex_raw);
		hex = Utility.hexStringToByteArray(hex_raw);
		System.out.println("Wireshark Bits: " + Utility.getBytesAsBits(hex,true));
		oacks = TFTP.extractOACK(hex);
		System.out.println("extractOACK: " + Utility.stringArrToString(oacks[0]) + ", " + Utility.stringArrToString(oacks[1]));
		
		
		System.out.println("\n\n");
		
		System.out.println("ACK Packet");
		System.out.println("System: ");
		syspacket = TFTP.getACK(84);
		syshex  = Utility.getBytesHex(syspacket);
		System.out.println("System Hex from Processed Byte: " + syshex);
		sysbyte = Utility.hexStringToByteArray(syshex);
		System.out.println("System Bits: " + Utility.getBytesAsBits(sysbyte,true));
		//=============================================
		System.out.println("Wireshark: ");
		hex_raw = "00040054";
		System.out.println("Wireshark Hex Raw: " + hex_raw);
		hex = Utility.hexStringToByteArray(hex_raw);
		System.out.println("Wireshark Bits: " + Utility.getBytesAsBits(hex,true));
		System.out.println("isACK: " + TFTP.isACK(hex));
		System.out.println("extractACK: Block " + TFTP.extractACK(hex));
		
		System.out.println("\n\n");
		
		System.out.println("Data Packet");
		System.out.println("System: ");
		syspacket = TFTP.getDataPacket(1,"hello world".getBytes());
		syshex  = Utility.getBytesHex(syspacket);
		System.out.println("System Hex from Processed Byte: " + syshex);
		sysbyte = Utility.hexStringToByteArray(syshex);
		System.out.println("System Bits: " + Utility.getBytesAsBits(sysbyte,true));
		//=============================================
		System.out.println("Wireshark: ");
		hex_raw = "0003000168656c6c6f20776f726c64";
		System.out.println("Wireshark Hex Raw: " + hex_raw);
		hex = Utility.hexStringToByteArray(hex_raw);
		System.out.println("Wireshark Bits: " + Utility.getBytesAsBits(hex,true));
		System.out.println("getOpCode: " + TFTP.getOpCode(hex));
		System.out.println("Extract Data: " + Utility.getBytesAsBits(TFTP.extractData(hex),true));
		
		System.out.println("\n\n");
		
		System.out.println("Error Packet");
		System.out.println("System: ");
		syspacket = TFTP.getErrPacket(1);
		syshex  = Utility.getBytesHex(syspacket);
		System.out.println("System Hex from Processed Byte: " + syshex);
		sysbyte = Utility.hexStringToByteArray(syshex);
		System.out.println("System Bits: " + Utility.getBytesAsBits(sysbyte,true));
		//=============================================
		System.out.println("Wireshark: ");
		hex_raw = "0005000146696c65206e6f7420666f756e640000";
		System.out.println("Wireshark Hex Raw: " + hex_raw);
		hex = Utility.hexStringToByteArray(hex_raw);
		System.out.println("Wireshark Bits: " + Utility.getBytesAsBits(hex,true));
		System.out.println("isError: " + TFTP.isError(hex));
		String[] error = TFTP.extractError(hex);
		System.out.println("Extract Error: " + error[0] + " = " + error[1]);

		System.out.println("\n\n");

		System.out.println("Extract Block Number");
		hex_raw = "0003000168656c6c6f20776f726c64";
		System.out.println("Hex Raw: " + hex_raw);
		System.out.println("Bits: " + Utility.getBytesAsBits(Utility.hexStringToByteArray(hex_raw),true));
		System.out.println("Expected output: " + 1);
		int block = TFTP.extractBlockNumber(Utility.hexStringToByteArray(hex_raw));
		System.out.println("System Result Block#: " + block);
	}
	
	private static void networkTest(String[] target) {
		/***
		 * NETWORK RELATED ZONE
		 */
		System.out.println("\n\n");
		System.out.println("========================================");
		System.out.println("========THIS IS THE NETWORK ZONE========");
		System.out.println("========================================");
		System.out.println("\n\n");
		
		//FILES
		File f = new File("test.png");
		
		//CONNECTION CONFIGURATION
		System.out.println("Target details: " + Utility.arrayToString(target));
		Client c = new Client(target[0],Integer.parseInt(target[1]),61001,512);
		System.out.println("Target is online: " + c.targetIsOnline());
		
		System.out.println("");
		
		//OPTS AND VALS
		String[] vals4 = {"0"};
		String[] opts4 = {"tsize"};
		try {vals4[0] = Files.size(f.toPath())+"";
		} catch (IOException e) {
			System.out.println("Scratch Network Related - OPTS & VALS: " + e.getMessage());
		}
		
		//WRITE TO SERVER
		System.out.println("File Details: " + f.getName() + " with size: " + vals4[0]);
		System.out.println("Writing To Server...");
		boolean state = c.send(f, opts4, vals4);
		if(state)
			System.out.println("Write to server successful");
		else
			System.out.println("Write to server failed");
		
		System.out.println("\n\n");
		
		//READ FROM SERVER
		vals4[0] = "0";
		opts4[0] = "tsize";
		System.out.println("File Details: " + f.getName());
		System.out.println("Reading From Server...");
		if(c.receive(f.getName(), "test_recv.jpg", opts4, vals4) != null)
			System.out.println("Read from server successful");
		else
			System.out.println("Read to server failed");
		
//		//SEND ERROR
//		try {
//			new Client().forceSend(new DatagramPacket(t.getErrPacket(1),t.getErrPacket(1).length), InetAddress.getByName("localhost"), 60001);
//		} catch (UnknownHostException e) {
//			System.out.println("Error occured on SEND ERROR: " + e.getMessage());
//		}
		
		System.out.println("\n\n");
		
		System.out.println("=================================");
		System.out.println("========NETWORK BENCHMARK========");
		System.out.println("=================================");
		
		String[] blksizes = {/*"128","512","1024","1428","2048","4096","8192","16384","32768",*/"65536"};
		String[] vals5 = {"0","0"};
		String[] opts5 = {"tsize","blksize"};
		File benchfile = new File(benchFile);
		
		if(!benchfile.exists())
			return;
		
		try {
			vals5[0] = Files.size(benchfile.toPath())+"";
		} catch (IOException e) {
			System.out.println("Scratch Network Related - OPTS & VALS: " + e.getMessage());
		}
		System.out.println("File Details: " + benchfile.getName() + " with size: " + vals5[0]);
		System.out.println("Benchmarking...");
				
		for(int i = blksizes.length-1; i >= 0; i--) {
			vals5[1] = blksizes[i];
			System.out.println("Config: ");
			System.out.println(Utility.arrayToString(opts5));
			System.out.println(Utility.arrayToString(vals5));
			
			String benchStart = Utility.dtNow();
			state = c.send(benchfile, opts5, vals5);
			
			if(state) {
				String benchEnd = Utility.dtNow();
				System.out.println("Benchmarking successful: " + benchStart + " - " + benchEnd);
			}
			else
				System.out.println("Benchmarking failed!");
		}
	}
}