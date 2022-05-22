package data;

import java.io.File;
import java.net.DatagramPacket;
import java.util.ArrayList;

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
 * ACK PACKET STRUCTURE (RFC 1350)
 * ====================
 * 2bytes  ||  2bytes    
 * ====================
 * Opcode  ||  Block#  
 * ====================
 * 
 * OACK PACKET STRUCTURE (RFC 2347)
 * Note that opt and val are combined with the terminating 0 byte.
 * ================================= ============================
 * 2bytes  ||  2bytes  ||  2bytes  || ... ||  2bytes  ||  2bytes  
 * ==============================================================
 * Opcode  ||   opt1   ||   val1   || ... ||   optn   ||   valn
 * ==============================================================
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
		
		if(!validOpCode(opcode))
			return null;
		
		//Check if opcode is extractable with data, extract data accordingly if so.
		
		return null;
	}
	
	public int getOpCode(DatagramPacket packet) {
		return getOpCode(packet.getData());
	}
	
	public int getOpCode(byte[] packetBytes) {
		return (int)packetBytes[1];
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
	
	public byte[] getWRQ(File f, String mode, byte[] opt, byte[] vals) {
		if(f != null)
			if(f.exists() && validOptVal(opt,vals))
				return buildRQPacket(2,f.getName(), mode, opt, vals);
		return null;
	}
	
	public byte[] getRRQ(String filename, String mode, byte[] opt, byte[] vals) {
		if(filename != null && validOptVal(opt, vals))
			return buildRQPacket(1,filename, mode, opt, vals);
		return null;
	}
	
	public byte[] getErr(Integer err, String errmsg) {
		return buildErrPacket(err, errmsg);
	}
	
	private boolean validOptVal(byte[] opt, byte[] vals) {
		if(opt != null && vals != null)
			if(opt.length == vals.length)
				return true;
		return false;
	}
	
	//Follows RFC 1350 and RFC 2347
	private boolean validErrCode(Integer err) {
		if(err < 0 || err > 8)
			return false;
		return true;
	}
	
	//Follows RFC 1350 and RFC 2347
	private boolean validOpCode(Integer opcode) {
		if(opcode < 1 || opcode > 6)
			return false;
		return true;
	}
	
	private byte[] buildErrPacket(Integer err, String emsg) {
		//Error Packet 
		if(!validErrCode(err))
			return null;
		byte[] opcode = {0,5}, errcode = {err.byteValue(), 0}, errMsg = emsg.getBytes(), padding = new byte[0];
		byte[][] combined = {opcode, errcode, errMsg, padding};
		return combineBytes(combined);
	}
	
	/**
	 * Builds the byte[] of TFTP data packet.
	 * Follows RFC 1350
	 * @param data Data of the packet
	 * @param block block of the 
	 * @return
	 */
	private byte[] buildDataPacket(byte[] data, int block) {
		byte[] dataPacket = null;
		return dataPacket;
	}
	
	/**
	 * Combines byte[][] into byte[] linearly.
	 * @param bytes byte[][] to turn into byte[]
	 * @return byte[] equivalent of byte[][]
	 */
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
	
	//Follows RFC 2347
	private byte[] buildRQPacket(Integer type, String filename, String mode, byte[] opts, byte[] vals) {
		if(type > 2 || type < 1)
			return null;
		//Check if given file or mode is null, return null if so.
		if(filename == null || mode == null) {
			return null;
		}
		//Prepare opcode for Read Request.
		byte[] opcode = {0,type.byteValue()};
		
		if(opts != null && vals != null) { //Check if opts and vals are not null.
			if(opts.length != vals.length) { //Check if lengths of opts and vals are not equal.
				return null;
			}else { //Lengths of opts and vals are equal.
				byte[] optsVals = buildOptsVals(opts, vals); //Combines opts & vals into one byte[]; Includes the last padding for valsN
				byte[][] combined = {opcode, filename.getBytes(), getPaddingByteArr(), mode.getBytes(), getPaddingByteArr(), optsVals};
				return combineBytes(combined);
			}
		}else {
			//Follows bytes: {0,1,filename.bytes,0,mode.bytes,0};
			byte[][] combined = {opcode,filename.getBytes(),getPaddingByteArr(), mode.getBytes(), getPaddingByteArr()};
			return combineBytes(combined);
		}
	}
	
	//Follows RFC 1350
	private byte[] buildAckPacket(Integer block) {
		byte[] ack = {0,4,block.byteValue(),0};
		return ack;
	}
	
	//Follows RFC 1350
	private byte[] buildDataPacket(Integer block, byte[] data) {
		byte[] opcode = {0,3}, blockNum = {block.byteValue(),0};
		byte[][] preDataPacket = {opcode,blockNum,data};
		return combineBytes(preDataPacket);
	}
	
	private byte[] getPaddingByteArr() {
		byte[] arr = {new Integer(0).byteValue()};
		return arr;
	}
	
	private byte getPaddingByte() {
		return new Integer(0).byteValue();
	}
	
	private byte[] buildOptsVals(byte[] opts, byte[] vals) {
		if(opts == null || vals == null) {
			return null;
		}else {
			if(opts.length != vals.length) {
				return null;
			}else {
				ArrayList<Byte> optsvals = new ArrayList<Byte>();
				for(int i = 0; i < opts.length; i++) {
					optsvals.add(opts[i]);
					optsvals.add(getPaddingByte());
					optsvals.add(vals[i]);
					optsvals.add(getPaddingByte());
				}
				byte[] rawoptsvals = new byte[optsvals.size()];
				Byte[] optsvalsArr = new Byte[optsvals.size()];
				optsvals.toArray(optsvalsArr);
				for(int i = 0; i < optsvals.size(); i++)
					rawoptsvals[i] = optsvalsArr[i].byteValue();
				return rawoptsvals;
			}
		}
	}
}
