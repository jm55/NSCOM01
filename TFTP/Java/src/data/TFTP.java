package data;

import java.io.File;
import java.net.DatagramPacket;
import java.util.ArrayList;

import utils.Utility;

/**
 * Builds the TFTP packet that the Client will submit or receive, and process accordingly.
 * 
 * Packets implemented: RFC 1350 & 2347
 * 
 * Reference: 
 * 		Files of RFC 1350, 2347, 2348(!), & 2349(!)
 * 		https://faculty-web.msoe.edu/yoder/a/cs2911/20q1/lab7res/CS2911Lab7.pdf
 * 
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
	private Utility m = new Utility();
	private final String className = "TFTP";
	private final String[] MODES = {"netascii", "octet", "mail"};
	private final int PACKETSIZE_LIMIT = 512;
	public TFTP() {
		
	}
	
	/**
	 * Extracts Data from a packet.
	 * @param packetBytes Packet to extract data from.
	 * @return Data in byte[] format
	 */
	public byte[] extractData(DatagramPacket packet) {
		return extractData(packet.getData());
	}
	
	/**
	 * TODO
	 * Extracts Data from a packet.
	 * @param packetBytes Packet in byte[] form.
	 * @return Data in byte[] format
	 */
	public byte[] extractData(byte[] packetBytes) {
		if(packetBytes == null)
			return null;
		if(packetBytes.length == 0) {
			return null;
		}
		int opcode = getOpCode(packetBytes);
		
		if(!validOpCode(opcode))
			return null;
		if(opcode == 4) {
			byte[] data = null;
			
			/**
			 * EXTRACT DATA FROM PACKET BYTES.
			 * REFER TO RFC 1350 FOR STRUCTURE
			 */
			
			return data;
		}
		return null;
	}
	
	/**
	 * Get the error message of a packet bytes as String[].
	 * Content arrangement: {Error Code, Error Message}
	 * @param packet Error packet in byte[] form.
	 * @return String[] containing error code and error message, false if packetBytes is not an error packet.
	 */
	public String[] extractError(DatagramPacket packet) {
		if(!isError(packet))
			return null;
		return extractError(packet.getData());
	}
	
	/**
	 * TODO
	 * Get the error message of a packet bytes as String[].
	 * Content arrangement: {Error Code, Error Message}
	 * @param packetBytes Error packets in byte[] form.
	 * @return String[] containing error code and error message, false if packetBytes is not an error packet.
	 */
	public String[] extractError(byte[] packetBytes) {
		if(!isError(packetBytes)) //If not an error
			return null;
		String[] errMessage = new String[2];
		/**
		 * Extract errCode and errMsg from packet.
		 * Refer to RFC 1350
		 * Place extracted values accordingly to errMessage = {errCode, errMsg}
		 */
		return errMessage;
	}
	
	/**
	 * Extract OpCode of a packet.
	 * @param packet Packet to extract the OpCode from.
	 * @return OpCode of the packet.
	 */
	public int getOpCode(DatagramPacket packet) {
		return getOpCode(packet.getData());
	}
	
	/**
	 * Extract OpCode of a packet.
	 * @param packet Packet in byte[] to extract the OpCode from.
	 * @return OpCode of the packet.
	 */
	public int getOpCode(byte[] packetBytes) {
		return (int)packetBytes[1];
	}
	
	/**
	 * Checks if a packet is an error packet.
	 * @param packet Packet in DatagramPacket format. 
	 * @return True if packet is an error packet, false if otherwise.
	 */
	public boolean isError(DatagramPacket packet) {
		if(getOpCode(packet) == 5)
			return true;
		return false;
	}
	
	/**
	 * Checks if a packet is an error packet.
	 * @param packetBytes Packet in byte[] format.
	 * @return True if packet is an error packet, false if otherwise.
	 */
	public boolean isError(byte[] packetBytes) {
		if(getOpCode(packetBytes)==5)
			return true;
		return false;
	}
	
	/**
	 * Checks if a packet is an ACK packet.
	 * @param packet Packet in DatagramPacket format. 
	 * @return True if packet is an ACK packet, false if otherwise.
	 */
	public boolean isACK(DatagramPacket packet) {
		if(getOpCode(packet) == 4)
			return true;
		return false;
	}
	
	/**
	 * Checks if a packet is an ACK packet.
	 * @param packetBytes Packet in byte[] format.
	 * @return True if packet is an ACK packet, false if otherwise.
	 */
	public boolean isACK(byte[] packetBytes) {
		if(getOpCode(packetBytes)==4)
			return true;
		return false;
	}
	
	/**
	 * Checks if a packet is an OACK packet.
	 * @param packet Packet in DatagramPacket format. 
	 * @return True if packet is an OACK packet, false if otherwise.
	 */
	public boolean isOACK(DatagramPacket packet) {
		if(getOpCode(packet) == 6)
			return true;
		return false;
	}
	
	/**
	 * Checks if a packet is an OACK packet.
	 * @param packetBytes Packet in byte[] format.
	 * @return True if packet is an OACK packet, false if otherwise.
	 */
	public boolean isOACK(byte[] packetBytes) {
		if(getOpCode(packetBytes)==6)
			return true;
		return false;
	}
	
	/**
	 * Get a Write Request (WRQ) Packet.
	 * Follows RFC 1350 & 2347.
	 * @param f File to be written. Only extracts filename.
	 * @param mode Transfer mode. Refer to RFC 1350.
	 * @param opt Options
	 * @param vals Option values
	 * @return byte[] if valid parameters, false if otherwise or file does not exist.
	 */
	public byte[] getWRQPacket(File f, String mode, byte[] opt, byte[] vals) {
		if(f != null)
			if(f.exists() && validOptVal(opt,vals))
				return buildRQPacket(2,f.getName(), mode, opt, vals);
		return null;
	}
	
	/**
	 * Get a Read Request (RRQ) Packet.
	 * Follows RFC 1350 & 2347.
	 * @param filename Filename of the file to Read.
	 * @param mode Transfer mode. Refer to RFC 1350.
	 * @param opt Options
	 * @param vals Option values.
	 * @return byte[] if valid parameters, false if otherwise.
	 */
	public byte[] getRRQPacket(String filename, String mode, byte[] opt, byte[] vals) {
		if(filename != null && validOptVal(opt, vals))
			return buildRQPacket(1,filename, mode, opt, vals);
		return null;
	}
	
	/**
	 * Get an error packet.
	 * @param err Error Code
	 * @param errmsg Error Message
	 * @return byte[] if valid err parameter, false if otherwise.
	 */
	public byte[] getErrPacket(Integer err, String errmsg) {
		return buildErrPacket(err, errmsg);
	}
	
	/**
	 * Get Data Packet for Sending
	 * @param block Block# of the Data Packet.
	 * @param data Data to be sent.
	 * @return byte[] if valid parameters, null if otherwise.
	 */
	public byte[] getDataPacket(Integer block, byte[] data) {
		if(block < 0)
			return null;
		if(data == null)
			return null;
		if(data.length == 0)
			return null;
		return buildDataPacket(block, data);
	}
	
	/**
	 * Checks if an opt and val byte[] are equal in length.
	 * Follows RFC 2347
	 * @param opt Opt byte[]
	 * @param vals Vals byte[]
	 * @return True if equal in length, false if otherwise.
	 */
	private boolean validOptVal(byte[] opt, byte[] vals) {
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
	private boolean validErrCode(Integer err) {
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
	private boolean validMode(String mode) {
		if(mode == null)
			return false;
		for(String m: this.MODES)
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
	private boolean validOpCode(Integer opcode) {
		if(opcode < 1 || opcode > 6)
			return false;
		return true;
	}
	
	/**
	 * Builds an Error Packet for use in TFTP transmission.
	 * Follows RFC1350 and the subsequent valid error codes of RFC 2347.
	 * @param err Error code (Check valid values from RFC 1350 & 2347)
	 * @param emsg Error message.
	 * @return Returns a packet in its byte[] form. Returns null if error code is invalid.
	 */
	private byte[] buildErrPacket(Integer err, String emsg) {
		//Error Packet 
		if(!validErrCode(err))
			return null;
		byte[] opcode = {0,5}, errcode = {err.byteValue(), 0}, errMsg = emsg.getBytes(), padding = new byte[0];
		byte[][] combined = {opcode, errcode, errMsg, padding};
		return combineBytes(combined);
	}
	
	/**
	 * TODO
	 * Builds the byte[] of TFTP data packet.
	 * Follows RFC 1350
	 * @param data Data of the packet
	 * @param block block of the 
	 * @return
	 */
	private byte[] buildDataPacket(byte[] data, int block) {
		byte[] dataPacket = null;
		/**
		 * BUILD PACKET ACCORDING TO RFC 1350
		 */
		return dataPacket;
	}
	
	/**
	 * Combines byte[][] into byte[] linearly.
	 * @param bytes byte[][] to turn into byte[]
	 * @return Combined byte[] equivalent of byte[][]
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
	
	/**
	 * Builds a Normal ACK packet.
	 * @param block Block# to be acknowledged.
	 * @return Packet in byte[] form, null if block is invalid (such that it is < 0).
	 */
	private byte[] buildACKPacket(Integer block) {
		if(block < 0)
			return null;
		byte[] ack = {0,4,block.byteValue(),0};
		return ack;
	}
	
	/**
	 * Builds an Option ACK Packet.
	 * Follows OACK of 2347.
	 * @param opts Options
	 * @param vals Option values
	 * @return Packet in byte[] form, null if opts and/or vals are not valid.
	 */
	private byte[] buildOACKPacket(byte[] opts, byte[] vals) {
		if(opts == null || vals == null)
			return null;
		if(opts.length != vals.length)
			return null;
		byte[] optCode = {0,4};
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
	private byte[] buildDataPacket(Integer block, byte[] data) {
		if(block < 0)
			return null;
		if(data == null)
			return null;
		if(data.length > PACKETSIZE_LIMIT)
			return null;
		byte[] opcode = {0,3}, blockNum = {block.byteValue(),0};
		byte[][] preDataPacket = {opcode,blockNum,data};
		return combineBytes(preDataPacket);
	}
	
	/**
	 * Get a padding as byte[].
	 * @return byte[] containing a zero-value byte.
	 */
	private byte[] getPaddingByteArr() {
		byte[] arr = {getPaddingByte()};
		return arr;
	}
	
	/**
	 * Get a padding byte as byte. 
	 * @return Zero-value byte.
	 */
	private byte getPaddingByte() {
		Integer padding = 0;
		return padding.byteValue();
	}
	
	/**
	 * Combines opts and vals into one byte[] that follows RFC 2347.
	 * The byte[] ends with a null terminating of byte 0.
	 * @param opts List of opts. (String)
	 * @param vals List of vals. (String)
	 * @return byte[] if opts and vals are valid, null if opts and vals are either null or not equal in length.
	 */
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
