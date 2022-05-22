package testing;

import java.io.IOException;
import java.util.Scanner;

import utils.*;

public class Testing {
	private Scanner scan;
	private Utility m = new Utility();
	public Testing() {
		scan = new Scanner(System.in);
		int choice = -1;
		do {
			choice = menu();
		}while(choice != 0);
		System.out.println("System Exiting...");
		System.gc();
		System.exit(0);
	}
	private int menu() {
		m.cls();
		System.out.println("====Testing Utility====");
		System.out.println("1 - FileHandlers");
		System.out.println("2 - Hasher");
		System.out.println("0 - Exit");
		System.out.println("=======================");
		return Integer.parseInt(scan.nextLine());
	}
}