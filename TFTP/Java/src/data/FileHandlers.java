package data;

/**
 * References:
 * https://www.programiz.com/java-programming/examples/get-file-extension
 * https://stackoverflow.com/a/17011063
 * https://mkyong.com/java/how-to-convert-array-of-bytes-into-file
 * https://www.baeldung.com/java-write-byte-array-file
 */

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import utils.Monitor;

public class FileHandlers {
	private File f;
	private Monitor m = new Monitor();
	private final String className = "FileHandlers";
	
	public FileHandlers() {
		this.f = null;
	}
	
	public FileHandlers(File f) {
		this.f = f;
	}
	
	public FileHandlers(String path) {
		if(new File(path).exists()) {
			m.printMessage(this.className, "FileHandlers(path)", "File pointed by path exists...");
			this.f = new File(path);
		}else
			m.printMessage(this.className, "FileHandlers(path)", "File pointed by path does not exist...");
	}
	
	/**
	 * Returns this File object.
	 * @return File set for FileHandlers.
	 */
	public File getFile() {
		m.printMessage(this.className, "getFile", "Returning this.f ...");
		return this.f;
	}
	
	public byte[] getFileAsBytes() {
		return new FileByte().getBytesFromFile(this.f);
	}
	
	/**
	 * Get the file path of the specified in the parameters.
	 * @param f File where the path will be extracted
	 * @param canonical Set as true if the path needed is in canonical, false if absolute.
	 * @return Path of File f.
	 */
    public String getFilePath(File f, boolean canonical){
    	m.printMessage(this.className, "getFilePath(File, canonical)", "Getting filepath (" + canonical + ")...");
        if(!canonical)
            return f.getAbsolutePath();
        try {
			return f.getCanonicalPath();
		} catch (IOException e) {
			m.printMessage(this.className, "getFilePath(File, canonical)", "TryCatch: Canonical path of File cannot be retrieved, returning absolute path instead.");
			return f.getAbsolutePath();
		}
    }
    
    /**
     * Get the file path of the FileHandler's File object.
     * @param canonical Set as true if the path needed is in canonical, false if absolute.
     * @return Path of the File in FileHandler. Null if File is null.
     */
    public String getFilePath(boolean canonical) {
    	m.printMessage(this.className, "getFilePath(canonical)", "Getting filepath of this.f (" + canonical + ")...");
    	return getFilePath(this.f, canonical);
    }
    
    /**
     * Get the file path of a file specified by user through JFileChooser, which
     * can be in either absolute or canonical form.
     * @param canonical Set as true if the path needed is in canonical, false if absolute.
     * @return Path of specified file in JFileChooser.
     */
    public String getNewFilePath(boolean canonical) {
    	m.printMessage(this.className, "getNewFilePath(canonical)", "Getting filepath of new File (" + canonical + ")...");
    	return getFilePath(openFile(), canonical);
    }
    
    /**
     * Set this File f from path.
     * @param path Path of File to be set to this.
     */
    public void setFile(String path) {
    	m.printMessage(this.className, "setFile(path)", "Setting this.f ...");
    	Path p = Paths.get(path);
    	this.f = p.toFile();
    }
    
    /**
     * Set this File f from File f.
     * @param f File to be set as this.f
     */
    public void setFile(File f) {
    	m.printMessage(this.className, "setFile(File)", "Setting this.f ...");
    	this.f = f;
    }
    
    /**
	 * Opens and returns a File object specified by user through through JFileChooser.
	 * It also sets FileHandlers File object.
	 * If file selection fails (i.e. file selection was cancelled) it retains the File of this.
	 * @return File opened or null if it fails.
	 */
	public File openFile() {
		m.printMessage(this.className, "openFile()", "Opening file from JFileChooser...");
		JFileChooser fileChooser = new JFileChooser();
		int choice = fileChooser.showOpenDialog(null);
        if(choice == JFileChooser.APPROVE_OPTION) {
        	m.printMessage(this.className, "openFile()", "Set chosen file as this.f ...");
        	this.f = fileChooser.getSelectedFile();
        }else {
        	m.printMessage(this.className, "openFile()", "File selection failed, retaning this.f and returning null...");
        	return null;
        }	
        return this.f;
	}
	
	/**
	 * Saves the file of this object.
	 * Checks if this file is not null and exists before saving. 
	 * Returns false if not met.
	 * @return True if file is saved, false if otherwise.
	 */
	public boolean saveFile() {
		m.printMessage(this.className, "saveFile()", "saving this.f as new file");
		if(this.f != null) {
			if(this.f.exists())
				return saveFile(new FileByte().getBytesFromFile(this.f), null);
		}
		return false;
	}
	
    /**
     * Saves filebytes[] as a File through JFileChooser.
     * Assumes that the user will enter the file extension as part of the filename at the save dialog.
     * @param filebytes byte[] of file to be saved as File.
     * @return True if saving file successful or not.
     */
    public boolean saveFile(byte[] filebytes) {
    	m.printMessage(this.className, "saveFile(filebytes)", "Saving filebytes as file (null extension)...");
    	return saveFile(filebytes, null);
    }
    
    /**
     * Saves file directly without JFileChooser 
     * File specified through intended file path and extension
     * @param path File path to save
     * @param extension Extension of file
     * @return TRue if saving file is successful or not.
     */
    public boolean saveFile(String inputPath, String outputPath, String extension) {
    	m.printMessage(this.className, "saveFile(inputPath,outputPath,extension)","Saving file...");
    	if(outputPath == null || outputPath == "") {
    		m.printMessage(this.className, "saveFile(inputPath,outputPath,extension)", "outputPath missing, ending saveFile attempt");
    		return false;
    	}	
    	if(inputPath == null || inputPath == "") {
    		m.printMessage(this.className, "saveFile(inputPath,outputPath,extension)", "inputPath missing");
    		return writeFile(new FileByte().getBytesFromFile(this.f), new File(outputPath), extension);
    	}    		
    	m.printMessage(this.className, "saveFile(inputPath,outputPath,extension)", "Complete parameters");
    	return writeFile(new FileByte().getBytesFromFilePath(inputPath), new File(outputPath), extension);
    }
    
    /**
     * Save file of this at extension.
     * @param extension Extension of the file
     * @return True if saving file is successful or not.
     */
    public boolean saveFile(String extension) {
    	m.printMessage(this.className, "saveFile(extension)","Saving file...");
    	return saveFile(new FileByte().getBytesFromFile(this.f), extension);
    }
    
    /**
     * Save filebytes as file specified by outputPath and extension
     * @param filebytes byte[] of file to be saved.
     * @param outputPath 
     * @param extension Default file extension of file. Optional, assumes outputPath has extension
     * @return
     */
    public boolean saveFile(byte[] filebytes, String outputPath, String extension) {
    	m.printMessage(this.className, "saveFile(inputPath,outputPath,extension)","Quietly saving file...");
    	return writeFile(filebytes, new File(outputPath), extension);	
    }
    
    /**
     * Save filebytes to file specified by JFileChooser
     * @param filebytes bytes[] to turn into File
     * @param extension Intended file extension
     * @return True if saving file is successful or not
     */
    public boolean saveFile(byte[] filebytes, String extension){
    	m.printMessage(this.className, "saveFile(filebytes, extension)", "Saving filebytes as file (with extension)...");
        JFileChooser fChooser = new JFileChooser();
        fChooser.showSaveDialog(null);
        File file = fChooser.getSelectedFile();
        if(file.exists()){
        	m.printMessage(this.className, "saveFile(filebytes, extension)", "File exists, asking for rewrite...");
            int choice = JOptionPane.showConfirmDialog(null, "Replace file?");
            if(choice == 0){ //choice being yes (0)
            	m.printMessage(this.className, "saveFile(filebytes, extension)", "Overwriting file...");
                return writeFile(filebytes, file, extension);
            }else{ //choice being no (1) or cancel (2)
            	m.printMessage(this.className, "saveFile(filebytes, extension)", "Rewrite not selected, file saving failed...");
            	return false;
            }
        }else{
        	m.printMessage(this.className, "saveFile(filebytes, extension)", "File !exists, creating new file...");
            return writeFile(filebytes, file, extension);
        }
    }
    
    /**
     * Get the file extension of this object's File.
     * @return Extension of the 
     */
    public String getFileExt() {
    	m.printMessage(this.className, "getFileExt()", "Getting file extension of this.f ...");
    	return getFileExt(this.f);
    }
    
    /**
     * Get file extension from File.
     * Does not affect object's File.
     * @param file File object
     * @return Extension of file, returns null if File does not exist
     */
    public String getFileExt(File file){
    	m.printMessage(this.className, "getFileExt(file)", "Getting file extension of file...");
        if(file.exists())
        	return getFileExt(file.getAbsolutePath());
        return null;
    }

    /**
     * Get file extension from path of File.
     * Does not affect object's File.
     * @param path Path of File object
     * @return Extension of File, returns null if File pointed by path does not exist.
     */
    public String getFileExt(String path){
    	m.printMessage(this.className, "getFileExt(path)", "Getting file extension of File pointed by path...");
    	if(new File(path).exists()) {
    		int index = path.lastIndexOf('.');
            String extension = "";
            if(index > 0)
                extension = path.substring(index + 1);
            return "."+extension;
    	}
    	return null;
    }
    
    /**
     * Writes the actual File to the system.
     * @param filebytes byte[] to turn into a File.
     * @param file File object where filebytes will be written.
     * @param extension Extension of file (Optional)
     * @return True if file writing was success, false if otherwise.
     */
    private boolean writeFile(byte[] filebytes, File file, String extension){
    	m.printMessage(this.className, "writeFile(filebytes,file,extension)", "Writing file...");
    	if(filebytes == null) {
    		m.printMessage(this.className, "writeFile(filebytes,file,extension)", "filebytes is null");
    		return false;
    	}
    	if(filebytes.length == 0) {
    		m.printMessage(this.className, "writeFile(filebytes,file,extension)", "filebytes has length 0");
    		return false;
    	}
    	if(!file.getName().contains(".")) {
    		if(!extension.contains("."))
    			extension = "." + extension;
    		file = new File(file.toString() + extension);
    	}
    	boolean state = false;
        try {

        	m.printMessage(this.className, "saveFile(inputPath,outputPath,extension)","filebytes length = " + filebytes.length);
        	m.printMessage(this.className, "saveFile(inputPath,outputPath,extension)","filename = " + file.getName());
        	m.printMessage(this.className, "saveFile(inputPath,outputPath,extension)","extension = " + extension);
            if(!file.exists()) {
            	m.printMessage(this.className, "writeFile(filebytes,file,extension)", "file does not exist, Creating new file...");
            	file.createNewFile();
            }
            m.printMessage(this.className, "writeFile(filebytes,file,extension)", "Writing filebytes to file...");
            m.printMessage(this.className, "writeFile(filebytes,file,extension)", "Writing " + filebytes.length + " bytes to: " + file.getName());
            Files.write(file.toPath(), filebytes);
            filebytes = null;
            this.f = file;
            state = true;
        } catch (IOException e) {
        	m.printMessage(this.className, "writeFile(filebytes,file,extension)", "TryCatch: Error occured while writing file.");
        	filebytes = null;
        }
        System.gc();
        return state;
    }
}
