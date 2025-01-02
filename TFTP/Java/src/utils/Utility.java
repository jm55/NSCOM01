package utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import gui.GUI;

/**
 * Contains all utility functions for used by other class files.
 */
public class Utility {
	private static DateTimeFormatter datetimeFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	private static final String TEMP_OUTPUT_DIR = ".\\downloads\\"; //Saves on a temporary location for file
	
	public static void printMessage(String className, String methodName, String text) {
		System.out.print("[" + dtNow()  + "] " + className + "." + methodName);
		if(text.length() > 0)
			System.out.println(": " + text);
		else
			System.out.println("");
	}
	
	public static String getGUIConsoleMessage(String message){
		return dtNow() + ": " + message;
	}
	
	public static String dtNow() {
		return datetimeFormat.format(LocalDateTime.now());
	}
	
	public static byte[] hexStringToByteArray(String s) {
		byte[] b = new byte[s.length()/2];
		for (int i = 0; i < b.length; i++)
			b[i] = (byte)Integer.parseInt(s.substring(i * 2, (i * 2) + 2), 16);
		return b;
	}
	
	public static String getBytesAsBits(byte[] bytes, boolean space) {
		StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(String.format("%8s", Integer.toBinaryString(bytes[i] & 0xFF)).replace(' ', '0'));
        	if(space)
        		sb.append(' ');
        }
        return sb.toString();
	}
	
	public static String getBytesHex(byte[] bytes) {
		String out = "";
		for(byte b: bytes)
			out += Integer.toHexString(b);
		return out;
	}
	
	public static String getBytesString(byte[] bytes) {
		String out = "";
		for(byte b: bytes)
			out += b;
		return out;
	}
	
	public static void printBytes(byte[] bytes) {
		for(byte b: bytes)
			System.out.print(b);
		System.out.println("");
	}
	
	public static void printByteAsString(byte[] bytes) {
		System.out.println(new String(bytes, StandardCharsets.UTF_8)); 
	}
	
    public static void cls() {
    	String[] command = {"clear"};
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
            	Runtime.getRuntime().exec(command);
        } catch (IOException | InterruptedException ex) {
        	GUI.errorDialog(null, "IOException/InterruptedException occured. Unable to do cls().");
        }
    }
    
    public static String getTempOutPath(String filename) {
		return TEMP_OUTPUT_DIR + filename;
	}
    
    public static byte[] shortToByteArr(Short val) {
    	return ByteBuffer.allocate(2).putShort(val).array();
    }
    
    public static byte[] integerToByteArr(Integer val) {
    	return ByteBuffer.allocate(4).putInt(val).array();
    }
    
    public static byte[] byteToByteArr(byte val) {
    	return ByteBuffer.allocate(1).put(val).array();
    }
    
    public static String byteToHex(byte b) {
    	return Integer.toHexString(b & 0xFF);
    }
    
    public static byte[] ByteListToByteArr(ArrayList<Byte> list) {
    	byte[] b = new byte[list.size()];
    	for(int i = 0; i < b.length; i++)
    		b[i] = list.get(i).byteValue();
    	return b;
    }
    
    public static String[] StringListToStringArr(ArrayList<String> list) {
    	String[] b = new String[list.size()];
    	for(int i = 0; i < b.length; i++)
    		b[i] = list.get(i);
    	return b;
    }
    
    public static String stringArrToString(String[] arr) {
    	String out = "{";
    	for(int i = 0; i < arr.length; i++) {
    		if(i < arr.length-1)
    			out+= arr[i] + ",";
    		else
    			out+= arr[i] + "}";
    	}
    	return out;
    }
    
    public static String ByteListToString(ArrayList<Byte> list) {
    	String out = "";
    	for(int i = 0; i < list.size(); i++)
    		out += (char) list.get(i).byteValue();
    	return out;
    }
    
    public static void writeMonitor(String className, String method, int bytesRead, int remainingBytes, int threshold, int blocksize) {
		if(bytesRead == blocksize && remainingBytes <= threshold)
			printMessage(className, method, "Last " + threshold + " bytes...");
		if(bytesRead < blocksize)
			printMessage(className, method, "Last piece of data[]");
	}
    
    public static String arrayToString(String[] arr) {
    	String out = "[";
    	for(int i = 0; i < arr.length; i++) {
    		out += arr[i];
    		if(i < arr.length-1)
    			out += ", ";
    	}
    	out += "]";
    	return out;
    }
    
    public static void printOptsValsComparison(String className, String methodName, String[] opts, String[] vals, String[][] checking) {
    	printMessage(className, methodName, "Checking matches...");
		printMessage(className, methodName, "Sent opts: " + arrayToString(opts));
		printMessage(className, methodName, "Sent vals: " + arrayToString(vals));
		printMessage(className, methodName, "OACK opts: " + arrayToString(checking[0]));
		printMessage(className, methodName, "OACK vals: " + arrayToString(checking[1]));
    }
    
    public static byte[] trimPacket(DatagramPacket packet, String className, String methodName) {
    	byte[] trimmedPacket = new byte[packet.getLength()]; //TRIMMED RCV
		System.arraycopy(packet.getData(), packet.getOffset(), trimmedPacket, 0, packet.getLength());
		return trimmedPacket;
    }
    
    public static double percentageValue(int subtotal, long total) {
    	double raw_percent = (((double)subtotal/(double)total)*100);
    	return new BigDecimal(raw_percent).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
