package data;

/**
 * Builds the TFTP packet that the Client will submit or receive, and process accordingly.
 * Reference: https://faculty-web.msoe.edu/yoder/a/cs2911/20q1/lab7res/CS2911Lab7.pdf
 * OPCODES:
 * Value	Meaning
 * 1		Read Request 
 * 2		Write Request
 * 3		Data Message
 * 4		Acknowledgement Message
 * 5		Error Message
 * READ/WRITE REQUEST (RRQ/WRQ) PACKET STRUCTURE
 * ========================================================
 * 2bytes    ||    String    ||    1byte    ||    1byte
 * ========================================================
 * Opcode    ||   Filename   ||     Mode    ||      0
 * ========================================================
 * DATA PACKET STRUCTURE
 * ==========================================
 * 2bytes    ||    2bytes    ||    nbytes    
 * ==========================================
 * Opcode    ||    Block#    ||    1byte    
 * ==========================================
 * ACK PACKET STRUCTURE
 * ==========================
 * 2bytes    ||    2bytes    
 * ==========================
 * Opcode    ||    Block#    
 * ==========================
 * ERROR PACKET STRUCTURE
 * ========================================================
 * 2bytes    ||    2bytes    ||    String   ||    1byte
 * ========================================================
 * Opcode    ||    ErrCode   ||    ErrMsg   ||      0
 * ========================================================
 * ERROR CODES:
 * Value 	Meaning
 * 0		Not defined, see error message (if any).
 * 1 		File not found.
 * 2 		Access violation.
 * 3 		Disk full or allocation exceeded.
 * 4 		Illegal TFTP operation.
 * 5 		Unknown transfer ID.
 * 6 		File already exists.
 * 7 		No such user.

 */
public class TFTP {
	private byte[] TFTP_Packet = null;
	//Opcodes have 2bytes each but why so?: https://stackoverflow.com/a/50952901;
	private byte[] opCode = new byte[2], blockNumber = new byte[2]; 
	private byte[] errOpcode = new byte[2], errCode = new byte[2];
	private String errMsg;
	private byte[] padding = new byte[1];
	private byte[] data = null;
	
	public TFTP() {
		
	}
	
	
	private void setData(byte[] data, int block) {
		this.data = data;
	}
	
	
	private byte[] buildReadRequest(String filename) {
		byte[] RRQ = null;
		/**
		 * BUILD REQUEST PACKET HERE
						 */
		return RRQ;
	}
	
	private byte[] buildWriteRequest() {
		byte[] WRQ = null;
		
		return WRQ;
	}
}
