//https://simplesolution.dev/java-convert-file-to-byte-array/
//https://www.javatpoint.com/java-jfilechooser
//https://www.baeldung.com/java-write-byte-array-file
//https://www.programiz.com/java-programming/examples/get-file-extension
//https://stackoverflow.com/a/17011063
//https://mkyong.com/java/how-to-convert-array-of-bytes-into-file
package filebyte;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
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
     * @return File object of the selected file. Returns null if the user choice has cancelled or the file chooser experienced an error opening a file.
     */
    public File getFile(){
        JFileChooser fileChooser = new JFileChooser();
        int choice = fileChooser.showOpenDialog(null);
        if(choice == JFileChooser.APPROVE_OPTION)
            return fileChooser.getSelectedFile();
        else
            return null;
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
     * Return bytes as string using the specified charset.
     * @param bytes Bytes of data to be printed.
     * @param charsets Charset of the string. Use StandardCharsets.<FORMAT>
     * @return String formatted as charsets.
     */
    public String getCharsetContents(byte[] bytes, Charset charsets){
        return new String(bytes, charsets); 
    }

    /**
     * Shows UTF-8 Contents of the bytes[].
     * @param bytes byte[] to be printed in UTF-8.
     */
    public void showUTF8Contents(byte[] bytes){
        String fileContent = new String(bytes, StandardCharsets.UTF_8);
        System.out.println("File Content:");
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
     * @param path Path that points to the file. Formatted as absolute path.
     * @return File extension of the file pointed by the file path.
     */
    private String getFileExt(String path){
        int index = path.lastIndexOf('.');
        String extension = "";
        if(index > 0)
            extension = path.substring(index + 1);
        return extension;
    }

    /**
     * Splits the byte[] into chunks of size specified by the limit.
     * TFTP requires to send 512 byte chunks of payload with the terminating being
     * any payload size of less than 512 bytes.
     * @param filebytes byte[] of File to be split into chunks.
     * @param limit Chunk size of each byte[]
     * @return Returns an arrayList containing chunks of byte[] at specified limit
     */
    public ArrayList<byte[]> splitByBytes(byte[] filebytes, int limit){
        ArrayList<byte[]> compilation = new ArrayList<byte[]>();
        int l = 0;
        scratch = new byte[limit]; //assumes size of each chunk will reach limit
        
        if(filebytes.length < limit){ //check if size of filebytes < limit
            scratch = new byte[filebytes.length]; //set scratch to be of size filebytes[]
        }
        
        //run through every bit of filebytes and split according to chunk limit
        for(int i = 0; i < filebytes.length; i++){
            scratch[l] = filebytes[i];
            l++;
            if(l >= limit){ //checks chunk limit if it has been reached
                compilation.add(scratch);
                if(filebytes.length-i < limit){ //if remaining bytes are not equal to limit
                    scratch = new byte[filebytes.length-i-1];
                }else{
                    scratch = new byte[limit];
                }
                l = 0;
            }
        }

        compilation.add(scratch); //Add last scratch
        compilation.add(getTerminatingByte()); //TERMINATING BYTE (any payload of less than 512 for TFTP)
        return compilation;
    }

    public boolean isTerminating(byte[] sample){
        byte[] t = getTerminatingByte();
        if(sample.length  == t.length){
            for(int i = 0; i < sample.length; i++){
                if(sample[i] != t[i])
                    return false;
            }
            return true;
        }else{
            return false;
        }
    }

    public byte[] getTerminatingByte(){
        return new byte[1];   
    }
}