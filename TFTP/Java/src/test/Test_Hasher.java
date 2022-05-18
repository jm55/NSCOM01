package test;

import data.FileHandlers;
import data.FileByte;
import utils.Hasher;
import utils.Monitor;

public class Test_Hasher {
	Hasher hs;
	public Test_Hasher() {
		hs = new Hasher();
		//testHash();
		//testCompareHash();
		testQuickCompare();
	}
	public void testQuickCompare() {
		FileHandlers fh = new FileHandlers();
		boolean match = hs.quickCompare(fh.openFile(), fh.openFile());
		System.out.println("QuickCompare Match: " + match);
	}
	
	public void testCompareHash() {
		FileByte fb = new FileByte();
		FileHandlers fh = new FileHandlers();
		
		System.out.println("Opening Files A&B...");
		byte[] a = fb.getBytesFromFile(fh.openFile());
		byte[] b = fb.getBytesFromFile(fh.openFile());
		
		System.out.println("Comparing hashes...");
		boolean match = hs.compareHash(a, b);
		a = b = null;
		
		System.out.println("Match: " + match);
		System.out.println("Compare Hash complete!");
	}
	
	public void testHash() {
		FileHandlers fh = new FileHandlers();
		System.out.println("Opening File...");
		
		FileByte fb = new FileByte(fh.openFile());
		System.out.println("Loading bytes...");
		byte[] fb_bytes = fb.getBytes();
		
		System.out.println("Computing hash...");
		System.out.println("File hash: "  + hs.computeHash(fb_bytes, "SHA-256"));
		System.out.println("Hash Compute complete!");
	}
}