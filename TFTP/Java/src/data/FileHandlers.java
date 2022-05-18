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

public class FileHandlers {
	File f;
	
	public FileHandlers() {
		System.out.println("FileHandlers");
		this.f = null;
	}
	
	/**
	 * Get the file path of the specified in the parameters.
	 * @param f File where the path will be extracted
	 * @param canonical Set as true if the path needed is in canonical, false if absolute.
	 * @return Path of File f.
	 */
    public String getFilePath(File f, boolean canonical){
        if(!canonical)
            return f.getAbsolutePath();
        try {
			return f.getCanonicalPath();
		} catch (IOException e) {
			System.out.println("Canonical path of File cannot be retrieved, returning absolute path instead.");
			return f.getAbsolutePath();
		}
    }
    
    /**
     * Get the file path of the FileHandler's File object.
     * @param canonical Set as true if the path needed is in canonical, false if absolute.
     * @return Path of the File in FileHandler. Null if File is null.
     */
    public String getFilePath(boolean canonical) {
    	return getFilePath(this.f, canonical);
    }
    
    /**
     * Get the file path of a file specified by user through JFileChooser, which
     * can be in either absolute or canonical form.
     * @param canonical Set as true if the path needed is in canonical, false if absolute.
     * @return Path of specified file in JFileChooser.
     */
    public String getNewFilePath(boolean canonical) {
    	return getFilePath(openFile(), canonical);
    }
    
    public void setFile(String path) {
    	Path p = Paths.get(path);
    	this.f = p.toFile();
    }
    
    public void setFile(File f) {
    	this.f = f;
    }
    
    /**
	 * Opens and returns a File object specified by user through through JFileChooser
	 * @return File opened or null if it fails.
	 */
	public File openFile() {
		JFileChooser fileChooser = new JFileChooser();
		int choice = fileChooser.showOpenDialog(null);
        if(choice == JFileChooser.APPROVE_OPTION)
        	this.f = fileChooser.getSelectedFile();
        else
        	this.f = null;
        return this.f;
	}
    
    /**
     * 
     * @param filebytes
     * @return
     */
    public boolean saveFile(byte[] filebytes) {
    	return saveFile(filebytes, null);
    }
    
    public boolean saveFile(byte[] filebytes, String extension){
        JFileChooser fChooser = new JFileChooser();
        fChooser.showSaveDialog(null);
        File file = fChooser.getSelectedFile();
        if(file.exists()){
            int choice = JOptionPane.showConfirmDialog(null, "Replace file?");
            if(choice == 0){ //choice being yes (0)
                return writeFile(filebytes, file, extension);
            }else //choice being no (1) or cancel (2)
                return false;
        }else{
            System.out.println("File does not exist, creating new file...");
            return writeFile(filebytes, file, extension);
        }
    }
    
    public String getFileExt() {
    	return getFileExt(this.f);
    }
    
    public String getFileExt(File file){
        return getFileExt(file.getAbsolutePath());
    }

    public String getFileExt(String path){
        int index = path.lastIndexOf('.');
        String extension = "";
        if(index > 0)
            extension = path.substring(index + 1);
        return extension;
    }
    
    private boolean writeFile(byte[] filebytes, File file, String extension){
        if(extension != null)
            file = new File(file.toString() + extension);
        
        try {
            if(!file.exists())
                file.createNewFile();
            Files.write(file.toPath(), filebytes);
            filebytes = null;
            this.f = file;
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
