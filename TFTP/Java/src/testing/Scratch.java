package testing;


import java.io.*;
import java.nio.*;
import java.security.*;
import java.util.ArrayList;

import data.*;
import network.*;
import utils.*;


public class Scratch {
	private static Utility u = new Utility();
	private static final String className = "Scratch";
	public static void main(String[] args) {
		u.setState(true);
		RunScratch();
		//System.gc();
		System.exit(0);
	}
	
	public static void RunScratch() {
		TFTP t = new TFTP();
		System.out.println("Running scratch...");
		
		String hex = "68656c6c6f20776f726c64";
		byte[] data = u.hexStringToByteArray(hex);
		Integer block = 1;
		byte[] result = buildDataPacket(block, data);
		System.out.println(u.getBytesAsBits(result));
	}
	
	/**
	 * FROM TFTP
	 */
	private static final int PACKETSIZE_LIMIT = 512;
	private static final String[] MODES = {"netascii", "octet", "mail"};
	
	/**
	 * Converts a String[] into a 2D byte arr. For use in making opts and vals byte array.
	 * @param text
	 * @return
	 */
	private static byte[][] stringArrToByteArr(String[] text){
		byte[][] byteArr = new byte[text.length][];
		
		for(int i = 0; i < text.length; i++)
			byteArr[i] = text[i].getBytes();
		
		return byteArr;
	}
	
	/**
	 * Checks if an opt and val byte[] are equal in length.
	 * Follows RFC 2347
	 * @param opt Opt String[]
	 * @param vals Vals String[]
	 * @return True if equal in length, false if otherwise.
	 */
	private static boolean validOptVal(String[] opt, String[] vals) {
		if(opt != null && vals != null)
			if(opt.length == vals.length)
				return true;
		return false;
	}
	
	/**
	 * Checks if an opt and val byte[] are equal in length.
	 * Follows RFC 2347
	 * @param opt Opt byte[][]
	 * @param vals Vals byte[][]
	 * @return True if equal in length, false if otherwise.
	 */
	private static boolean validOptVal(byte[][] opt, byte[][] vals) {
		if(opt != null && vals != null)
			if(opt.length == vals.length)
				return true;
		return false;
	}
	
	/**
	 * Checks if an error code is valid for TFTP.
	 * Follows Error Code value ranges from RFC 1350 & 2347
	 * @param err Error code to be checked.
	 * @return True if valid, false if otherwise.
	 */
	private static boolean validErrCode(Integer err) {
		if(err < 0 || err > 8)
			return false;
		return true;
	}
	
	/**
	 * Checks if mode is valid for TFTP.
	 * Checks if mode is either: "netascii", "octet", or "mail".
	 * @param mode Mode to be checked.
	 * @return True if valid, false if otherwise.
	 */
	private static boolean validMode(String mode) {
		if(mode == null)
			return false;
		for(String m: Scratch.MODES)
			if(m.compareToIgnoreCase(mode)==0)
				return true;
		return false;
	}
	
	/**
	 * Checks if an opcode is valid for TFTP.
	 * Follows Opcode value ranges from RFC 1350 & 2347
	 * @param opcode OpCode to be checked.
	 * @return True if valid, false if otherwise.
	 */
	private static boolean validOpCode(Integer opcode) {
		if(opcode < 1 || opcode > 6)
			return false;
		return true;
	}
	
	/**
	 * Builds an Error Packet for use in TFTP transmission.
	 * Follows RFC1350 and the subsequent valid error codes of RFC 2347.
	 * @param err Error code (Check valid values from RFC 1350 & 2347)
	 * @param emsg Error message. Adds \0 if it does not exist as a terminating character.
	 * @return Returns a packet in its byte[] form. Returns null if error code is invalid.
	 */
	private static byte[] buildErrPacket(Integer err, String emsg) {
		if(emsg.charAt(emsg.length()-1) != '\0')
			emsg += '\0';
		//Error Packet 
		if(!validErrCode(err))
			return null;
		byte opcodeVal = 5;
		byte[] opcode = buildOpcode(opcodeVal), errcode = {0, err.byteValue()}, errMsg = emsg.getBytes();
		byte[][] combined = {opcode, errcode, errMsg, getPaddingByteArr()}; //the 2nd and last paddings are for errMsg termination and err packet padding respectively
		return combineBytes(combined);
	}
	
	/**
	 * Combines byte[][] into byte[] linearly.
	 * @param bytes byte[][] to turn into byte[]
	 * @return Combined byte[] equivalent of byte[][]
	 */
	private static byte[] combineBytes(byte[][] bytes){
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
	
	/**
	 * Builds a Request Packet or RQ.
	 * Can be set as either Read(1) or Write(2).
	 * Follows RFC 2347.
	 * @param type Type of request: Read(1) or Write(2).
	 * @param filename Filename of file to read or write.
	 * @param mode Transfer Mode: "netascii", "octet", "mail"
	 * @param opts Options
	 * @param vals Vals
	 * @return Packet in byte[] form. Returns null if type is invalid, filename or transfer mode is null, or if opts and vals are not equal in length.
	 */
	private static byte[] buildRQPacket(byte type, String filename, String mode, String[] opts, String[] vals) {
		if(type > 2 || type < 1)
			return null;
		//Check if given file or mode is null, return null if so.
		if(filename == null || mode == null) {
			return null;
		}
		//Check if mode is valid or not
		boolean match = false;
		for(String m: Scratch.MODES)
			if(m.equals(mode))
				match = true;
		if(!match)
			return null;
		
		//Prepare opcode for Read Request.
		byte[] opcode = buildOpcode(type);
		
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
			byte[][] combined = {opcode, getPaddingByteArr(), filename.getBytes(), getPaddingByteArr(), mode.getBytes()};
			return combineBytes(combined);
		}
	}
	
	/**
	 * Builds a Normal ACK packet.
	 * @param block Block# to be acknowledged.
	 * @return Packet in byte[] form, null if block is invalid (such that it is < 0).
	 */
	private static byte[] buildACKPacket(Short block) {
		if(block < 0)
			return null;
		byte opcode = 4;
		byte[][] combined = {buildOpcode(opcode),u.shortToByteArr(block)};
		byte[] ack = combineBytes(combined);
		return ack;
	}
	
	/**
	 * Builds an Option ACK Packet.
	 * Follows OACK of 2347.
	 * @param opts Options
	 * @param vals Option values
	 * @return Packet in byte[] form, null if opts and/or vals are not valid.
	 */
	private static byte[] buildOACKPacket(String[] opts, String[] vals) {
		if(opts == null || vals == null)
			return null;
		if(opts.length != vals.length)
			return null;
		byte opcodeVal = 6;
		byte[] optCode = buildOpcode(opcodeVal);
		byte[] combinedOptsVals = buildOptsVals(opts, vals);
		byte[][] combined = {optCode, combinedOptsVals};
		return combineBytes(combined);
	}
	
	/**
	 * Builds a data packet.
	 * Follows RFC 1350.
	 * @param block Block# of the data block.
	 * @param data Data in byte[].
	 * @return byte[] if parameters are valid, false if block is < 1, data is null, or length of data exceeds packet size limit for TFTP.
	 */
	private static byte[] buildDataPacket(Integer block, byte[] data) {
		if(block < 0)
			return null;
		if(data == null)
			return null;
		if(data.length-1 > PACKETSIZE_LIMIT)
			return null;
		byte opcodeVal = 3;
		byte[] opcode = buildOpcode(opcodeVal), blockNum = {0, block.byteValue()};
		byte[][] preDataPacket = {opcode,blockNum,data};
		return combineBytes(preDataPacket);
	}
	
	/**
	 * Get a padding as byte[].
	 * @return byte[] containing a zero-value byte.
	 */
	private static byte[] getPaddingByteArr() {
		byte[] arr = {getPaddingByte()};
		return arr;
	}
	
	/**
	 * Get a padding byte as byte. 
	 * @return Zero-value byte.
	 */
	private static byte getPaddingByte() {
		Integer padding = 0;
		return padding.byteValue();
	}
	
	/**
	 * Combines opts and vals into one byte[] that follows RFC 2347.
	 * The byte[] ends with a null terminating of byte 0.
	 * @param opts List of opts. (String to byte[][])
	 * @param vals List of vals. (String to byte[][])
	 * @return byte[] if opts and vals are valid, null if opts and vals are either null or not equal in length.
	 */
	private static byte[] buildOptsVals(String[] opts, String[] vals) {
		if(opts == null || vals == null) {
			return null;
		}else {
			if(opts.length != vals.length) {
				return null;
			}else {
				byte[][] optsBytes = stringArrToByteArr(opts);
				byte[][] valsBytes = stringArrToByteArr(vals);
				return buildOptsVals(optsBytes, valsBytes);
			}
		}
	}
	
	/**
	 * Combines opts and vals into one byte[] that follows RFC 2347.
	 * The byte[] ends with a null terminating of byte 0.
	 * @param opts List of opts. (String[] to byte[][])
	 * @param vals List of vals. (String[] to byte[][])
	 * @return byte[] if opts and vals are valid, null if opts and vals are either null or not equal in length.
	 */
	private static byte[] buildOptsVals(byte[][] opts, byte[][] vals) {
		if(opts == null || vals == null) {
			return null;
		}else {
			if(opts.length != vals.length) {
				return null;
			}else {
				ArrayList<Byte> optsvals = new ArrayList<Byte>();
				for(int i = 0; i < opts.length; i++) {
					//append opts[i] then add padding
					for(int j = 0;  j < opts[i].length; j++)
						optsvals.add(opts[i][j]);
					optsvals.add(getPaddingByte());
					//append vals[i] then add padding
					for(int j = 0;  j < vals[i].length; j++)
						optsvals.add(vals[i][j]);
					optsvals.add(getPaddingByte());
				}
				//convert ArrayList<Byte> to byte[]
				byte[] rawoptsvals = new byte[optsvals.size()];
				Byte[] optsvalsArr = new Byte[optsvals.size()];
				optsvals.toArray(optsvalsArr);
				for(int i = 0; i < optsvals.size(); i++)
					rawoptsvals[i] = optsvalsArr[i].byteValue();
				return rawoptsvals;
			}
		}
	}
	
	/**
	 * Builds an opcode byte[]
	 * @param opcode Specified Opcode
	 * @return opcode in byte[]
	 */
	private static byte[] buildOpcode(byte opcode) {
		byte[] opcodeByte = {0, opcode};
		return opcodeByte;
	}
	
}