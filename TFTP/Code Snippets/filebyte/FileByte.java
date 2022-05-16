//https://simplesolution.dev/java-convert-file-to-byte-array/
//https://www.javatpoint.com/java-jfilechooser
//https://www.baeldung.com/java-write-byte-array-file
//https://www.programiz.com/java-programming/examples/get-file-extension
//https://stackoverflow.com/a/17011063
package filebyte;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import javax.swing.*;

public class FileByte {
    byte[] scratch;

    public FileByte(){
        System.out.println("FileByte");
    }

    /**
     * Get the filepath directly from JFileChooser.
     * @param canonical Set as true if getCanonicalPath(), false if getAbsolutePath();
     * @return Path of the file in String form
     * @throws IOException
     */
    public String getFilePath(boolean canonical) throws IOException{
        if(!canonical)
            return getFile().getAbsolutePath();
        return getFile().getCanonicalPath();
    }

    /**
     * Calls a JFileChooser and returns selected file as a File object.
     * @return File object of the selected file.
     */
    public File getFile(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.showOpenDialog(null);
        return fileChooser.getSelectedFile();
    }

    /**
     * Gets all bytes of a given File object.
     * @param file File object from which the bytes[] will come from.
     * @return bytes[] of the File object.
     * @throws IOException
     */
    public byte[] getAllBytes(File file)throws IOException{
        return getAllBytes(file.getAbsolutePath());
    }

    /**
     * Gets all bytes of a given file's path.
     * @param path Absolute file path of the file from which the bytes[] will come from.
     * @return bytes[] of the file pointed by path.
     * @throws IOException
     */
    public byte[] getAllBytes(String path) throws IOException{
        Path filePath = Paths.get(path);
        return Files.readAllBytes(filePath);
    }

    /**
     * Shows the byte by byte content of the bytes.
     * @param bytes Byte array to be printed.
     */
    public void showRawContents(byte[] bytes){
        for(byte b: bytes){
            System.out.print(b);
        }
        System.out.println("");
    }

    /**
     * Shows UTF-8 Contents of the bytes[].
     * @param bytes byte[] to be printed in UTF-8.
     */
    public void showUTF8Contents(byte[] bytes){
        String fileContent = new String(bytes, StandardCharsets.UTF_8);
        System.out.println("\nFile Content:");
        System.out.println(fileContent);
    }

    /**
     * Delegates saving of a File from the byte[] given with the specified parameters.
     * @param filebytes byte[] to be turned into a file.
     * @param extension Specific extension of the file.
     * @return True if successful, false if not successful.
     */
    public boolean saveFile(byte[] filebytes, String extension){
        JFileChooser fChooser = new JFileChooser();
        fChooser.showSaveDialog(null);
        File file = fChooser.getSelectedFile();

        if(file.exists()){
            System.out.println("File exists");
            int choice = JOptionPane.showConfirmDialog(null, "Replace file?");
                    
            if(choice == 0){ //choice being yes (0)
                return writeFile(filebytes, file, extension);
            }else //choice being no (1) or cancel (2)
                return false;
        }else{
            System.out.println("File !exists, creating new file...");
            return writeFile(filebytes, file, extension);
        }
    }

    /**
     * Actually writes the file to the system.
     * File extension is specified through the extension parameter
     * @param filebytes byte[] to be turned into File.
     * @param file File object that was specified.
     * @param extension Specific intended file extension of the file. Set as null if file extension is attached to the path/filename itself already or there is no need to specify fileExt.
     * @return True if successfully written, false if otherwise.
     */
    private boolean writeFile(byte[] filebytes, File file, String extension){
        System.out.println("writeFile = " + file.toString());
        if(extension != null)
            file = new File(file.toString() + extension);
        try {
            if(!file.exists())
                file.createNewFile();
        }catch(IOException e) {
            return false;
        }
        
        try {
            System.out.println("File Extension: " + getFileExt(file.getAbsolutePath()));
            Files.write(file.toPath(), filebytes);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Gets the file extension of a given File.
     * @param file File object to get the file extension from.
     * @return File extension of the file pointed by the file path.
     */
    private String getFileExt(File file){
        return getFileExt(file.getAbsolutePath());
    }

    /**
     * Gets the file extension of a given file path.
     * @param path 
     * @return File extension of the file pointed by the file path.
     */
    private String getFileExt(String path){
        int index = path.lastIndexOf('.');
        String extension = "";
        if(index > 0)
            extension = path.substring(index + 1);
        return extension;
    }

    public ArrayList<byte[]> splitByBytes(byte[] filebytes, int limit){

        ArrayList<byte[]> compilation = new ArrayList<byte[]>();
        int l = 0;
        scratch = new byte[512];
        
        System.out.println("filebytes:scratch = " + filebytes.length + " : " + scratch.length);
        System.out.println("batches: " + filebytes.length/scratch.length);

        for(int i = 0; i < filebytes.length; i++){
            scratch[l] = filebytes[i];
            l++;
            if(l >= limit){
                compilation.add(scratch);
                scratch = new byte[512];
                l = 0;
            }
        }
        
        System.out.println("confirmed batches: " + compilation.size());
        return compilation;
    }

    public byte[] getSingleByte(){
        return new byte[1];   
    }
}