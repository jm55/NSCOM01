import filebyte.FileByte;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

import javax.sound.midi.SysexMessage;

public class Driver_FileByte {
    static FileByte fb;
    public static void main(String[] args) throws IOException{
        System.out.println("Driver_FileByte");
        fb = new FileByte();

        //openFile();

        /**
         * Function call simulates a file being received(by opening a file),
         * attempts to split it into 512 byte chunks,
         * save file according to user discretion (for validation)
         */
        splitFile(fb.getAllBytes(fb.getFile()));

        //saveFile();

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
        /**
         * Simulating how to split the filebytes[] into chunks of 512 bytes.
         * 
         */
        ArrayList<byte[]> split = fb.splitByBytes(filebytes, 512);
        //filebytes = null; //just to free memory for demo

        /**
         * Simulating receive through a for-loop
         * Maybe for TFTP, the payload size is on the headers of the data packet so no need to compute.
         * 
         * Imagine every byte[] of split is the byte[] sent/received on a UDP socket.
         * This shows the re-assembly process of byte[] into file.
         */
        int totalPacketSize = 0, ctr = 0;
        for(int i = 0; i < split.size(); i++)
            totalPacketSize += split.get(i).length;
        byte[] receiveCompile = new byte[totalPacketSize-1];
        for(int i = 0; i < split.size(); i++){
            if(!fb.isTerminating(split.get(i))){
                for(int j = 0; j < split.get(i).length; j++){
                    receiveCompile[ctr] = split.get(i)[j];
                    ctr++;
                }
            }
        }
        split = null; //just to free memory for demo
        fb.saveFile(receiveCompile, null); //Simulate writing collected byte[] into machine
    }

    private static void splitFileUDP(byte[] filebytes){

    }
}
