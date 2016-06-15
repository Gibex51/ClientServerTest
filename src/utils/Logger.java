package utils;

import java.time.*;
import java.time.format.*;
import java.util.*;

public final class Logger {
	
	private static ArrayList<LogWriter> writers = new ArrayList<LogWriter>();
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	public static void addWriter(LogWriter writer) {
		if (writer != null)
			synchronized (writers) {
				writers.add(writer);
			}
	}
	
	public static void write(String message) {		
		for (LogWriter writer: writers) {
			LocalDateTime today = LocalDateTime.now();		
			synchronized (writer) {
				writer.write(String.format("[%s] %s", today.format(formatter), message));
			}
		}
	}
}
