package utils;

import java.io.*;
import java.util.zip.*;

/**
 * Reference:
 * https://www.demo2s.com/java/java-compressing-byte-arrays.html
 * 
 * @author ESCALONA-LTP02
 *
 */

public class Compression {
	private Monitor m = new Monitor(true);
	private final String className = "Compression";
	/**
	 * Compresses the given byte[]
	 * @param input
	 * @param compressionLevel Can be 0-9 or either Deflater.BEST_COMPRESSION, Deflater.BEST_SPEED, Deflater.DEFAULT_COMPRESSION. 
	 * @param GZIPFormat Enable GZIPFormat or not
	 * @return
	 */
	public byte[] compress(byte[] input, int compressionLevel, boolean GZIPFormat){ 
		// Create a Deflater object to compress data  
        Deflater compressor = new Deflater(compressionLevel, GZIPFormat); 

        // Set the input for the compressor
        m.printMessage(this.className, "compress", "Setting compressor...");
        compressor.setInput(input);
        compressor.finish(); //Indicate that we have no more input for the compressor object
        input = null;

        // Compress the data 
        m.printMessage(this.className, "compress", "Preparing bao...");
        ByteArrayOutputStream bao = new ByteArrayOutputStream(); 
        byte[] readBuffer = new byte[512]; 

        m.printMessage(this.className, "compress", "Compressing byte[]...");
        while (!compressor.finished()) { 
            int readCount = compressor.deflate(readBuffer); 
            if (readCount > 0) { 
                // Write compressed data to the output stream  
                bao.write(readBuffer, 0, readCount); 
            } 
        } 
        readBuffer = null;
        
        // End the compressor  
        m.printMessage(this.className, "compress", "Ending compressor...");
        compressor.end(); 

        
        // Return the written bytes from output stream  
        m.printMessage(this.className, "compress", "Returning compressed byte[]...");
        return bao.toByteArray(); 
    }
	
	public byte[] decompress(byte[] input, boolean GZIPFormat){ 
		// Create an Inflater object to compress the data  
        Inflater decompressor = new Inflater(GZIPFormat); 

        // Set the input for the decompressor
        m.printMessage(this.className, "decompress", "Setting decompressor...");
        decompressor.setInput(input); 
        input = null;

        // Decompress data  
        m.printMessage(this.className, "decompress", "Preparing bao...");
        ByteArrayOutputStream bao = new ByteArrayOutputStream(); 
        byte[] readBuffer = new byte[512]; //the data at TFTP could be at most 512 bytes

        m.printMessage(this.className, "decompress", "Decompressing...");
        while (!decompressor.finished()) { 
            int readCount;
			try {
				readCount = decompressor.inflate(readBuffer);
				if (readCount > 0)
	                bao.write(readBuffer, 0, readCount); // Write the data to the output stream
			} catch (DataFormatException e) {
				m.printMessage(this.className, "decompress", "TryCatch: Decompression Error Occured");
			} 
        }
        readBuffer = null;

        // End the decompressor  
        m.printMessage(this.className, "decompress", "Ending decompressor...");
        decompressor.end(); 
        
        // Return the written bytes from the output stream  
        m.printMessage(this.className, "decompress", "Returning decompressed byte[]...");
        return bao.toByteArray(); 
    } 
}
