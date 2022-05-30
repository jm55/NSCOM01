package testing;


import java.io.*;
import java.nio.*;
import java.security.*;
import java.util.ArrayList;

import data.*;
import network.*;
import utils.*;


public class Scratch {
	private static Utility u = new Utility();
	private static final String className = "Scratch";
	public static void main(String[] args) {
		u.setState(true);
		RunScratch();
		//System.gc();
		System.exit(0);
	}
	
	public static void RunScratch() {
		TFTP t = new TFTP();
		System.out.println("Running scratch...\n\n");
		//Wireshark
		String hex_raw = "";
		byte[] hex = null;
		//System
		byte[] syspacket = null;
		String syshex = "";
		byte[] sysbyte = null;
		String[] opts = {"tsize"}, vals = {"81967"};
		
		System.out.println("Data Packet");
		String data = "9452d14009452d14009452d14009452d14009452d14009452d14009452d14009452d14009452d14009452d1401ffd9";
		byte[] data_hexbyte = u.hexStringToByteArray(data);
		System.out.println("System: ");
		syspacket = t.getDataPacket(161,data_hexbyte);
		syshex  = u.getBytesHex(syspacket);
		System.out.println(syshex);
		sysbyte = u.hexStringToByteArray(syshex);
		System.out.println(u.getBytesAsBits(sysbyte));
		//=============================================
		System.out.println("Wireshark: ");
		hex_raw = "000300a1" + data;
		hex = u.hexStringToByteArray(hex_raw);
		System.out.println(hex_raw);
		System.out.println(u.getBytesAsBits(hex));
		
		System.out.println("\n\n");
		
		System.out.println("WRQ Packet Without Opts & Vals");
		System.out.println("System: ");
		syspacket = t.getWRQPacket(new File("tote_tilt.jpg"), "octet", null, null);
		syshex  = u.getBytesHex(syspacket);
		System.out.println(syshex);
		sysbyte = u.hexStringToByteArray(syshex);
		System.out.println(u.getBytesAsBits(sysbyte));
		//=============================================
		System.out.println("Wireshark: ");
		hex_raw = "0002746f74655f74696c742e6a7067006f6374657400";
		System.out.println(hex_raw);
		hex = u.hexStringToByteArray(hex_raw);
		System.out.println(u.getBytesAsBits(hex));
		
		System.out.println("\n\n");
		
		System.out.println("WRQ Packet");
		System.out.println("System: ");
		opts[0] = "tsize";
		vals[0] = "81967";
		syspacket = t.getWRQPacket(new File("tote_tilt.jpg"), "octet", opts, vals);
		syshex  = u.getBytesHex(syspacket);
		System.out.println(syshex);
		sysbyte = u.hexStringToByteArray(syshex);
		System.out.println(u.getBytesAsBits(sysbyte));
		//=============================================
		System.out.println("Wireshark: ");
		hex_raw = "0002746f74655f74696c742e6a7067006f63746574007473697a6500383139363700";
		System.out.println(hex_raw);
		hex = u.hexStringToByteArray(hex_raw);
		System.out.println(u.getBytesAsBits(hex));
		
		System.out.println("\n\n");
		
		System.out.println("RRQ Packet Without Opts & Vals");
		System.out.println("System: ");
		syspacket = t.getRRQPacket("nenechi.png", "octet", null, null);
		syshex  = u.getBytesHex(syspacket);
		System.out.println(syshex);
		sysbyte = u.hexStringToByteArray(syshex);
		System.out.println(u.getBytesAsBits(sysbyte));
		//=============================================
		System.out.println("Wireshark: ");
		hex_raw = "00016e656e656368692e706e67006f6374657400";
		System.out.println(hex_raw);
		hex = u.hexStringToByteArray(hex_raw);
		System.out.println(u.getBytesAsBits(hex));
		
		System.out.println("\n\n");
		
		System.out.println("RRQ Packet");
		System.out.println("System: ");
		opts[0] = "tsize";
		vals[0] = "0";
		syspacket = t.getRRQPacket("nenechi.png", "octet", opts, vals);
		syshex  = u.getBytesHex(syspacket);
		System.out.println(syshex);
		sysbyte = u.hexStringToByteArray(syshex);
		System.out.println(u.getBytesAsBits(sysbyte));
		//=============================================
		System.out.println("Wireshark: ");
		hex_raw = "00016e656e656368692e706e67006f63746574007473697a65003000";
		System.out.println(hex_raw);
		hex = u.hexStringToByteArray(hex_raw);
		System.out.println(u.getBytesAsBits(hex));
		
		System.out.println("\n\n");
		
		System.out.println("OACK Packet");
		System.out.println("System: ");
		opts[0] = "tsize";
		vals[0] = "81967";
		syspacket = t.getOACK(opts, vals);
		syshex  = u.getBytesHex(syspacket);
		System.out.println(syshex);
		sysbyte = u.hexStringToByteArray(syshex);
		System.out.println(u.getBytesAsBits(sysbyte));
		//=============================================
		System.out.println("Wireshark: ");
		hex_raw = "00067473697a6500383139363700";
		System.out.println(hex_raw);
		hex = u.hexStringToByteArray(hex_raw);
		System.out.println(u.getBytesAsBits(hex));
		
		System.out.println("\n\n");
		
		System.out.println("ACK Packet");
		System.out.println("System: ");
		syspacket = t.getACK(84);
		syshex  = u.getBytesHex(syspacket);
		System.out.println(syshex);
		sysbyte = u.hexStringToByteArray(syshex);
		System.out.println(u.getBytesAsBits(sysbyte));
		//=============================================
		System.out.println("Wireshark: ");
		hex_raw = "00040054";
		System.out.println(hex_raw);
		hex = u.hexStringToByteArray(hex_raw);
		System.out.println(u.getBytesAsBits(hex));
		
		System.out.println("\n\n");
		
		System.out.println("Data Packet");
		System.out.println("System: ");
		syspacket = t.getDataPacket(1,"hello world".getBytes());
		syshex  = u.getBytesHex(syspacket);
		System.out.println(syshex);
		sysbyte = u.hexStringToByteArray(syshex);
		System.out.println(u.getBytesAsBits(sysbyte));
		//=============================================
		System.out.println("Wireshark: ");
		hex_raw = "0003000168656c6c6f20776f726c64";
		System.out.println(hex_raw);
		hex = u.hexStringToByteArray(hex_raw);
		System.out.println(u.getBytesAsBits(hex));
		
		System.out.println("\n\n");
		
		System.out.println("Error Packet");
		System.out.println("System: ");
		syspacket = t.getErrPacket(1);
		syshex  = u.getBytesHex(syspacket);
		System.out.println(syshex);
		sysbyte = u.hexStringToByteArray(syshex);
		System.out.println(u.getBytesAsBits(sysbyte));
		//=============================================
		System.out.println("Wireshark: ");
		hex_raw = "0005000146696c65206e6f7420666f756e640000";
		System.out.println(hex_raw);
		hex = u.hexStringToByteArray(hex_raw);
		System.out.println(u.getBytesAsBits(hex));
	}	
}