package utils;

public class ConsoleLogWriter implements LogWriter {
	public void Write(String message) {
		System.out.println(message);
	}
}
