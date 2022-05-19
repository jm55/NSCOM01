package data;

/**
 * This class file will handle file related functions.
 * 
 * 
 * References:
 * https://simplesolution.dev/java-convert-file-to-byte-array/
 * https://www.javatpoint.com/java-jfilechooser
 */

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import utils.*;

public class FileByte {
	private byte[] bytes;
	private Monitor m = new Monitor(true);
	private final String className = "FileByte";
	
	public FileByte(){
		this.bytes = new byte[0];
    }
	
	public FileByte(File f) {
		this.bytes = this.getBytesFromFile(f);
	}
	
	public FileByte(String path) {
		this.bytes = this.getBytesFromFilePath(path);
	}
	
	public FileByte(byte[] bytes) {
		this.bytes = bytes;
	}
	
	public void setBytes(File f) {
		m.printMessage(this.className, "setBytes(File)", "Setting bytes from File...");
		this.bytes = this.getBytesFromFile(f);
	}
	
	public void setBytes(String path) {
		m.printMessage(this.className, "setBytes(path)", "Setting bytes from path...");
		this.bytes = this.getBytesFromFilePath(path);
	}
	
	public void setBytes(byte[] bytes) {
		m.printMessage(this.className, "setBytes(byte[])", "Setting bytes from byte[]...");
		this.bytes = bytes;
	}

	/**
	 * Get byte[] held by this object.
	 * @return byte[] of this object.
	 */
	public byte[] getBytes() {
		m.printMessage(this.className, "getBytes()", "Returning this.bytes...");
		if(this.bytes != null)
			return this.bytes;
		return null;
	}
	
	/**
	 * Get byte[] from File.
	 * Does not assign as byte[] of this.
	 * @param file File where byte[] will be extracted
	 * @return Extracted byte[] from File.
	 */
    public byte[] getBytesFromFile(File file){
    	m.printMessage(this.className, "getBytesFromFile(File)", "Getting bytes from File...");
        return getBytesFromFilePath(file.getAbsolutePath());
    }

    /**
	 * Get byte[] from path of File.
	 * Does not assign as byte[] of this.
	 * Assumes absolute path was given.
	 * @param path Path of File where byte[] will be extracted
	 * @return Extracted byte[] from File pointed by path.
	 */
    public byte[] getBytesFromFilePath(String path){
    	m.printMessage(this.className, "getBytesFromFilePath(path)", "Getting bytes from path...");
        Path filePath = Paths.get(path);
        try {
			return Files.readAllBytes(filePath);
		} catch (IOException e) {
			System.out.println("Error retrieving bytes[] of specified File.");
			return null;
		}
    }

    /**
     * Prints the raw contents of the byte[] held by this object.
     */
    public void printRawContents(){
    	m.printMessage(this.className, "printRawContents()", "Printing raw contents of this.byte...");
        printRawContents(this.bytes);
    }
    
    /**
     * Prints the raw contents of the specified byte[].
     * @param bytes byte[] to be printed in raw.
     */
    public void printRawContents(byte[] bytes) {
    	m.printMessage(this.className, "printRawContents(bytes)", "Printing raw contents of bytes...");
    	for(byte b: bytes)
    		System.out.print(b);
    	System.out.println("");
    }
    
    /**
     * Returns the charSet equivalent of the byte[] of this object.
     * Valid Charset: Items found on StandardCharset
     * @param charsets Charset interpretation of the byte[].
     * @return Charset string equivalent of byte[] of this.
     */
    public String getCharsetContents(Charset charsets){
    	m.printMessage(this.className, "getCharsetContents(charsets)", "Printing charset interpretation of this.bytes...");
    	return getCharsetContents(this.bytes, charsets);
    }
    
    /**
     * eturns the charSet equivalent of the specified byte[].
     * Valid Charset: Items found on StandardCharset
     * @param bytes byte[] to be interpreted as Charset
     * @param charsets Charset interpretation of the byte[].
     * @return Charset string equivalent of byte[] specified
     */
    public String getCharsetContents(byte[] bytes, Charset charsets){
    	m.printMessage(this.className, "getCharsetContents(bytes, charsets)", "Printing charset interpretation of this bytes...");
        return new String(bytes, charsets); 
    }
    
    /**
     * Splits the bytes of byte[] of this.
     * @param limit byte[] size limit of each chunk split.
     * @return ArrayList byte[] of the split byte[] of this.
     */
    public ArrayList<byte[]> splitByBytes(int limit){
    	m.printMessage(this.className, "splitByBytes(limit)", "Spliting this.bytes @ " + limit + "...");
    	return splitByBytes(this.bytes, limit);
    }

    /**
     * Splits the bytes specified in filebytes[].
     * @param filebytes byte[] to be split.
     * @param limit byte[] size limit of each chunk split.
     * @return ArrayList byte[] of the split filebytes[].
     */
    public ArrayList<byte[]> splitByBytes(byte[] filebytes, int limit){
    	m.printMessage(this.className, "splitByBytes(filebytes, limit)", "Spliting filebytes @ " + limit + "...");
        ArrayList<byte[]> compilation = new ArrayList<byte[]>();
        int l = 0;
        byte[] scratch = new byte[limit]; //assumes size of each chunk will reach limit
        
        m.printMessage(this.className, "splitByBytes(filebytes, limit)", "Checking if filebytes.length is < " + limit + "...");
        if(filebytes.length < limit){ //check if size of filebytes < limit
        	m.printMessage(this.className, "splitByBytes(filebytes, limit)", "filebytes.length <" + limit + "...");
        	scratch = new byte[filebytes.length]; //set scratch to be of size filebytes[]
        }
        
        //run through every bit of filebytes and split according to chunk limit
        m.printMessage(this.className, "splitByBytes(filebytes, limit)", "Spliting filebytes...");
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

        m.printMessage(this.className, "splitByBytes(filebytes, limit)", "Finalizing split...");
        compilation.add(scratch); //Add last scratch
        compilation.add(getTerminatingByte()); //TERMINATING BYTE (any payload of less than 512 for TFTP)
        
        filebytes = scratch = new byte[0];
        m.printMessage(this.className, "splitByBytes(filebytes, limit)", "Returning split...");
        return compilation;
    }

    /**
     * Checks if the sample byte[] is the terminating byte[].
     * Not the one for TFTP protocol but more of an internal filebyte management.
     * @param sample byte[] to be sampled.
     * @return True if equal to terminating byte[], false if otherwise.
     */
    public boolean isTerminating(byte[] sample){
        byte[] t = getTerminatingByte();
        //m.printMessage(this.className, "isTerminating(sample)", "Checking if terminating...");
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
    
    /**
     * Clears byte[] of object.
     */
    public void clearBytes() {
    	this.bytes = new byte[0];
    }

    /**
     * Reassembles collection as byte[], sets it as object's byte[], and returns it.
     * @param collection ArrayList byte[] to be reassembled.
     * @return byte[] of collection
     */
    public byte[] reassembleBytes(ArrayList<byte[]> collection) {
    	m.printMessage(this.className, "reassembleBytes(collection)", "Reassembling collection to this.bytes...");
    	this.bytes = directReassembleBytes(collection);
    	
    	System.gc();
    	collection = null;
    	
    	return this.bytes;
    }
    
    /**
     * Reassembles collection as byte[] and returns it.
     * @param collection ArrayList byte[] to be reassembled. 
     * @return byte[] of collection.
     */
    public byte[] directReassembleBytes(ArrayList<byte[]> collection){
    	m.printMessage(this.className, "reassembleBytes(collection)", "Reassembling collection...");
        if(collection.size() == 0 || collection == null) {
        	m.printMessage(this.className, "reassembleBytes(collection)", "collection is null...");
        	return null;
        }
        int totalPacketSize = 0, ctr = 0;
        for(int i = 0; i < collection.size(); i++)
            totalPacketSize += collection.get(i).length;
        byte[] receiveCompile = new byte[totalPacketSize-1];
        for(int i = 0; i < collection.size(); i++){
            if(!isTerminating(collection.get(i))){
                for(int j = 0; j < collection.get(i).length; j++){
                    receiveCompile[ctr] = collection.get(i)[j];
                    ctr++;
                }
            }
        }
        
        collection = null;
        System.gc();
        
        m.printMessage(this.className, "reassembleBytes(collection)", "Returning is reassebmled bytes...");
        return receiveCompile;
    }

    public byte[] getTerminatingByte(){
        return new byte[1];   
    }
}