package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Reference: https://howtodoinjava.com/java/java-security/sha-md5-file-checksum-hash/
 * @author Escalona, Jose Miguel
 */
public class Hasher {
	Monitor m = new Monitor();
	private final String className = "Hasher";
	
	public String getMD5(File f) {
		m.printMessage(className, "getMD5(File)", "Getting hash using MD5");
		return computeHash(f, "MD5");
	}
	public String getSHA256(File f) {
		m.printMessage(className, "getSHA256(File)", "Getting hash using SHA-256");
		return computeHash(f, "SHA-256");
	}
	public String getSHA1(File f) {
		m.printMessage(className, "getSHA1(File)", "Getting hash using SHA-1");
		return computeHash(f, "SHA-1");
	}
	private String computeHash(File f, String algo) {
		m.printMessage(className, "hashFile(File,String)", "Computing hash...");
		MessageDigest md;
		String output = "";
		try {
			md = MessageDigest.getInstance(algo);
			FileInputStream stream = new FileInputStream(f);
			
			byte[] buffer = new byte[1024];
			int byteCount = -1;
			
			m.printMessage(className, "hashFile(File,String)", "Streaming file...");
			while((byteCount = stream.read(buffer)) != -1)
				md.update(buffer,0,byteCount);
			
			m.printMessage(className, "hashFile(File,String)", "Building hex...");
			byte[] bytes = md.digest();
			StringBuilder sb = new StringBuilder();
			for(byte b: bytes)
				sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
			output = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			m.printMessage(className, "hashFile(File,String)", "NoSuchAlgorithmException: " + e.getLocalizedMessage());
		} catch (IOException e) {
			m.printMessage(className, "hashFile(File,String)", "IOException: " + e.getLocalizedMessage());
		} catch(NullPointerException e) {
			m.printMessage(className, "hashFile(File,String)", "NullPointerException: " + e.getLocalizedMessage());
		}
		m.printMessage(className, "hashFile(File,String)", "Computing hash completed!");
		return output;
	}
}
