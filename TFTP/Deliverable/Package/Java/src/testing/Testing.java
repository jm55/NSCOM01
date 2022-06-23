package testing;

import java.io.IOException;
import java.util.Scanner;

import utils.*;
/**
 * @deprecated
 * Conducts specific and automated testing of functions.
 */
public class Testing {
	private Scanner scan;
	private Utility m = new Utility();
	public Testing() {
		scan = new Scanner(System.in);
		int choice = -1;
		do {
			choice = menu();
			if(choice == 1) { //Automated Testing
				m.cls();
				new Automated_Testing();
			}
		}while(choice != 0);
		System.out.println("System Exiting...");
		System.gc();
		System.exit(0);
	}
	private int menu() {
		m.cls();
		System.out.println("====Testing Utility====");
		System.out.println("1 - Automated Testing");
		System.out.println("2 - FileHandlers");
		System.out.println("3 - Hasher");
		System.out.println("0 - Exit");
		System.out.println("=======================");
		return Integer.parseInt(scan.nextLine());
	}
}