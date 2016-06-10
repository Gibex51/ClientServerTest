package utils;

import java.io.*;

public class FileLogWriter implements LogWriter {
	
	private String outputFile;
	
	public FileLogWriter(String outputFile) {
		this.outputFile = outputFile;
	}
	
	public void Write(String message) {
		try {
			PrintWriter printWriter = new PrintWriter(new FileOutputStream(outputFile, true));
			printWriter.write(message + "\n");
			printWriter.flush();
			printWriter.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
