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
import utils.Monitor;

public class FileHandlers {
	private File f;
	private Monitor m = new Monitor();
	private final String className = "FileHandlers";
	
	public FileHandlers() {
		m.printMessage(className, "FileHandlers()", "Default constructor");
		this.f = null;
	}
	
	public FileHandlers(File f) {
		m.printMessage(className, "FileHandlers(File)", "Constructor with (File)");
		this.f = f;
	}
	
	public boolean openFile() {
		m.printMessage(className, "openFile()", "Opening File...");
		this.f = openJFC();
		try {
			if(this.f.exists()) {
				m.printMessage(className, "openFile()", "Chosen file exists...");
				return true;
			}
		}catch(NullPointerException e) {
			m.printMessage(className, "openFile()", "NullPointerException: " + e.getLocalizedMessage());
		}
		m.printMessage(className, "openFile()", "Chosen file !exists...");
		return false;
	}
	
	public File openAsFile() {
		m.printMessage(className, "openAsFile()", "Opening File...");
		return openJFC();
	}
	
	public File saveFile() {
		return saveJFC();
	}
	
	public File getFile() {
		m.printMessage(className, "getFile()", "Returning File...");
		return this.f;
	}
	
	public String getFileExtension() {
		return getFileExtension(this.f);
	}
	
	public String getFileExtension(File f) {
		if(this.f != null) {
			if(f.exists()) {
				if(this.f.getName().contains("."))
					return this.f.getName().substring(this.f.getName().indexOf('.'));
			}
		}
		return null;
	}
	
	public String getFileName() {
		return getFileName(this.f);
	}
	
	public String getFileName(File f) {
		if(this.f != null) {
			if(this.f.exists()) {
				m.printMessage(className, "getFileName(boolean)", "Get Filename...");
				return this.f.getName();
			}
		}
		return null;
	}
	
	public int getChunks(int chunkSize) {
		return getChunks(this.f, chunkSize);
	}
	
	public int getChunks(File f, int chunkSize) {
		try {
			InputStream inputStream = new FileInputStream(f.getAbsolutePath());
			Integer BUFFER_SIZE = chunkSize, SIZE = inputStream.available();
			return new BigDecimal((float)SIZE/(float)BUFFER_SIZE).setScale(2, RoundingMode.UP).intValue();
		} catch (IOException e) {
			m.printMessage(className, "getChunks(int)", "IOException: " + e.getLocalizedMessage());
			return 0;
		}
	}
	
	public int getFileSize() {
		return getFileSize(this.f);
	}

	public int getFileSize(File f) {
		try {
			InputStream inputStream = new FileInputStream(this.f.getAbsolutePath());
			return inputStream.available();
		} catch (IOException e) {
			m.printMessage(className, "getChunks(int)", "IOException: " + e.getLocalizedMessage());
			return 0;
		}
	}
	
	private File saveJFC() {
		m.printMessage(className, "openJFC()", "Opening File...");
		try {
			JFileChooser jfc = new JFileChooser();
			if(jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
				m.printMessage(className, "openJFC()", "Save file approved!");
				if(jfc.getSelectedFile().exists()) {
					if(new GUI(false).confirmDialog("Overwrite existing file?", "File exists", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
						return jfc.getSelectedFile();
					}
				}else {
					m.printMessage(className, "openJFC()", "Selected file does not exist, creating file...");
					jfc.getSelectedFile().createNewFile();
					return jfc.getSelectedFile();
				}
			}else {
				m.printMessage(className, "saveJFC()", "Open file cancelled!");
			}
		}catch(NullPointerException e) {
			m.printMessage(className, "saveJFC()", "NullPointerException: " + e.getLocalizedMessage());
		} catch (IOException e) {
			m.printMessage(className, "saveJFC()", "IOException: " + e.getLocalizedMessage());
		}
		return null;
	}
	
	private File openJFC() {
		m.printMessage(className, "openJFC()", "Opening File...");
		try {
			JFileChooser jfc = new JFileChooser();
			if(jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				m.printMessage(className, "openJFC()", "Open file approved!");
				return jfc.getSelectedFile();
			}else {
				m.printMessage(className, "openJFC()", "Open file cancelled!");
				return null;
			}
		}catch(NullPointerException e) {
			m.printMessage(className, "openJFC()", "NullPointerException: " + e.getLocalizedMessage());
			return null;
		}
	}
}
