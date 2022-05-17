/**
 * Driver for FileByte.java
 * 
 * Shows how it can be used.
 */

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;


public class Driver_FileByte {
    static FileByte fb;
    public static void main(String[] args) throws IOException{
        System.out.println("Driver_FileByte");
        fb = new FileByte();

        openFile(); //Sample of opening a File and turning it into bytes

        splitFile(fb.getAllBytes(fb.getFile())); //Sample of splitting a File to ArrayList<bytes[]> and then reassembling+saving it back to File

        saveFile(); //Sample of saving a byte[] into a File

        System.exit(0);
    }

    private static void saveFile(){
        /**
         * FOR THE DEMO OF fb.saveFile();
         * Objective is to save txt file containing LoremIpsum
         * File extension is expected to be given since it is implied to through TFTP.
         */
        
        String fileExtension = ".txt";
        byte[] saveFileByte = "LoremIpsum".getBytes();
        fb.showUTF8Contents(saveFileByte);

        if(fb.saveFile(saveFileByte, fileExtension))
            System.out.println("File write successful!");
        else
            System.out.println("File write !successful!");
    }

    private static void openFile() throws IOException{
        /**
         * FOR THE DEMO OF THE fb.getFilePath() and fb.getAllBytes()
         * Let's simulate open a file and print its filebyte length/size.
         */
        String filePath = null;
        filePath = fb.getFilePath(false);

        byte[] filebytes = null;
        filebytes = fb.getAllBytes(filePath); 
        System.out.println("filebytes size: " + filebytes.length);
    }

    private static void splitFile(byte[] filebytes){
        //SEND: SPLIT THE GIVEN 
        ArrayList<byte[]> split = fb.splitByBytes(filebytes, 512);
        filebytes = null;

        //RECEIVE: REASSEMBLE BYTES AND SAVE AS FILE
        fb.saveFile(fb.reassembleBytes(split), null); //Simulate writing collected byte[] into machine
        split = null; //just to free memory for demo
    }
}
