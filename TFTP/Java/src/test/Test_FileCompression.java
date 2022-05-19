package test;

import java.util.ArrayList;
import java.util.zip.Deflater;

import data.*;
import utils.*;

public class Test_FileCompression {
	private String hashA, hashB;
	public Test_FileCompression() {
		System.out.println("Test_FileCompression");
		
		this.hashA = this.hashB = "";
		
		//Perform Send()
		System.out.println("PERFORMING SEND:");
		ArrayList<byte[]> packets = sender();
		System.gc();
		
		//Perform Receive();
		System.out.println("PERFORMING RECEIVE:");
		receive(packets);
		packets = new ArrayList<byte[]>(0);
		System.gc();
		
		//Confirmation: Check if hashes match
		System.out.println("CONFIRMATION:");
		System.out.println("HashA: " + this.hashA);
		System.out.println("HashB: " + this.hashB);
		System.out.println("Match: " + this.hashA.equals(this.hashB));
	}
	
	private ArrayList<byte[]> sender() {
		//Get File
		System.out.println("Getting File...");
		FileHandlers fh = new FileHandlers();
		fh.openFile();
		
		//Get FileBytes
		System.out.println("Creating byte[]...");
		FileByte fb = new FileByte(fh.getFile());
		fh = null;
		
		//Check Hash
		System.out.println("Computing hashA...");
		this.hashA = new Hasher().computeHash(fb.getBytes(),null);
		
		//Compress
		System.out.println("Compressing...");
		fb.setBytes(new Compression().compress(fb.getBytes(), Deflater.BEST_COMPRESSION, true));
		
		//Return Split/Disassembled 'Packets'
		System.out.println("Disassembling byte[] to 'packets'...");
		return fb.splitByBytes(512);
	}
	
	private void receive(ArrayList<byte[]> packets) {
		//Reassemble and Decompress
		System.out.println("Decompressing 'packets'...");
		byte[] receivedBytes = new Compression().decompress(new FileByte().directReassembleBytes(packets), true);
		packets = null;
		
		//Check Hash
		System.out.println("Computing hashB...");
		this.hashB = new Hasher().computeHash(receivedBytes,null);
		
		//Save byte[] as File
		System.out.println("Saving file...");
		new FileHandlers().saveFile(receivedBytes);
		receivedBytes = null;
	}
}
