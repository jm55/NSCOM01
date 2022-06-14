package testing;


import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Scanner;

import data.*;
import network.Client;
import utils.*;

/**
 * Used for testing functions/methods before actual implementation.
 * Executable on its own.
 */
public class Scratch {
	private static Utility u = new Utility();
	private static final String className = "Scratch";
	private static Scanner scan = null;
	public static void main(String[] args) {
		u.setState(true);
		scan = new Scanner(System.in);
		long time_diff = RunScratch();
		System.out.print("Testing time elapsed: " + (double)(time_diff/1000) + "seconds");
		//System.gc();
		scan.close();
		System.exit(0);
	}
	
	public static long RunScratch() {
		TFTP t = new TFTP();
		System.out.println("Running scratch tester...");
		//Wireshark
		String hex_raw = "";
		byte[] hex = null;
		//System
		byte[] syspacket = null;
		String syshex = "";
		byte[] sysbyte = null;
		String[] opts = {"tsize"}, vals = {"81967"};
		
		String[] target = {"",""};
		System.out.print("Enter target IP: ");
		target[0] = scan.nextLine();
		System.out.print("Enter target port: ");
		target[1] = scan.nextLine();
		
		long start = System.currentTimeMillis();
		
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
		byte[] data_hexbyte = u.hexStringToByteArray(data);
		System.out.println("System: ");
		syspacket = t.getDataPacket(161,data_hexbyte);
		syshex  = u.getBytesHex(syspacket);
		System.out.println("System Hex from Processed Byte: " + syshex);
		sysbyte = u.hexStringToByteArray(syshex);
		System.out.println("System Bits: " + u.getBytesAsBits(sysbyte));
		//=============================================
		System.out.println("Wireshark: ");
		hex_raw = "000300a1" + data;
		hex = u.hexStringToByteArray(hex_raw);
		System.out.println("Wireshark Hex Raw: " + hex_raw);
		System.out.println("Wireshark Bits: " + u.getBytesAsBits(hex));
		
		System.out.println("\n\n");
		
		System.out.println("WRQ Packet Without Opts & Vals");
		System.out.println("System: ");
		syspacket = t.getWRQPacket(new File("test.png"), "octet", null, null);
		syshex  = u.getBytesHex(syspacket);
		System.out.println("System Hex from Processed Byte: " + syshex);
		sysbyte = u.hexStringToByteArray(syshex);
		System.out.println("System Bits: " + u.getBytesAsBits(sysbyte));
		//=============================================
		System.out.println("Wireshark: ");
		hex_raw = "0002746f74655f74696c742e6a7067006f6374657400";
		System.out.println("Wireshark Hex Raw: " + hex_raw);
		hex = u.hexStringToByteArray(hex_raw);
		System.out.println("RQHasOACK: " + t.RQHasOACK(hex));
		System.out.println("Wireshark Bits: " + u.getBytesAsBits(hex));
		
		System.out.println("\n\n");
		
		System.out.println("WRQ Packet");
		System.out.println("System: ");
		opts[0] = "tsize";
		vals[0] = "81967";
		syspacket = t.getWRQPacket(new File("test.png"), "octet", opts, vals);
		syshex  = u.getBytesHex(syspacket);
		System.out.println("System Hex from Processed Byte: " + syshex);
		sysbyte = u.hexStringToByteArray(syshex);
		System.out.println("System Bits: " + u.getBytesAsBits(sysbyte));
		//=============================================
		System.out.println("Wireshark: ");
		hex_raw = "0002746f74655f74696c742e6a7067006f63746574007473697a6500383139363700";
		System.out.println("Wireshark Hex Raw: " + hex_raw);
		hex = u.hexStringToByteArray(hex_raw);
		String[][] oacks = t.extractOACKFromRQ(hex);
		System.out.println("RQHasOACK: " + t.RQHasOACK(hex));
		System.out.println("extractOACKFromRQ: " + u.stringArrToString(oacks[0]) + ", " + u.stringArrToString(oacks[1]));
		System.out.println("Wireshark Bits: " + u.getBytesAsBits(hex));
		
		System.out.println("\n\n");
		
		System.out.println("RRQ Packet Without Opts & Vals");
		System.out.println("System: ");
		syspacket = t.getRRQPacket("nenechi.png", "octet", null, null);
		syshex  = u.getBytesHex(syspacket);
		System.out.println("System Hex from Processed Byte: " + syshex);
		sysbyte = u.hexStringToByteArray(syshex);
		System.out.println("System Bits: " + u.getBytesAsBits(sysbyte));
		//=============================================
		System.out.println("Wireshark: ");
		hex_raw = "00016e656e656368692e706e67006f6374657400";
		System.out.println("Wireshark Hex Raw: " + hex_raw);
		hex = u.hexStringToByteArray(hex_raw);
		System.out.println("RQHasOACK: " + t.RQHasOACK(hex));
		System.out.println("Wireshark Bits: " + u.getBytesAsBits(hex));
		
		System.out.println("\n\n");
		
		System.out.println("RRQ Packet");
		System.out.println("System: ");
		opts[0] = "tsize";
		vals[0] = "0";
		syspacket = t.getRRQPacket("nenechi.png", "octet", opts, vals);
		syshex  = u.getBytesHex(syspacket);
		System.out.println("System Hex from Processed Byte: " + syshex);
		sysbyte = u.hexStringToByteArray(syshex);
		System.out.println("System Bits: " + u.getBytesAsBits(sysbyte));
		//=============================================
		System.out.println("Wireshark: ");
		hex_raw = "00016e656e656368692e706e67006f63746574007473697a65003000";
		System.out.println("Wireshark Hex Raw: " + hex_raw);
		hex = u.hexStringToByteArray(hex_raw);
		System.out.println("Wireshark Bits: " + u.getBytesAsBits(hex));
		oacks = t.extractOACKFromRQ(hex);
		System.out.println("RQHasOACK: " + t.RQHasOACK(hex));
		System.out.println("extractOACKFromRQ: " + u.stringArrToString(oacks[0]) + ", " + u.stringArrToString(oacks[1]));
		
		System.out.println("\n\n");
		
		System.out.println("OACK Packet");
		System.out.println("System: ");
		opts[0] = "tsize";
		vals[0] = "81967";
		syspacket = t.getOACK(opts, vals);
		syshex  = u.getBytesHex(syspacket);
		System.out.println("System Hex from Processed Byte: " + syshex);
		sysbyte = u.hexStringToByteArray(syshex);
		System.out.println("System Bits: " + u.getBytesAsBits(sysbyte));
		//=============================================
		System.out.println("Wireshark: ");
		hex_raw = "00067473697a6500383139363700";
		System.out.println("Wireshark Hex Raw: " + hex_raw);
		hex = u.hexStringToByteArray(hex_raw);
		System.out.println("Wireshark Bits: " + u.getBytesAsBits(hex));
		oacks = t.extractOACK(hex);
		System.out.println("extractOACK: " + u.stringArrToString(oacks[0]) + ", " + u.stringArrToString(oacks[1]));
		
		
		System.out.println("\n\n");
		
		System.out.println("ACK Packet");
		System.out.println("System: ");
		syspacket = t.getACK(84);
		syshex  = u.getBytesHex(syspacket);
		System.out.println("System Hex from Processed Byte: " + syshex);
		sysbyte = u.hexStringToByteArray(syshex);
		System.out.println("System Bits: " + u.getBytesAsBits(sysbyte));
		//=============================================
		System.out.println("Wireshark: ");
		hex_raw = "00040054";
		System.out.println("Wireshark Hex Raw: " + hex_raw);
		hex = u.hexStringToByteArray(hex_raw);
		System.out.println("Wireshark Bits: " + u.getBytesAsBits(hex));
		System.out.println("isACK: " + t.isACK(hex));
		System.out.println("extractACK: Block " + t.extractACK(hex));
		
		System.out.println("\n\n");
		
		System.out.println("Data Packet");
		System.out.println("System: ");
		syspacket = t.getDataPacket(1,"hello world".getBytes());
		syshex  = u.getBytesHex(syspacket);
		System.out.println("System Hex from Processed Byte: " + syshex);
		sysbyte = u.hexStringToByteArray(syshex);
		System.out.println("System Bits: " + u.getBytesAsBits(sysbyte));
		//=============================================
		System.out.println("Wireshark: ");
		hex_raw = "0003000168656c6c6f20776f726c64";
		System.out.println("Wireshark Hex Raw: " + hex_raw);
		hex = u.hexStringToByteArray(hex_raw);
		System.out.println("Wireshark Bits: " + u.getBytesAsBits(hex));
		System.out.println("getOpCode: " + t.getOpCode(hex));
		System.out.println("Extract Data: " + u.getBytesAsBits(t.extractData(hex)));
		
		System.out.println("\n\n");
		
		System.out.println("Error Packet");
		System.out.println("System: ");
		syspacket = t.getErrPacket(1);
		syshex  = u.getBytesHex(syspacket);
		System.out.println("System Hex from Processed Byte: " + syshex);
		sysbyte = u.hexStringToByteArray(syshex);
		System.out.println("System Bits: " + u.getBytesAsBits(sysbyte));
		//=============================================
		System.out.println("Wireshark: ");
		hex_raw = "0005000146696c65206e6f7420666f756e640000";
		System.out.println("Wireshark Hex Raw: " + hex_raw);
		hex = u.hexStringToByteArray(hex_raw);
		System.out.println("Wireshark Bits: " + u.getBytesAsBits(hex));
		System.out.println("isError: " + t.isError(hex));
		String[] error = t.extractError(hex);
		System.out.println("Extract Error: " + error[0] + " = " + error[1]);

		System.out.println("\n\n");

		System.out.println("Extract Block Number");
		hex_raw = "0003000168656c6c6f20776f726c64";
		System.out.println("Hex Raw: " + hex_raw);
		System.out.println("Bits: " + u.getBytesAsBits(u.hexStringToByteArray(hex_raw)));
		System.out.println("Expected output: " + 1);
		int block = t.extractBlockNumber(u.hexStringToByteArray(hex_raw));
		System.out.println("System Result Block#: " + block);
		
		/***
		 * NETWORK RELATED ZONE
		 */
		
		System.out.println("\n\n");
		System.out.println("========================================");
		System.out.println("========THIS IS THE NETWORK ZONE========");
		System.out.println("========================================");
		System.out.println("\n\n");
		
		//FILES
		FileHandlers fh = new FileHandlers();
		File f = new File("test.png");
		
		//CONNECTION CONFIGURATION
		System.out.println("Target details: " + u.arrayToString(target));
		Client c = new Client(target[0],Integer.parseInt(target[1]),512);
		System.out.println("Target is online: " + c.targetIsOnline());
		
		//OPTS AND VALS
		String[] vals4 = {"0"};
		String[] opts4 = {"tsize"};
		try {vals4[0] = Files.size(f.toPath())+"";
		} catch (IOException e) {
			System.out.println("Scratch Network Related - OPTS & VALS: " + e.getLocalizedMessage());
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
		
		return System.currentTimeMillis() - start;
	}	
}