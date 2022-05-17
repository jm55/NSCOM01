import filebyte.FileByte;

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

        //openFile();

        //function call simulates a file being received(by opening a file),
        //attempts to split it into 512 byte chunks,
        //save file according to user discretion (for validation)
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
        String filePath = null;
        filePath = fb.getFilePath(false);

        byte[] filebytes = null;
        filebytes = fb.getAllBytes(filePath); 
        System.out.println("filebytes size: " + filebytes.length);
    }

    private static void splitFile(byte[] filebytes){
        ArrayList<byte[]> split = fb.splitByBytes(filebytes, 512);
        ArrayList<Byte>rawBytesList = new ArrayList<Byte>();
        for(int i = 0; i < split.size(); i++){
            if(split.get(i).length == 512){
                for(int j = 0; j < split.get(i).length; j++)
                    rawBytesList.add(split.get(i)[j]);
            }else{
                System.out.println("System received terminating payload (size <512bytes).");
            }   
        }
        Byte[] rawBytes = rawBytesList.toArray(new Byte[rawBytesList.size()]);
        byte[] rawbytes = new byte[rawBytes.length]; //for Byte => byte conversion
        for(int i = 0;  i < rawBytes.length; i++)
            rawbytes[i] = rawBytes[i];
        fb.saveFile(rawbytes, null);
    }
}
