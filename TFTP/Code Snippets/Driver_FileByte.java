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

        if(filebytes != null){
            fb.showContents(filebytes);
        }
    }
}
