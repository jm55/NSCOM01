package data;

import java.io.*;
import java.math.*;
import javax.swing.*;

import gui.GUI;
import utils.Utility;

/**
 * FileHandlers.java
 * 
 * Contains all file handling functionalities of the program.
 */
public class FileHandlers {
	private static final String className = "FileHandlers";
	private static final int BUFFER_SIZE = 512;
	
	/**
	 * Attempts to open a file using the JFileChooser.
	 * @return Selected file or null if none was selected.
	 */
	public static File openFile() {
		File f = openJFC();
		if(f.exists())
			return f;
		return null;
	}

	
	public static boolean saveFileToFile(File input, File output) {
		if(input == null || output == null) //in || out is null.
			return false;
		if(!input.exists() || input.equals(output)) //Source file does not exist or if File out is the same as File in.
			return false;
		try {
			if(!output.exists())
				output.createNewFile();
			InputStream inputStream = new FileInputStream(input.getAbsolutePath());
			OutputStream outputStream = new FileOutputStream(input.getAbsolutePath());
            byte[] BUFFER = new byte[BUFFER_SIZE];
            if(inputStream.available() < BUFFER_SIZE)
            	BUFFER = new byte[inputStream.available()];
            Integer bytesRead = -1;
            while ((bytesRead = inputStream.read(BUFFER)) != -1) //File Streaming
            	outputStream.write(BUFFER, 0, bytesRead);
            inputStream.close();
            outputStream.close();
		} catch (IOException e) {
			Utility.printMessage(className, "saveFileToFile(in,out)", "IOException: " + e.getMessage());
		}
		return true;
	}
	
	public static File openAsFile() {
		return openJFC();
	}
	
	public static File saveAsFile() {
		return saveJFC();
	}
	
	public static String getFileExtension(File f) {
		if(f != null && f.exists() && f.getName().contains("."))
			return f.getName().substring(f.getName().indexOf('.'));
		return null;
	}
	
	public static String getFileName(File f) {
		if(f != null && f.exists())
			return f.getName();
		return null;
	}
	
	public static int getChunks(File f, int chunkSize) {
		try {
			InputStream input = new FileInputStream(f.getAbsolutePath());
			Integer BUFFER_SIZE = chunkSize;
			int chunk = new BigDecimal((float)input.available()/(float)BUFFER_SIZE).setScale(2, RoundingMode.UP).intValue();
			input.close();
			return chunk;
		} catch (IOException e) {
			Utility.printMessage(className, "getChunks(f, chunkSize)", "IOException: " + e.getMessage());
			return -1;
		}
	}
	
	public static int getFileSize(File f) {
		try {
			InputStream input = new FileInputStream(f.getAbsolutePath());
			int available = input.available();
			input.close();
			return available;
		} catch (IOException e) {
			Utility.printMessage(className, "getChunks(f)", "IOException: " + e.getMessage());
			return -1;
		}
	}
	
	private static File saveJFC() {
		try {
			JFileChooser jfc = new JFileChooser();
			if(jfc.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) //User cancelled the dialog
				return null;
			if(jfc.getSelectedFile().exists()) //File already exists and about to be overwritten
				if(GUI.confirmDialog(null,"Overwrite existing file?", "File exists", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION)
					return jfc.getSelectedFile();
			if(jfc.getSelectedFile().createNewFile())
				return jfc.getSelectedFile();
		}catch(NullPointerException e) {
			Utility.printMessage(className, "saveJFC()", "NullPointerException: " + e.getMessage());
		} catch (IOException e) {
			Utility.printMessage(className, "saveJFC", "IOException: " + e.getMessage());
		}
		return null;
	}
	
	private static File openJFC() {
		try {
			JFileChooser jfc = new JFileChooser();
			if(jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				return jfc.getSelectedFile();
		}catch(NullPointerException e) {
			Utility.printMessage(className, "openJFC", "NullPointerException: " + e.getMessage());
			
		}
		return null;
	}
}
