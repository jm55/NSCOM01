package utils;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.BitSet;

public class Utility {
	private static boolean state = false;
	private DateTimeFormatter datetimeFormat;
	private final String TEMP_OUTPUT_DIR = ".\\temp\\"; //Saves on a temporary location for file
	public Utility() {
		datetimeFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	}
	public void setState(boolean state) {
		Utility.state = state;
	}
	public void printMessage(String className, String methodName, String text) {
		if(state)
			System.out.println("(" + dtNow()  + ") " + className + "." + methodName + ": " + text);
	}
	public String getGUIConsoleMessage(String message){
		return dtNow() + ": " + message;
	}
	public String dtNow() {
		return datetimeFormat.format(LocalDateTime.now());
	}
	public static byte[] hexStringToByteArray(String s) { //https://stackoverflow.com/a/8890335
		byte[] b = new BigInteger(s,16).toByteArray();
		return b;
	}
	public String getBytesAsBits(byte[] bytes) { //https://stackoverflow.com/a/62318518
		StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(String.format("%8s", Integer.toBinaryString(bytes[i] & 0xFF)).replace(' ', '0'));
        	sb.append(' ');
        }
        return sb.toString();
	}
	public String getBytesHex(byte[] bytes) {
		String out = "";
		for(byte b: bytes)
			out += Integer.toHexString(b);
		return out;
	}
	public String getBytesString(byte[] bytes) {
		String out = "";
		for(byte b: bytes)
			out += b;
		return out;
	}
	public void printBytes(byte[] bytes) {
		for(byte b: bytes)
			System.out.print(b);
		System.out.println("");
	}
	public void printByteAsString(byte[] bytes) {
		System.out.println(new String(bytes, StandardCharsets.UTF_8)); 
	}
    public void cls() {
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                Runtime.getRuntime().exec("clear");
        } catch (IOException | InterruptedException ex) {}
    }
    public String getTempOutPath(String filename) {
		return this.TEMP_OUTPUT_DIR + filename;
	}
    public byte[] shortToByteArr(Short val) {
    	ByteBuffer buffer = ByteBuffer.allocate(2);
    	buffer.putShort(val);
    	return buffer.array();
    }
    public byte[] integerToByteArr(Integer val) {
    	ByteBuffer buffer = ByteBuffer.allocate(4);
    	buffer.putInt(val);
    	return buffer.array();
    }
    public byte[] byteToByteArr(byte val) {
    	ByteBuffer buffer = ByteBuffer.allocate(1);
    	buffer.put(val);
    	return buffer.array();
    }
    public void writeMonitor(String className, String method, int bytesRead, int remainingBytes, int threshold) {
		//Confirmatory for 
		if(bytesRead == 512 && remainingBytes <= threshold)
			printMessage(className, method, "Last " + threshold + " bytes...");
		if(bytesRead < 512)
			printMessage(className, method, "Last piece of data[]");
	}
}
