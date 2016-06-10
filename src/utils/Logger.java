package utils;

import java.time.*;
import java.time.format.*;
import java.util.*;

public final class Logger {
	private static ArrayList<LogWriter> writers = new ArrayList<LogWriter>();
	
	public static void AddWriter(LogWriter writer) {
		if (writer != null)
			synchronized (writers) {
				writers.add(writer);
			}
	}
	
	public static void Write(String message) {
		synchronized (writers) {
			for (LogWriter writer: writers) {
				LocalDateTime today = LocalDateTime.now();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				writer.Write(String.format("[%s] %s", today.format(formatter), message));
			}
		}
	}
}
