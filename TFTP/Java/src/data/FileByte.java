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

public class FileByte {
	byte[] bytes = null;
	
	public FileByte(){
        System.out.println("FileByte");
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
		this.bytes = this.getBytesFromFile(f);
	}
	
	public void setBytes(String path) {
		this.bytes = this.getBytesFromFilePath(path);
	}
	
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public byte[] getBytes() {
		if(this.bytes != null)
			return this.bytes;
		return null;
	}
	
    public byte[] getBytesFromFile(File file){
        return getBytesFromFilePath(file.getAbsolutePath());
    }

    public byte[] getBytesFromFilePath(String path){
        Path filePath = Paths.get(path);
        try {
			return Files.readAllBytes(filePath);
		} catch (IOException e) {
			System.out.println("Error retrieving bytes[] of specified File.");
			return null;
		}
    }

    public void printRawContents(){
        printRawContents(this.bytes);
    }
    
    public void printRawContents(byte[] bytes) {
    	for(byte b: bytes)
    		System.out.print(b);
    	System.out.println("");
    }
    
    public String getCharsetContents(Charset charsets){
    	return getCharsetContents(this.bytes, charsets);
    }
    
    public String getCharsetContents(byte[] bytes, Charset charsets){
        return new String(bytes, charsets); 
    }
    
    public ArrayList<byte[]> splitByBytes(int limit){
    	return splitByBytes(this.bytes, limit);
    }

    public ArrayList<byte[]> splitByBytes(byte[] filebytes, int limit){
        ArrayList<byte[]> compilation = new ArrayList<byte[]>();
        int l = 0;
        byte[] scratch = new byte[limit]; //assumes size of each chunk will reach limit
        
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
        
        scratch = null;
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

    public byte[] reassembleBytes(ArrayList<byte[]> collection){
        if(collection.size() == 0 || collection == null)
            return null;
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
        return receiveCompile;
    }

    public byte[] getTerminatingByte(){
        return new byte[1];   
    }
}