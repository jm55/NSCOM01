package data;

/**
 * References:
 * https://www.programiz.com/java-programming/examples/get-file-extension
 * https://stackoverflow.com/a/17011063
 * https://mkyong.com/java/how-to-convert-array-of-bytes-into-file
 * https://www.baeldung.com/java-write-byte-array-file
 */

import java.io.*;
import java.math.*;
import javax.swing.*;

import gui.GUI;
import utils.Utility;

public class FileHandlers {
	private File f;
	private Utility u = new Utility();
	private final String className = "FileHandlers";
	private final int BUFFER_SIZE = 512;
	
	public FileHandlers() {
		u.printMessage(className, "FileHandlers()", "Default constructor");
		this.f = null;
	}
	
	public FileHandlers(File f) {
		u.printMessage(className, "FileHandlers(File)", "Constructor with (File)");
		this.f = f;
	}
	
	/**
	 * Get File from User and assign it as this FileHandlers' file.
	 * @return True if successful or file does exist, false if otherwise.
	 */
	public boolean openFile() {
		u.printMessage(className, "openFile()", "Opening File...");
		this.f = openJFC();
		try {
			if(this.f.exists()) {
				u.printMessage(className, "openFile()", "Chosen file exists...");
				return true;
			}
		}catch(NullPointerException e) {
			u.printMessage(className, "openFile()", "NullPointerException: " + e.getLocalizedMessage());
		}
		u.printMessage(className, "openFile()", "Chosen file !exists...");
		return false;
	}
	
	/**
	 * Saves File to another File via FileByteStreaming
	 * Reference: https://www.codejava.net/java-se/file-io/java-io-fileinputstream-and-fileoutputstream-examples
	 * @param in Input File
	 * @param out Output File
	 * @return True if in is saved as out, false if otherwise.
	 */
	public boolean saveFileToFile(File in, File out) {
		if(in == null || out == null) //in || out is null.
			return false;
		if(!in.exists()) //Source file does not exist.
			return false;
		if(in.equals(out)) //Do not save to out if it is same as in.
			return false;
		try {
			if(!out.exists())
				out.createNewFile();
			InputStream inputStream = new FileInputStream(in.getAbsolutePath());
			OutputStream outputStream = new FileOutputStream(out.getAbsolutePath());
			
			int BUFFER_SIZE = 512, SIZE = inputStream.available(); //512 bytes max for TFTP
            byte[] buffer = new byte[BUFFER_SIZE];
            if(SIZE < BUFFER_SIZE)
            	buffer = new byte[SIZE];
            Integer bytesRead = -1;
            
            u.printMessage(className, "saveFileToFile(File, File)", "Streaming and Writing to file...");
            u.printMessage(className, "saveFileToFile(File, File)", "File total size: " + SIZE);
            while ((bytesRead = inputStream.read(buffer)) != -1) { //File Streaming
                u.writeMonitor(this.className, "saveFileToFile(File, File)", bytesRead, inputStream.available(), 2500, this.BUFFER_SIZE);
            	outputStream.write(buffer, 0, bytesRead);
            } 
            
		} catch (IOException e) {
			u.printMessage(this.className, "saveFileToFile(File, File)", "IOException: " + e.getLocalizedMessage());
		}
		
		return true;
	}
	
	/**
	 * Get File from User.
	 * @return File object if successful, null if otherwise.
	 */
	public File openAsFile() {
		u.printMessage(className, "openAsFile()", "Opening File...");
		return openJFC();
	}
	
	/**
	 * Get Save File from User.
	 * @return File object if successful, null if otherwise.
	 */
	public File saveFile() {
		return saveJFC();
	}
	
	/**
	 * Returns the File object of this FileHandlers.
	 * @return File object, returns null if no File was assigned.
	 */
	public File getFile() {
		u.printMessage(className, "getFile()", "Returning File...");
		return this.f;
	}
	
	/**
	 * Get the file extension of the FileHandlers' file.
	 * @param f File to be used.
	 * @return File extension of the FileHandlers' file.
	 */
	public String getFileExtension() {
		return getFileExtension(this.f);
	}
	
	/**
	 * Get the file extension of the specified file.
	 * @param f File to be used.
	 * @return File extension of the given File.
	 */
	public String getFileExtension(File f) {
		if(this.f != null) {
			if(f.exists()) {
				if(this.f.getName().contains("."))
					return this.f.getName().substring(this.f.getName().indexOf('.'));
			}
		}
		return null;
	}
	
	/**
	 * Get the file name of a this FileHandlers' file.
	 * @param f File to be used.
	 * @return File name of this FileHandlers' file, null if it does not exist.
	 */
	public String getFileName() {
		return getFileName(this.f);
	}
	
	/**
	 * Get the file name of a specified file.
	 * @param f File to be used.
	 * @return File name of the File, null if File does not exist.
	 */
	public String getFileName(File f) {
		if(this.f != null) {
			if(this.f.exists()) {
				u.printMessage(className, "getFileName(boolean)", "Get Filename...");
				return this.f.getName();
			}
		}
		return null;
	}
	
	/**
	 * Get the chunks of this FileHandlers' File object based on its byte count & intended chunk byte size.
	 * Uses RoundingMode.UP if value contains decimal.
	 * Example:
	 * 		File = 64bytes
	 * 		Chunksize = 8bytes
	 * 		Chunks = 64/8 = 8 chunks
	 * @param chunkSize Chunk or Split byte size of File.
	 * @return No. of chunks on FileHandlers' File object given it's chunk size, -1 if an exception occurs.
	 */
	public int getChunks(int chunkSize) {
		return getChunks(this.f, chunkSize);
	}
	
	/**
	 * Get the chunks of a File based on its byte count & intended chunk byte size.
	 * Uses RoundingMode.UP if value contains decimal.
	 * Example:
	 * 		File = 64bytes
	 * 		Chunksize = 8bytes
	 * 		Chunks = 64/8 = 8 chunks
	 * @param f File to be checked.
	 * @param chunkSize Chunk or Split byte size of File.
	 * @return No. of chunks on a file given it's chunk size, -1 if an exception occurs.
	 */
	public int getChunks(File f, int chunkSize) {
		try {
			InputStream inputStream = new FileInputStream(f.getAbsolutePath());
			Integer BUFFER_SIZE = chunkSize, SIZE = inputStream.available();
			return new BigDecimal((float)SIZE/(float)BUFFER_SIZE).setScale(2, RoundingMode.UP).intValue();
		} catch (IOException e) {
			u.printMessage(className, "getChunks(int)", "IOException: " + e.getLocalizedMessage());
			return -1;
		}
	}
	
	/**
	 * Returns the filesize of the file of this FileHandlers' object.
	 * @return File size of this FileHandlers' File object.
	 */
	public int getFileSize() {
		return getFileSize(this.f);
	}
	
	/**
	 * Returns the filesize of a given File.
	 * @param f File to be checked.
	 * @return File size of file in int or -1 if an exception occured.
	 */
	public int getFileSize(File f) {
		try {
			InputStream inputStream = new FileInputStream(this.f.getAbsolutePath());
			return inputStream.available();
		} catch (IOException e) {
			u.printMessage(className, "getChunks(int)", "IOException: " + e.getLocalizedMessage());
			return -1;
		}
	}
	
	/**
	 * Opens a JFileChooser as 'Save File' which the user can select a file to save data to.
	 * @return File object selected by user to save data to, or null if otherwise.
	 */
	private File saveJFC() {
		u.printMessage(className, "openJFC()", "Opening File...");
		try {
			JFileChooser jfc = new JFileChooser();
			if(jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
				u.printMessage(className, "openJFC()", "Save file approved!");
				if(jfc.getSelectedFile().exists()) {
					if(new GUI(false).confirmDialog("Overwrite existing file?", "File exists", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
						return jfc.getSelectedFile();
					}
				}else {
					u.printMessage(className, "openJFC()", "Selected file does not exist, creating file...");
					jfc.getSelectedFile().createNewFile();
					return jfc.getSelectedFile();
				}
			}else {
				u.printMessage(className, "saveJFC()", "Open file cancelled!");
			}
		}catch(NullPointerException e) {
			u.printMessage(className, "saveJFC()", "NullPointerException: " + e.getLocalizedMessage());
		} catch (IOException e) {
			u.printMessage(className, "saveJFC()", "IOException: " + e.getLocalizedMessage());
		}
		return null;
	}
	
	/**
	 * Opens a JFileChooser as 'Open File' which the user can select a file to open.
	 * @return File object selected by user to open, or null if otherwise.
	 */
	private File openJFC() {
		u.printMessage(className, "openJFC()", "Opening File...");
		try {
			JFileChooser jfc = new JFileChooser();
			if(jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				u.printMessage(className, "openJFC()", "Open file approved!");
				return jfc.getSelectedFile();
			}else {
				u.printMessage(className, "openJFC()", "Open file cancelled!");
				return null;
			}
		}catch(NullPointerException e) {
			u.printMessage(className, "openJFC()", "NullPointerException: " + e.getLocalizedMessage());
			return null;
		}
	}
}
