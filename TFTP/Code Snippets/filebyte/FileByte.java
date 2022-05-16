//https://simplesolution.dev/java-convert-file-to-byte-array/
//https://www.javatpoint.com/java-jfilechooser  

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
    public FileByte(){
        System.out.println("FileByte");
    }

    public String getFilePath(boolean canonical) throws IOException{
        if(!canonical)
            return getFile().getAbsolutePath();
        return getFile().getCanonicalPath();
    }

    public File getFile(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.showOpenDialog(null);
        return fileChooser.getSelectedFile();
    }

    public byte[] getAllBytes(String path) throws IOException{
        Path filePath = Paths.get(path);
        return Files.readAllBytes(filePath);
    }

    public void showRawContents(byte[] bytes){
        for(byte b: bytes){
            System.out.print(b);
        }
    }

    public void showContents(byte[] bytes){
        String fileContent = new String(bytes, StandardCharsets.UTF_8);
        System.out.println("\nFile Content:");
        System.out.println(fileContent);
    }
}