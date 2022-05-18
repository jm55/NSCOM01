package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Monitor {
	boolean state;
	DateTimeFormatter datetimeFormat;
	
	public Monitor() {
		datetimeFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	}
	public void printMessage(String className, String methodName, String text, boolean ln) {
		if(state)
			if(ln)
				System.out.println("(" + dtNow()  + ") " + className + "." + methodName + ": " + text);
			else
				System.out.print("(" + dtNow()  + ") " + className + "." + methodName + ": " + text);
	}
	public String dtNow() {
		return datetimeFormat.format(LocalDateTime.now());
	}
}
