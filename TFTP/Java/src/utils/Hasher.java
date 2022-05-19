package utils;

import data.FileByte;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.File;

/**
 * Hasher contains all the necessary methods for hashing a given byte[] of a File.
 * 
 * Reference:
 * https://www.geeksforgeeks.org/sha-256-hash-in-java/
 * 
 * @author Escalona, Jose Miguel
 *
 */
public class Hasher {
	private Monitor m = new Monitor();
	private final String className = "Hasher";
	
	/**
	 * Quick compare two Files.
	 * Uses SHA-256.
	 * @param a File A
	 * @param b File B
	 * @return True if hashes match, false if otherwise.
	 */
	public boolean quickCompare(File a, File b) {
		FileByte fb = new FileByte();
		m.printMessage(this.className, "quickCompare", "Comparing...");
		return compareHash(fb.getBytesFromFile(a), fb.getBytesFromFile(b), "SHA-256");
	}
	
	/**
	 * Compares hash of a given byteA and String hashB using SHA-256 algorithm.
	 * Algorithms allowed: MD2, MD5, SHA-1, SHA-224, SHA-256, SHA-384, SHA-512
	 * @param byteA byte[] of A
	 * @param hashB Hash of B
	 * @return True if match, false if otherwise.
	 */
	public boolean compareHash(byte[] byteA, String hashB) {
		m.printMessage(this.className, "compareHash(byteA, hashB)", "Comparing...");
		return compareHash(byteA, hashB, "SHA-256");
	}
	
	/**
	 * Compares hash of a given byteA and String hashB
	 * Algorithms allowed: MD2, MD5, SHA-1, SHA-224, SHA-256, SHA-384, SHA-512
	 * @param byteA byte[] of A
	 * @param hashB Hash of B
	 * @param algorithm Specifies the algorithm to be used.
	 * @return True if match, false if otherwise.
	 */
	public boolean compareHash(byte[] byteA, String hashB, String algorithm){
		m.printMessage(this.className, "compareHash(byteA,hashB,algoritm)", "Comparing...");
		return hashB.equals(computeHash(byteA,algorithm));
	}
	
	/**
	 * Compares the hashes of byteA and byteB using SHA-256.
	 * @param byteA byte[] of A
	 * @param byteB byte[] of B
	 * @return True if match, false if otherwise.
	 */
	public boolean compareHash(byte[] byteA, byte[] byteB) {
		m.printMessage(this.className, "compareHash(byteA,byteB)", "Comparing...");
		return compareHash(byteA, byteB, "SHA-256");
	}
	
	/**
	 * Compares the hashes of byteA and byteB using the specified algorithm.
	 * Defaults to SHA-256 if algorithm specified is invalid.
	 * Algorithms allowed: MD2, MD5, SHA-1, SHA-224, SHA-256, SHA-384, SHA-512
	 * @param byteA byte[] of A
	 * @param byteB byte[] of B
	 * @param algorithm Specifies the algorithm to be used.
	 * @return True if match, false if otherwise.
	 */
	public boolean compareHash(byte[] byteA, byte[] byteB, String algorithm) {
		m.printMessage(this.className, "compareHash(byteA,byteB,algorithm)", "Comparing...");
		String hashA = performHash(byteA, algorithm);
		byteA = null;
		return hashA.equals(performHash(byteB, algorithm));
	}
	
	/**
	 * Delegates computation of hash given byte[] and algorithm.
	 * Invalid or null algorithm parameter defaults to use of SHA-256.
	 * Algorithms allowed: MD2, MD5, SHA-1, SHA-224, SHA-256, SHA-384, SHA-512
	 * @param byteA byte[] of A
	 * @param algorithm Specifies the algorithm to be used.
	 * @return True if match, false if otherwise.
	 */
	public String computeHash(byte[] bytes, String algorithm) {
		m.printMessage(this.className, "computeHash(bytes,algorithm)", "Computing hash...");
		String[] allowedAlgorithms = {"MD2", "MD5","SHA-1","SHA-224","SHA-256","SHA-384","SHA-512"};
		boolean validAlgorithm = false;
		String out = "";
		if(algorithm != null) {
			for(String a: allowedAlgorithms) {
				if(algorithm.toUpperCase().equals(a)) {
					validAlgorithm = true;
					out = performHash(bytes, algorithm);
				}
			}
			if(!validAlgorithm)
				out = performHash(bytes, "SHA-256"); //Defaults to SHA-256 if invalid algorithm was given.
		}else
			out = performHash(bytes, "SHA-256");
		
		bytes = null;
		System.gc();
		
		return out;
	}
	
	private String performHash(byte[] bytes, String algorithm) {
		m.printMessage(this.className, "performHash(bytes,algorithm)", "Hashing...");
		MessageDigest md;
		String hash = null;
		try {
			md = MessageDigest.getInstance(algorithm);
			hash = hashHex(md.digest(bytes));
		} catch (NoSuchAlgorithmException e) {
			m.printMessage(this.className, "performHash(bytes,algorithm)", "TryCatch: Specified hashing algorithm is invalid.");
		}
		
		System.gc();
		bytes = null;
		
		return hash;
	}
	
	private String hashHex(byte[] hash) {
		m.printMessage(this.className, "hashHex(hash)", "Building hashHex...");
		//Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);
        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));
        // Pad with leading zeros
        while (hexString.length() < 64)
        	hexString.insert(0, '0');
        m.printMessage(this.className, "hashHex(hash)", "Returning hashHex...");
        
        hash = null;
        System.gc();
        
        return hexString.toString();
	}
	
}