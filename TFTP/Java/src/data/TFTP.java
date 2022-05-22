package data;

import java.io.File;
import java.net.DatagramPacket;

import utils.Monitor;

/**
 * Builds the TFTP packet that the Client will submit or receive, and process accordingly.
 * 
 * Reference: https://faculty-web.msoe.edu/yoder/a/cs2911/20q1/lab7res/CS2911Lab7.pdf
 * OPCODES (Most from RFC 1350):
 * Value	Meaning
 * 1		Read Request 
 * 2		Write Request
 * 3		Data Message
 * 4		Acknowledgement Message
 * 5		Error Message
 * 6		Option Acknowledgement (RFC2347; OACK)
 * 
 * READ/WRITE REQUEST (RRQ/WRQ) PACKET STRUCTURE (RFC 1350; OBSELETE, UPDATED BY RFC 2347)
 * ==========================================================
 * 2bytes    ||   String   ||  1byte  ||  String   ||  1byte
 * ==========================================================
 * Opcode    ||  Filename  ||    0    ||   Mode    ||    0
 * ==========================================================
 * 
 * READ/WRITE REQUEST (RRQ/WRQ) PACKET STRUCTURE (RFC 2347; MAX PACKET SIZE OF 512BYTES/OCTETS)
 * Note that opt and val are combined with the terminating 0 byte.
 * ==============================================================================================================
 * 2bytes  ||   String   ||  1byte  ||  String  ||  1byte  ||  2bytes ||  2bytes  || ... ||  2bytes  ||  2bytes
 * ==============================================================================================================
 * Opcode  ||  Filename  ||    0    ||   mode   ||    0    ||   opt1  ||  value1  || ... ||   optn   ||   valn   
 * ==============================================================================================================
 * 
 * DATA PACKET STRUCTURE (RFC 1350)
 * ================================
 * 2bytes  ||  2bytes  ||  nbytes    
 * ================================
 * Opcode  ||  Block#  ||  1byte    
 * ================================
 * 
 * OACK PACKET STRUCTURE (RFC 2347)
 * Note that opt and val are combined with the terminating 0 byte.
 * ================================= ============================
 * 2bytes  ||  2bytes  ||  2bytes  || ... ||  2bytes  ||  2bytes  
 * ==============================================================
 * Opcode  ||   opt1   ||   val1   || ... ||   optn   ||   valn
 * ==============================================================
 *
 * ACK PACKET STRUCTURE (RFC 1350)
 * ====================
 * 2bytes  ||  2bytes    
 * ====================
 * Opcode  ||  Block#  
 * ====================
 * 
 * ERROR PACKET STRUCTURE (RFC 1350)
 * ========================================================
 * 2bytes    ||    2bytes    ||    String   ||    1byte
 * ========================================================
 * Opcode    ||    ErrCode   ||    ErrMsg   ||      0
 * ========================================================
 * 
 * ERROR CODES (Most from RFC 1350):
 * Value 	Meaning
 * 0		Not defined, see error message (if any).
 * 1 		File not found.
 * 2 		Access violation.
 * 3 		Disk full or allocation exceeded.
 * 4 		Illegal TFTP operation.
 * 5 		Unknown transfer ID.
 * 6 		File already exists.
 * 7 		No such user.
 * 8		Transfer terminated due to option negotiation (RFC 2347)
 */
public class TFTP {
	Monitor m = new Monitor();
	private byte[] TFTP_Packet = null;
	//Opcodes have 2bytes each but why so?: https://stackoverflow.com/a/50952901;
	private byte[] opCode = new byte[2], blockNumber = new byte[2]; 
	private byte[] errOpcode = new byte[2], errCode = new byte[2];
	private String errMsg;
	private byte[] padding = new byte[1];
	private byte[] data = null;
	
	public TFTP() {
		
	}
	
	public byte[] extractData(DatagramPacket packet) {
		return extractData(packet.getData());
	}
	
	public byte[] extractData(byte[] packetBytes) {
		if(packetBytes == null)
			return null;
		if(packetBytes.length == 0) {
			return null;
		}
		int opcode = getOpCode(packetBytes);
		
		//Check if opcode is extractable with data, extract data accordingly if so.
		
		return null;
	}
	
	public int getOpCode(DatagramPacket packet) {
		return getOpCode(packet.getData());
	}
	
	public int getOpCode(byte[] packetBytes) {
		return packetBytes[0];
	}
	
	public boolean isError(DatagramPacket packet) {
		if(getOpCode(packet) == 5)
			return true;
		return false;
	}
	
	public boolean isError(byte[] packetBytes) {
		if(getOpCode(packetBytes)==5)
			return true;
		return false;
	}
	
	public byte[] getWRQ(File f) {
		if(f != null)
			if(f.exists())
				return buildReadRequest(f);
		return null;
	}
	
	public byte[] getOWRQ(File f, byte[] opt, byte[] vals) {
		if(f != null)
			if(f.exists())
				return buildReadRequest(f,opt,vals);
		return null;
	}
	
	public byte[] getWWRQ(String filename, byte[] opt, byte[] vals) {
		if(filename != null)
			return buildWriteRequest(filename,opt,vals);
		return null;
	}
	
	public byte[] getRRQ(String filename) {
		if(filename != null)
			return buildWriteRequest(filename);
		return null;
	}
	
	public byte[] getErr(Integer err, String errmsg) {
		return buildErrPacket(err, errmsg);
	}
	
	private byte[] buildErrPacket(Integer err, String emsg) {
		//Error Packet 
		byte[] opcode = {0,5}, errcode = {err.byteValue(), 0}, errMsg = emsg.getBytes(), padding = new byte[0];
		byte[][] combined = {opcode, errcode, errMsg, padding};
		return combineBytes(combined);
	}
	
	private byte[] buildDataPacket(byte[] data, int block) {
		byte[] dataPacket = null;
		/**
		 * BUILD DATA PACKET HERE
		 */
		return dataPacket;
	}
	
	private byte[] buildReadRequest(File f) {
		byte[] RRQ = null;
		/**
		 * BUILD REQUEST PACKET HERE
		 */
		if(!f.exists()) {
			
		}
		return RRQ;
	}
	
	private byte[] buildReadRequest(File f, byte[] opt, byte[] vals) {
		byte[] ORRQ = null;
		/**
		 * BUILD REQUEST PACKET HERE
		 */
		if(!f.exists()) {
			
		}
		if(ORRQ.length > 512)
			return null;
		return ORRQ;
	}
	
	private byte[] buildWriteRequest(String filename) {
		byte[] WRQ = null;
		/**
		 * BUILD REQUEST PACKET HERE
		 */
		return WRQ;
	}
	
	private byte[] buildWriteRequest(String filename, byte[] opt, byte[] vals) {
		byte[] OWRQ = null;
		/**
		 * BUILD OPTIONAL REQUEST PACKET HERE
		 */
		if(OWRQ.length > 512)
			return null;
		return OWRQ;
	}
	
	private byte[] combineBytes(byte[][] bytes){
		int size = 0, ctr = 0;
		for(int i = 0; i < bytes.length; i++)
			size += bytes[i].length;
		byte[] combinedBytes = new byte[size];
		for(byte[] byteArr: bytes) {
			for(byte b: byteArr) {
				combinedBytes[ctr] = b;
				ctr++;
			}
		}
		return combinedBytes;
	}
}
