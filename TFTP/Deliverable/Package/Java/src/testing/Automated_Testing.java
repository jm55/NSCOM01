package testing;

import data.*;
import utils.*;

/**
 * @deprecated
 * Conducts automated testing of functions.
 */
public class Automated_Testing {
	Utility u = new Utility();
	public Automated_Testing(){
		System.out.println("Test_TFTP()");
		Test_TFTP();
	}
	
	public void Test_TFTP() {
		TFTP t = new TFTP();
		check_buildOpcode(t);
		check_buildOACK(t);	
		check_buildACK(t);	
	}
	
	private void check_buildOpcode(TFTP t) {
		System.out.println("Check OpCode Assembly: ");
		byte[] opcodes = {1,2,3,4,5,6,7,8};
		for(byte o : opcodes)
			System.out.println(o + ": " + u.getBytesAsBits(t.checkOpCode(o),true));
	}
	
	private void check_buildOACK(TFTP t) {
		System.out.println("Check OACK Assembly: ");
		Integer[] optVals = {1,3,5,7};
		String[] opts = {"opts1", "opts2"};
		String[] vals = {"vals1", "vals2"};
		if(opts.length == vals.length) {
			System.out.println(u.getBytesAsBits(t.checkOACK(opts, vals),true));
		}
	}
	
	private void check_buildACK(TFTP t) {
		System.out.println("Check ACK Assembly: ");
		Short[] block = {1,3,5,7};
		for(Short s:block) {
			u.printBytes(t.checkACK(s));
		}
	}
}
