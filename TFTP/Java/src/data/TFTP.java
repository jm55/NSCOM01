package data;

import java.io.File;
import java.net.DatagramPacket;
import java.util.ArrayList;

import utils.Utility;

/**
 * Builds the TFTP packet that the Client will submit or receive, and process accordingly.
 * Packets implemented: RFC 1350 & 2347
 * Reference: 
 * 		Files of RFC 1350, 2347, 2348(!), & 2349(!)
 * 		https://faculty-web.msoe.edu/yoder/a/cs2911/20q1/lab7res/CS2911Lab7.pdf
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
 * 
 * MODE (RFC 1350):
 * netascii: A host which receives netascii mode data must translate the data to its own format.
 * octet: 	Octet mode is used to transfer a file that is in the 8-bit format of the machine from which the file is being transferred. 
 * 			It is assumed that each type of machine has a single 8-bit format that is more common, and that that format is chosen. 
 * 			For example, on a DEC-20, a 36 bit machine, this is four 8-bit bytes to a word with four bits of breakage. 
 * 			If a host receives a octet file and then returns it, the returned file must be identical to the original.
 * mail: 	Mail mode uses the name of a mail recipient in place of a file and
 *			must begin with a WRQ. Otherwise it is identical to netascii mode.
 *			The mail recipient string should be of the form "username" or "username@hostname". If the second form is used, 
 *			it allows the option of mail forwarding by a relay computer.
 */
public class TFTP {
	private static final String[] MODES = {"netascii", "octet", "mail"};
	
	public static byte[] extractData(DatagramPacket packet) {
		return extractData(packet.getData());
	}
	
	public static byte[] extractData(byte[] packetBytes) {
		if(packetBytes == null || packetBytes.length == 0)
			return null;
		if(!validOpCode(getOpCode(packetBytes)))
			return null;
		if(getOpCode(packetBytes) == 3) {
			int offset = 4; //4byte offset, refer to DATA packet structure for more details.
			byte[] data = new byte[packetBytes.length - offset];
			for(int i = offset; i < packetBytes.length; i++)
				data[i-offset] = packetBytes[i];
			return data;
		}
		return null;
	}
	
	public static String[] extractError(DatagramPacket packet) {
		if(!isError(packet))
			return null;
		return extractError(packet.getData());
	}
	
	public static String[] extractError(byte[] packetBytes) {
		if(!isError(packetBytes)) //If not an error
			return null;
		String[] errMessage = {packetBytes[3] + "", getErrMsg((int)packetBytes[3])};
		return errMessage;
	}
	
	public static int extractBlockNumber(DatagramPacket packet){
		return extractBlockNumber(packet.getData());
	}
	
	public static int extractBlockNumber(byte[] packetBytes){
		if(getOpCode(packetBytes) == 3 || getOpCode(packetBytes) == 4){
			Integer a = Integer.parseInt(Utility.byteToHex(packetBytes[2]),16);
			Integer b = Integer.parseInt(Utility.byteToHex(packetBytes[3]),16);
			String aString = String.format("%8s", Integer.toBinaryString(a)).replace(' ', '0');
			String bString = String.format("%8s", Integer.toBinaryString(b)).replace(' ', '0');
			return Integer.parseInt(aString+bString,2);
		}
		return -1;
	}
	
	public static int getOpCode(DatagramPacket packet) {
		return getOpCode(packet.getData());
	}
	
	public static int getOpCode(byte[] packetBytes) {
		return (int)packetBytes[1];
	}
	
	public static boolean isError(DatagramPacket packet) {
		if(getOpCode(packet) == 5)
			return true;
		return false;
	}
	
	public static boolean isError(byte[] packetBytes) {
		if(getOpCode(packetBytes)==5)
			return true;
		return false;
	}
	
	public static boolean isACK(DatagramPacket packet) {
		if(getOpCode(packet) == 4)
			return true;
		return false;
	}
	
	public static boolean isACK(byte[] packetBytes) {
		if(getOpCode(packetBytes)==4)
			return true;
		return false;
	}
	
	public static boolean isData(DatagramPacket packet) {
		if(getOpCode(packet) == 3)
			return true;
		return false;
	}
	
	public static boolean isData(byte[] packetBytes) {
		if(getOpCode(packetBytes)==3)
			return true;
		return false;
	}
	
	public static byte extractACK(byte[] packetBytes) {
		if(isACK(packetBytes))
			return packetBytes[3];
		return -1;
	}
	
	public static boolean isOACK(DatagramPacket packet) {
		if(getOpCode(packet) == 6)
			return true;
		return false;
	}
	public static boolean isOACK(byte[] packetBytes) {
		if(getOpCode(packetBytes)==6)
			return true;
		return false;
	}
	
	public static boolean RQHasOACK(byte[] packetBytes) {
		if(getOpCode(packetBytes) >= 3) //It is not a request.
			return false;
		int terminateCount = 0;
		for(int j = 0; j < packetBytes.length; j++)
			if(packetBytes[j] == 0 && j > 0)
				terminateCount++;
		if(terminateCount > 2)
			return true;
		return false;
	}
	
	public static String[][] extractOACKFromRQ(byte[] packetBytes){
		//Refer to RFC2347 for packet format.
		int zeroPadding = 0;
		for(int j = 0; j < packetBytes.length; j++) {
			if(packetBytes[j] == 0 && j > 0)
				zeroPadding++;
			if(zeroPadding>=2) {
				ArrayList<Byte> collector = new ArrayList<Byte>();
				ArrayList<String> optStr = new ArrayList<String>();
				ArrayList<String> valStr = new ArrayList<String>();
				boolean switchOptVal = true;
				for(int i = j+1; i < packetBytes.length; i++) {
					if(packetBytes[i] == 0) {
						if(switchOptVal) { 
							optStr.add(Utility.ByteListToString(collector));
						}else { 
							valStr.add(Utility.ByteListToString(collector));
						}
						collector = new ArrayList<Byte>();
						switchOptVal = !switchOptVal; //Change mode.
					}else {
						collector.add(packetBytes[i]);
					}
				}
				String[][] out = {Utility.StringListToStringArr(optStr), Utility.StringListToStringArr(valStr)};
				return out;
			}
		}
		return null;
	}
	
	public static String[][] extractOACK(byte[] packetBytes){
		ArrayList<Byte> collector = new ArrayList<Byte>();
		ArrayList<String> optStr = new ArrayList<String>();
		ArrayList<String> valStr = new ArrayList<String>();
		boolean switchOptVal = true;
		for(int i = 2; i < packetBytes.length; i++) {
			if(packetBytes[i] == 0) {
				if(switchOptVal)
					optStr.add(Utility.ByteListToString(collector));
				else
					valStr.add(Utility.ByteListToString(collector));
				collector = new ArrayList<Byte>();
				switchOptVal = !switchOptVal;
			}else {
				collector.add(packetBytes[i]);
			}
		}
		String[][] out = {Utility.StringListToStringArr(optStr), Utility.StringListToStringArr(valStr)};
		return out;
	}
	
	public static byte[] getWRQPacket(File f, String mode, String[] opt, String[] vals) {
		if(f != null)
			if(f.exists() && validOptVal(opt,vals))
				return buildRQPacket((byte)2, f.getName(), mode, opt, vals);
		return null;
	}
	
	public static byte[] getRRQPacket(String filename, String mode, String[] opt, String[] vals) {
		if(filename != null && validOptVal(opt, vals))
			return buildRQPacket((byte)1, filename, mode, opt, vals);
		return null;
	}
	
	public static byte[] getErrPacket(Integer err) {
		return buildErrPacket(err, getErrMsg(err));
	}
	
	public static byte[] getErrPacket(Integer err, String errmsg) {
		return buildErrPacket(err, errmsg);
	}
	
	public static byte[] getDataPacket(Integer block, byte[] data) {
		if(block < 0 || data == null)
			return null;
		if(data.length == 0)
			return null;
		return buildDataPacket(block, data);
	}
	
	public static String getErrMsg(Integer err) {
		if(err > 7 || !validErrCode(err))
			return null;
		String[] msg = {
				"Undefined error code",
				"File not found",
				"Access violation",
				"Disk full",
				"Illegal TFTP operation",
				"Unknown port",
				"File already exists",
				"No such user",
				"Invalid option"
		};
		return msg[err];
	}
	
	public static byte[] getACK(Integer block) {
		return buildACKPacket(block);
	}
	
	public static byte[] getOACK(String[] opts, String[] vals) {
		if(validOptVal(opts, vals))
			return buildOACKPacket(opts, vals);
		return null;
	}
	
	public static boolean validOptVal(String[] opt, String[] vals) {
		if(opt != null && vals != null)
			return opt.length == vals.length;
		return true;
	}
	
	private static byte[][] stringArrToByteArr(String[] text){
		byte[][] byteArr = new byte[text.length][];
		for(int i = 0; i < text.length; i++)
			byteArr[i] = text[i].getBytes();
		return byteArr;
	}
	
	private static boolean validOptVal(byte[][] opt, byte[][] vals) {
		if(opt != null && vals != null)
			return opt.length == vals.length;
		return false;
	}
	
	private static boolean validErrCode(Integer err) {
		if(err < 0 || err > 8)
			return false;
		return true;
	}
	
	private static boolean validMode(String mode) {
		if(mode == null)
			return false;
		for(String m: MODES)
			if(m.compareToIgnoreCase(mode)==0)
				return true;
		return false;
	}
	
	private static boolean validOpCode(Integer opcode) {
		if(opcode < 1 || opcode > 6)
			return false;
		return true;
	}
	
	private static byte[] buildErrPacket(Integer err, String emsg) {
		if(!validErrCode(err))
			return null;
		byte[] opcode = buildOpcode(5), errcode = {getPaddingByte(), getPaddingByte(), getPaddingByte(),  err.byteValue()}, errMsg = emsg.getBytes();
		byte[][] combined = {getPaddingByteArr(),getPaddingByteArr(),opcode, errcode, errMsg, getPaddingByteArr(), getPaddingByteArr(), getPaddingByteArr(), getPaddingByteArr()}; //the 2nd and last paddings are for errMsg termination and err packet padding respectively
		return combineBytes(combined);
	}
	
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
	
	private static byte[] buildRQPacket(byte type, String filename, String mode, String[] opts, String[] vals) {
		//Check if request is within values of 1-2.
		//Check if given file or mode is null, return null if so.
		if(type > 2 || type < 1 || filename == null || mode == null) 
			return null;
		
		//Check if mode is valid or not
		for(String m: MODES)
			if(!m.equals(mode)) //Return null if m does not match mode
				return null;
		
		if(opts != null && vals != null) { //Check if opts and vals are not null.
			if(opts.length != vals.length) { //Check if lengths of opts and vals are not equal.
				return null;
			}else { //Lengths of opts and vals are equal.
				byte[][] combined = {buildOpcode(type), filename.getBytes(), getPaddingByteArr(), mode.getBytes(), getPaddingByteArr(), buildOptsVals(opts, vals)};
				return combineBytes(combined);
			}
		}else {
			//Follows bytes: {0,1,filename.bytes,0,mode.bytes,0};
			byte[][] combined = {buildOpcode(type), filename.getBytes(), getPaddingByteArr(), mode.getBytes(), getPaddingByteArr()};			
			return combineBytes(combined);
		}
	}
	
	private static byte[] buildACKPacket(Integer block) {
		if(block < 0)
			return null;
		byte[][] combined = {buildOpcode(4), Utility.shortToByteArr(block.shortValue())};
		byte[] ack = combineBytes(combined);
		return ack;
	}
	
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
	
	private static byte[] buildDataPacket(Integer block, byte[] data) {
		if(block < 0 || data == null)
			return null;
		byte opcodeVal = 3;
		Short blockShort = block.shortValue();
		byte[] opcode = buildOpcode(opcodeVal), blockNum = Utility.shortToByteArr(blockShort);
		byte[][] preDataPacket = {opcode,blockNum,data};
		return combineBytes(preDataPacket);
	}
	
	private static byte[] getPaddingByteArr() {
		byte[] arr = {getPaddingByte()};
		return arr;
	}
	
	private static byte getPaddingByte() {
		Short padding = 0;
		return padding.byteValue();
	}
	
	private static byte[] buildOptsVals(String[] opts, String[] vals) {
		if(opts == null || vals == null)
			return null;
		if(opts.length == vals.length)
			return buildOptsVals(stringArrToByteArr(opts), stringArrToByteArr(vals));
		return null;
	}
	
	private static byte[] buildOptsVals(byte[][] opts, byte[][] vals) {
		if(opts == null || vals == null)
			return null;
		if(opts.length == vals.length) {
			ArrayList<Byte> optsvals = new ArrayList<Byte>();
			//Each row is a specific opt and val pair.
			for(int i = 0; i < opts.length; i++) {
				for(int j = 0;  j < opts[i].length; j++)
					optsvals.add(opts[i][j]);
				optsvals.add(getPaddingByte());
				for(int j = 0;  j < vals[i].length; j++)
					optsvals.add(vals[i][j]);
				optsvals.add(getPaddingByte());
			}
			return Utility.ByteListToByteArr(optsvals);
		}
		return null;
	}
	
	private static byte[] buildOpcode(int opcode) {
		return buildOpcode((byte)opcode);
	}
	
	private static byte[] buildOpcode(byte opcode) {
		byte[] opcodeByte = {getPaddingByte(), opcode};
		return opcodeByte;
	}
	
	public static byte[] checkOpCode(byte opcode) {
		return buildOpcode(opcode);
	}
	
	public static byte[] checkOptVals(String[] opts, String[] vals) {
		return buildOptsVals(opts, vals);
	}
	
	public static byte[] checkACK(Integer block) {
		return buildACKPacket(block);
	}
	
	public static byte[] checkOACK(String[] opts, String[] vals) {
		return buildOACKPacket(opts, vals);
	}
}
