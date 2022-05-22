package testing;

import java.io.*;
import java.nio.*;
import java.security.*;
import java.util.ArrayList;

import data.*;
import network.*;
import utils.*;


public class Scratch {
	private static Utility m = new Utility();
	private static final String className = "Scratch";
	public static void main(String[] args) {
		m.setState(true);
		RunScratch();
		//System.gc();
		System.exit(0);
	}
	
	public static void RunScratch() {
		System.out.println("Running scratch...");
	}
	
}