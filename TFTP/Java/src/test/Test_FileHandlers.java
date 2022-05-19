package test;

import data.FileHandlers;
public class Test_FileHandlers {
	public Test_FileHandlers() {
		System.out.println("Test_FileHandlers");
		FileHandlers fh = new FileHandlers();
		fh.openFile();
		System.out.println("Checking getFileExt(): " + fh.getFileExt() + ", " + fh.getFileExt(fh.getFile()) + ", " + fh.getFileExt(fh.getFile().getAbsolutePath()));
		fh = null;
	}
}
