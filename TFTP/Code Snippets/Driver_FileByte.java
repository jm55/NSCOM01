import filebyte.FileByte;

import java.io.IOException;

public class Driver_FileByte {
    public static void main(String[] args) throws IOException{
        System.out.println("Driver_FileByte");
        FileByte fb = new FileByte();
        String filePath = null;
        filePath = fb.getFilePath(false);

        byte[] filebytes = null;
        filebytes = fb.getAllBytes(filePath); 
        System.out.println("filebytes size: " + filebytes.length);

        fb.splitByBytes(filebytes, 512);

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

        System.exit(0);
    }
}
