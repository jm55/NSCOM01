package utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;

/**
 * Contains all utility functions for used by other class files.
 */
public class Utility {
	private static boolean state = false;
	private DateTimeFormatter datetimeFormat;
	private final String TEMP_OUTPUT_DIR = ".\\downloads\\"; //Saves on a temporary location for file
	public Utility() {
		datetimeFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	}
	/**
	 * Sets the state for verbose printing (via printMessage()).
	 * @param state True or False
	 */
	public void setState(boolean state) {
		Utility.state = state;
	}
	/**
	 * Prints the message for diagnostics.
	 * @param className Class name who called the print out
	 * @param methodName Method name who called the print out
	 * @param text Details about the verbose print out
	 */
	public void printMessage(String className, String methodName, String text) {
		if(state){
			if(text.length() > 0)
				System.out.println("(" + dtNow()  + ") " + className + "." + methodName + ": " + text);
			else
			System.out.println("(" + dtNow()  + ") " + className + "." + methodName);
		}
			
	}
	/**
	 * Print out format for GUI's console.
	 * @param message Message to be printed in the GUI console.
	 * @return Formatted GUI console printout containing the time the message was created.
	 */
	public String getGUIConsoleMessage(String message){
		return dtNow() + ": " + message;
	}
	/**
	 * Returns a formatted datetime object in String form.
	 * @return Formatted datetime in format yyyy/MM/dd HH:mm:ss
	 */
	public String dtNow() {
		return datetimeFormat.format(LocalDateTime.now());
	}
	/**
	 * Returns byte[] equivalent of hex string.
	 * Only issue being that it ommits any beginning hex value of 00
	 * Reference: https://stackoverflow.com/a/8890335 > https://www.geeksforgeeks.org/java-program-to-convert-hex-string-to-byte-array/
	 * @param s Hex string
	 * @return byte[] equivalent of hex.
	 */
	public byte[] hexStringToByteArray(String s) {
		byte[] b = new byte[s.length()/2];
		for (int i = 0; i < b.length; i++) {
			int index = i * 2;
			// Using parseInt() method of Integer class
			int val = Integer.parseInt(s.substring(index, index + 2), 16);
			b[i] = (byte)val;
		 }
		return b;
	}
	/**
	 * Returns bit string equivalent of bytes.
	 * Example format: 00000001 00000010 00000011 ...
	 * Reference: https://stackoverflow.com/a/62318518
	 * @param bytes byte[] to be converted into bits
	 * @param space True if add space in between 8-bits, false if otherwise
	 * @return Bit string equivalent of bytes
	 */
	public String getBytesAsBits(byte[] bytes, boolean space) {
		StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(String.format("%8s", Integer.toBinaryString(bytes[i] & 0xFF)).replace(' ', '0'));
        	if(space)
        		sb.append(' ');
        }
        return sb.toString();
	}
	/**
	 * Returns Hex equivalent of bytes
	 * ISSUES TO OVERFLOW DETERMINED SO CHECKING IS AVAILABLE BUT RESULT NEEDS FURTHER EXAMINATION
	 * @param bytes byte[] to be converted into Hex
	 * @return Hex string equivalent of bytes
	 */
	public String getBytesHex(byte[] bytes) {
		String out = "";
		for(byte b: bytes)
			out += Integer.toHexString(b);
		return out;
	}
	/**
	 * Returns direct string equivalent of bytes
	 * @param bytes
	 * @return String equivalent of bytes.
	 */
	public String getBytesString(byte[] bytes) {
		String out = "";
		for(byte b: bytes)
			out += b;
		return out;
	}
	/**
	 * Prints bytes as string
	 * @param bytes
	 */
	public void printBytes(byte[] bytes) {
		for(byte b: bytes)
			System.out.print(b);
		System.out.println("");
	}
	/**
	 * Prints bytes as UTF-8 string
	 * @param bytes
	 */
	public void printByteAsString(byte[] bytes) {
		System.out.println(new String(bytes, StandardCharsets.UTF_8)); 
	}
	/**
	 * Clears screen.
	 * Works for Windows only
	 */
    public void cls() {
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                Runtime.getRuntime().exec("clear");
        } catch (IOException | InterruptedException ex) {}
    }
    /**
     * Returns a formatted temporary output path for a given filename.
     * The path is simply a folder named 'temp' in the same folder as the program itself.
     * @param filename Filename of the file.
     * @return Formatted temporary output path of file
     */
    public String getTempOutPath(String filename) {
		return this.TEMP_OUTPUT_DIR + filename;
	}
    /**
     * Returns byte[] equivalent of a Short value
     * @param val Short val
     * @return byte[] equivalent of val
     */
    public byte[] shortToByteArr(Short val) {
    	ByteBuffer buffer = ByteBuffer.allocate(2);
    	buffer.putShort(val);
    	return buffer.array();
    }
    /**
     * Returns byte[] equivalent of an Integer val
     * @param val Integer val
     * @return byte[] equivalent of val
     */
    public byte[] integerToByteArr(Integer val) {
    	ByteBuffer buffer = ByteBuffer.allocate(4);
    	buffer.putInt(val);
    	return buffer.array();
    }
    /**
     * Returns byte[] equivalent of val.
     * @param val byte val
     * @return byte[] equivalent of val. Mainly a single item byte[]
     */
    public byte[] byteToByteArr(byte val) {
    	ByteBuffer buffer = ByteBuffer.allocate(1);
    	buffer.put(val);
    	return buffer.array();
    }
    /**
     * Turns byte into hex (preventing overflow issue when directly reading bytes as int)
     * @param b byte to turn into hex
     * @return Hex string equivalent of b
     */
    public String byteToHex(byte b) {
    	int res = b & 0xFF;
    	return Integer.toHexString(res);
    }
    /**
     * Converts ArrayList<Byte> to byte[]
     * @param list ArrayList to be converted
     * @return byte[] equivalent of list.
     */
    public byte[] ByteListToByteArr(ArrayList<Byte> list) {
    	byte[] b = new byte[list.size()];
    	for(int i = 0; i < b.length; i++)
    		b[i] = list.get(i).byteValue();
    	return b;
    }
    /**
     * Converts ArrayList<String> to byte[]
     * @param list ArrayList to be converted
     * @return String[] equivalent of list.
     */
    public String[] StringListToStringArr(ArrayList<String> list) {
    	String[] b = new String[list.size()];
    	for(int i = 0; i < b.length; i++)
    		b[i] = list.get(i);
    	return b;
    }
    /**
     * Converts String[] as a String for printing
     * @param arr
     * @return
     */
    public String stringArrToString(String[] arr) {
    	String out = "{";
    	for(int i = 0; i < arr.length; i++) {
    		if(i < arr.length-1)
    			out+= arr[i] + ",";
    		else
    			out+= arr[i] + "}";
    	}
    	return out;
    }
    /**
     * Converts ArrayList<Byte> to String (UTF-8)
     * @param list
     * @return String content of list
     */
    public String ByteListToString(ArrayList<Byte> list) {
    	String out = "";
    	for(int i = 0; i < list.size(); i++) {
    		byte b = list.get(i).byteValue();
    		out += (char) b;
    	}
    	return out;
    }
    /**
     * For monitoring a byte scan reaches threshold
     * @param className Class name that called the function
     * @param method Method name that called the function
     * @param bytesRead Amount of bytes being read
     * @param remainingBytes Bytes left to read.
     * @param threshold Threshold limit for remaining bytes to trigger a print out
     * @param blocksize Expected byte size of bytesRead (TFTP default: 512)
     */
    public void writeMonitor(String className, String method, int bytesRead, int remainingBytes, int threshold, int blocksize) {
		//Confirmatory for 
		if(bytesRead == blocksize && remainingBytes <= threshold)
			printMessage(className, method, "Last " + threshold + " bytes...");
		if(bytesRead < blocksize)
			printMessage(className, method, "Last piece of data[]");
	}
    /**
     * Prints a String[] as a String
     * @param arr
     * @return String as '[a,b,...,c]'
     */
    public String arrayToString(String[] arr) {
    	String out = "[";
    	for(int i = 0; i < arr.length; i++) {
    		out += arr[i];
    		if(i < arr.length-1)
    			out += ", ";
    	}
    	out += "]";
    	return out;
    }
    
    /**
     * Prints the list of opts and vals for both the sent and received values
     * @param className
     * @param methodName
     * @param opts
     * @param vals
     * @param checking
     */
    public void printOptsValsComparison(String className, String methodName, String[] opts, String[] vals, String[][] checking) {
    	printMessage(className, methodName, "Checking matches...");
		printMessage(className, methodName, "Sent opts: " + arrayToString(opts));
		printMessage(className, methodName, "Sent vals: " + arrayToString(vals));
		printMessage(className, methodName, "OACK opts: " + arrayToString(checking[0]));
		printMessage(className, methodName, "OACK vals: " + arrayToString(checking[1]));
    }
   
    /**
     * Trims the packet to eliminate excess padding bytes if there are any.
     * @param packet
     * @param className
     * @param methodName
     * @return
     */
    public byte[] trimPacket(DatagramPacket packet, String className, String methodName) {
    	printMessage(className, methodName, "Trimming OACK packet...");
    	byte[] trimmedPacket = new byte[packet.getLength()]; //TRIMMED RCV
		System.arraycopy(packet.getData(), packet.getOffset(), trimmedPacket, 0, packet.getLength());
		return trimmedPacket;
    }
    
    public double percentageValue(int subtotal, long total) {
    	double raw_percent = (((double)subtotal/(double)total)*100);
    	BigDecimal bd = new BigDecimal(raw_percent).setScale(2, RoundingMode.HALF_UP);
    	return bd.doubleValue();
    }
}
