package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Monitor {
	private boolean state;
	private DateTimeFormatter datetimeFormat;
	
	public Monitor(boolean state) {
		this.state = state;
		datetimeFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	}
	public void printMessage(String className, String methodName, String text) {
		if(state)
			System.out.println("(" + dtNow()  + ") " + className + "." + methodName + ": " + text);
	}
	private String dtNow() {
		return datetimeFormat.format(LocalDateTime.now());
	}
}
