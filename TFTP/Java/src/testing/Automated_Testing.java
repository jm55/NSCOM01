package testing;

import data.*;
import utils.*;

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
			System.out.println(o + ": " + u.getBytesString(t.checkOpCode(o)));
	}
	
	private void check_buildOACK(TFTP t) {
		System.out.println("Check OACK Assembly: ");
		Integer[] optVals = {1,3,5,7};
		byte[] opts = {optVals[0].byteValue(), optVals[1].byteValue(), optVals[2].byteValue(), optVals[3].byteValue()};
		byte[] vals = {2,4,6,8};
		if(opts.length == vals.length) {
			System.out.println("opts: " + u.getBytesString(opts));
			System.out.println("vals: " + u.getBytesString(vals));
			System.out.println(u.getBytesString(t.checkOACK(opts, vals)));
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
