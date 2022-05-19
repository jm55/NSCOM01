package test;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

import data.*;
import utils.*;

public class Automated_Test {
	private final String INPUT_DIR = ".\\resources\\files\\inputs\\", OUTPUT_DIR = ".\\resources\\files\\outputs\\"; 
	private int score = 0, total = 0;
	private FileByte fb;
	private FileHandlers fh;
	private Compression c;
	private Hasher h;
	private Monitor m;
	private final String className = "Automated_Test";
	public Automated_Test() {
		m = new Monitor();
		System.out.println("=============================");
		System.out.println("      AUTOMATED TESTING");
		System.out.println("=============================");
		ArrayList<TestResult> fb_result = TestFileByte();
		ArrayList<TestResult> fh_result = TestFileHandlers();
		ArrayList<TestResult> h_result = TestHasher();
		ArrayList<TestResult> c_result = TestCompression();
		
		
		System.out.println("=============================");
		System.out.println("        TESTING RESULTS");
		System.out.println("=============================");
		printTestResult(fb_result);
		printTestResult(fh_result);
		printTestResult(h_result);
		printTestResult(c_result);
		System.out.println("=============================");
		System.out.println("        TESTING SCORE");
		System.out.println("=============================");
		System.out.println("Test Score: " + getScore() + "%");
	}
	
	private ArrayList<TestResult> TestCompression(){
		ArrayList<TestResult> results = new ArrayList<TestResult>();
		
		return results;
	}
	
	private ArrayList<TestResult> TestHasher(){
		ArrayList<TestResult> results = new ArrayList<TestResult>();
		
		File[] f = 	{	new File(getInputPath("ABC.txt")),
						new File(getInputPath("ABC2.txt")),
						new File(getInputPath("DEF.txt")),
						new File(getInputPath("Image.jpg")),
						new File(getInputPath("LARGEJPEG.jpg")),
						new File(getInputPath("LargeMKV.mkv")),
						new File(getInputPath("LargePDF.pdf")),
						new File(getInputPath("LoremIpsum.txt")),
						new File(getInputPath("SmallPDF.pdf")),
					};
		String[] hashes = {	"ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",//ABC.txt
							"ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",//ABC2.txt
							"cb8379ac2098aa165029e3938a51da0bcecfc008fd6795f401178647f96c5b34",//DEF.txt
							"cdfafae2b78eb170f7d75d01047d5ddf236e295816b14529433e0f71cb8fbecf",//Image.jpg
							"58d995e4a6478ab2ef10897d7cb0a74d7d702d35da102a79f26bb09e7cac8c50",//LARGEJPEG.jpg
							"30082254ded971ca2d30071631be38cc27f472e2b8d71cef94aa2cdbd04a3c9a",//LargeMKV.mkv
							"d40b77103171cb5d08ac62b76bdbe9eb2e53b757c5b9b36b68ece2953b52e7f3",//LargePDF.pdf
							"2d8c2f6d978ca21712b5f6de36c9d31fa8e96a4fa5d8ff8b0188dfb9e7c171bb",//LoremIpsum.txt
							"b99938b8137b0afb1534fc0146efa45156a4b1908ff81fc22c7ad90409f2c752",//SmallPDF.pdf
							};
		
		h = new Hasher();
		
		//Compare each file to ShA-256 hashes
		if(f.length != hashes.length) {
			m.printMessage(this.className, "TestHasher()", "Files and hashes mismatch!");
		}else {
			for(int i = 0; i < f.length; i++) {
				if(f[i].exists()) {
					byte[] bytes = new FileByte().getBytesFromFile(f[i]);
					results.add(new TestResult<>("Hasher[]: Compute and checking hash of " + f[i].getName() + " (" + h.computeHash(bytes, null).substring(0,10) + "==" + hashes[i].substring(0,10) + ")",  h.compareHash(bytes, hashes[i])));
				}
			}
		}
		System.gc();
		
		//Check if invalid hash algorithm was given
		if(f[f.length-1].exists()) {
			byte[] bytes = new FileByte().getBytesFromFile(f[f.length-1]);
			results.add(new TestResult<>("Hasher[]: Check if invalid hash algorithm was given (result PASS since it reverts to SHA-256) " + f[f.length-1].getName() + " (" + h.computeHash(bytes, "HASH").substring(0,10) + "==" + hashes[hashes.length-1].substring(0,10) + ")",  h.compareHash(bytes, hashes[hashes.length-1])));
		}else {
			byte[] bytes = new FileByte().getBytesFromFile(f[0]);
			results.add(new TestResult<>("Hasher[]: Check if invalid hash algorithm was given (result PASS since it reverts to SHA-256) " + f[0].getName() + " (" + h.computeHash(bytes, "HASH").substring(0,10) + "==" + hashes[0].substring(0,10) + ")",  h.compareHash(bytes, hashes[0])));
		}
		System.gc();
		
		
		//Check if intentional false mismatch
		if(f[2].exists() && f[1].exists()) {
			byte[] bytes = new FileByte().getBytesFromFile(f[2]);
			results.add(new TestResult<>("Hasher[]: Check if intentional false mismatch " + f[2].getName() + " == " + f[1].getName(),  !h.compareHash(bytes, hashes[1])));
		}
		System.gc();
		
		//results.add(new TestResult<>("FileByte[]: Check byte[] length and validate", fb.getBytes().length == loremIpsum().length()));
		return results;
	}
	
	private ArrayList<TestResult> TestFileHandlers() {
		ArrayList<TestResult> results = new ArrayList<TestResult>();

		fh = new FileHandlers();
		
		//Set file to fh and Write file (small file)
		
		//Set file to fh and Write file (large file)
		
		//Write file (largest file)
		
		return results;
	}
	
	/**
	 * Tests user-critical functionalities of FileByte class.
	 * @return TestResuts of tests done to FileByte.
	 */
	private ArrayList<TestResult> TestFileByte() {
		m.printMessage(this.className, "TestFileByte()", "Testing FileByte...");
		
		ArrayList<TestResult> results = new ArrayList<TestResult>();

		//Check byte[] length and validate
		if(new File(getInputPath("LoremIpsum.txt")).exists()) {
			fb = new FileByte(new File(getInputPath("LoremIpsum.txt")));
			results.add(new TestResult<>("FileByte[]: Check byte[] length and validate", fb.getBytes().length == loremIpsum().length()));
		}
			
		//Check byte[] contents and validate
		if(new File(getInputPath("LoremIpsum.txt")).exists()) {
			fb = new FileByte(new File(getInputPath("LoremIpsum.txt")));
			results.add(new TestResult<>("FileByte[]: Check byte[] contents and validate", compareBytes(fb.getBytes(), loremIpsum().getBytes())));
		}
		
		//Check byte[] contents and validate via charset(UTF-8)
		if(new File(getInputPath("LoremIpsum.txt")).exists()) {
			fb = new FileByte(new File(getInputPath("LoremIpsum.txt")));
			results.add(new TestResult<>("FileByte[]: Check byte[] contents and validate via charset(UTF-8)", fb.getCharsetContents(StandardCharsets.UTF_8).equals(loremIpsum())));
		}
		
		//Check byte[] length on large PDF file
		if(new File(getInputPath("LargePDF.pdf")).exists()) {
			fb = new FileByte(new File(getInputPath("LargePDF.pdf")));
			results.add(new TestResult<>("FileByte[]: Check byte[] length on large PDF file", fb.getBytes().length == 5456281));
		}
		
		//Check byte[] length on large MKV file
		if(new File(getInputPath("LargeMKV.mkv")).exists()) {
			fb = new FileByte(new File(getInputPath("LargeMKV.mkv")));
			results.add(new TestResult<>("FileByte[]: Check byte[] length on large MKV file", fb.getBytes().length == 305828999));
		}
		
		//Check disassembleBytes and directReassembleBytes
		if(new File(getInputPath("LARGEJPEG.jpg")).exists()) {
			fb = new FileByte(new File(getInputPath("LARGEJPEG.jpg")));
			results.add(new TestResult<>("FileByte[]: Check disassembleBytes and directReassembleBytes", compareBytes(fb.getBytes(), fb.directReassembleBytes(fb.disassembleBytes(512)))));
		}
		
		//Check disassembleBytes and directReassembleBytes as null
		results.add(new TestResult<>("FileByte[]: Check disassembleBytes and directReassembleBytes as null", new FileByte().directReassembleBytes(null) == null));
		
		//Check disassembleBytes and reassembleBytes
		if(new File(getInputPath("LARGEJPEG.jpg")).exists()) {
			fb = new FileByte(new File(getInputPath("LARGEJPEG.jpg")));
			byte[] original =  fb.getBytes();
			fb.reassembleBytes(fb.disassembleBytes(512));
			results.add(new TestResult<>("FileByte[]: Check disassembleBytes and reassembleBytes", compareBytes(fb.getBytes(), original)));
		}
		
		//Check disassembleBytes and reassembleBytes as null
		results.add(new TestResult<>("FileByte[]: Check disassembleBytes and reassembleBytes as null", new FileByte().reassembleBytes(null) == null));
		
		//Check disassembly and reassembly on large files
		if(new File(getInputPath("LargeMKV.mkv")).exists()) {
			fb = new FileByte(new File(getInputPath("LargeMKV.mkv")));
			results.add(new TestResult<>("FileByte[]: Check disassembly and reassembly on large files", compareBytes(fb.getBytes(), fb.directReassembleBytes(fb.disassembleBytes(512)))));
		}
		
		//Clear FileByte's byte[]
		fb.clearBytes();
		results.add(new TestResult<>("FileByte[]: Clear FileByte's byte[]", fb.getBytes().length == 0));
		
		//Set FileByte by File
		if(new File(getInputPath("LoremIpsum.txt")).exists()) {
			results.add(new TestResult<>("FileByte[]: Set FileByte by File", compareBytes(new FileByte(new File(getInputPath("LoremIpsum.txt"))).getBytes(),loremIpsum().getBytes())));
		}
		
		//Set FileByte by Path
		if(new File(getInputPath("LoremIpsum.txt")).exists()) {
			results.add(new TestResult<>("FileByte[]: Set FileByte by Path", compareBytes(new FileByte(new File(getInputPath("LoremIpsum.txt")).getAbsolutePath()).getBytes(),loremIpsum().getBytes())));
		}
		
		//Set FileByte by byte[]
		if(new File(getInputPath("ABC.txt")).exists()) {
			results.add(new TestResult<>("FileByte[]: Set FileByte by byte[]", compareBytes(new FileByte("abc".getBytes()).getBytes(),new FileByte(new File(getInputPath("ABC.txt"))).getBytes())));
		}
		
		m.printMessage(this.className, "TestFileByte()", "Testing FileByte Finished...");
		
		System.gc();
		
		return results;
	}
	
	/*
	 * =======================================
	 *         TESTING SUB-METHODS
	 * =======================================
	 */
	
	
	private double getScore(){
		return new BigDecimal((this.score/this.total)*100).setScale(2, RoundingMode.HALF_UP).doubleValue();
	}
	
 	private boolean compareBytes(byte[] a, byte[] b) {
		if(a.length != b.length)
			return false;
		else {
			for(int i = 0; i < a.length; i++)
				if(Byte.compare(a[i], b[i]) != 0)
					return false;
		}
		return true;
	}
 	
	private void printTestResult(ArrayList<TestResult> tr) {
		String interpret = "";
		for(TestResult a:tr) {
			this.total++;
			interpret = "NOT PASS";
			if((Boolean)a.getValue()) {
				this.score++;
				interpret = "PASS";
			}
			System.out.println(a.getKey() + ": " + interpret);
		}
	}
	
	private String getOutputPath(String filename) {
		return OUTPUT_DIR + filename;
	}
	
	private String getInputPath(String filename) {
		return INPUT_DIR + filename;
	}
	
	/**
	 * Example Text from: https://en.wikipedia.org/wiki/Lorem_ipsum
	 * @return loremIpsum text.
	 */
 	private final String loremIpsum() {
		return "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
	}
}

/**
 * Reference: https://www.delftstack.com/howto/java/java-key-value-pair/
 */
class TestResult<K, V> implements Map.Entry<K, V> {
    private final K key;
    private V value;
    public TestResult(K key, V value) {
        this.key = key;
        this.value = value;
    }
    @Override
    public K getKey() {
        return key;
    }
    @Override
    public V getValue() {
        return value;
    }
    @Override
    public V setValue(V value) {
        V old = this.value;
        this.value = value;
        return old;
    }
}