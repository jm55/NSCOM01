package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @deprecated
 * Used for checking hashes of files.
 * Reference: https://howtodoinjava.com/java/java-security/sha-md5-file-checksum-hash/
 */
public class Hasher {
	Utility u = new Utility();
	private final String className = "Hasher";
	
	public String getMD5(File f) {
		u.printMessage(this.className, "getMD5(File)", "Getting hash using MD5");
		return computeHash(f, "MD5");
	}
	public String getSHA256(File f) {
		u.printMessage(this.className, "getSHA256(File)", "Getting hash using SHA-256");
		return computeHash(f, "SHA-256");
	}
	public String getSHA1(File f) {
		u.printMessage(this.className, "getSHA1(File)", "Getting hash using SHA-1");
		return computeHash(f, "SHA-1");
	}
	private String computeHash(File f, String algo) {
		u.printMessage(this.className, "hashFile(File,String)", "Computing hash...");
		MessageDigest md;
		String output = "";
		FileInputStream stream;
		try {
			md = MessageDigest.getInstance(algo);
			stream = new FileInputStream(f);
			
			byte[] buffer = new byte[1024];
			int byteCount = -1;
			
			u.printMessage(this.className, "hashFile(File,String)", "Streaming file...");
			while((byteCount = stream.read(buffer)) != -1)
				md.update(buffer,0,byteCount);
			
			u.printMessage(this.className, "hashFile(File,String)", "Building hex...");
			byte[] bytes = md.digest();
			StringBuilder sb = new StringBuilder();
			for(byte b: bytes)
				sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
			output = sb.toString();
			stream.close();
		} catch (NoSuchAlgorithmException e) {
			u.printMessage(this.className, "hashFile(File,String)", "NoSuchAlgorithmException: " + e.getMessage());
		} catch (IOException e) {
			u.printMessage(this.className, "hashFile(File,String)", "IOException: " + e.getMessage());
		} catch(NullPointerException e) {
			u.printMessage(this.className, "hashFile(File,String)", "NullPointerException: " + e.getMessage());
		}
		
		u.printMessage(this.className, "hashFile(File,String)", "Computing hash completed!");
		return output;
	}
}
