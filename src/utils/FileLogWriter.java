package utils;

import java.io.*;

public class FileLogWriter implements LogWriter {
	
	private String outputFile;
	
	public FileLogWriter(String outputFile) {
		this.outputFile = outputFile;
	}
	
	public void write(String message) {
		PrintWriter printWriter = null;
		try {
			printWriter = new PrintWriter(new FileOutputStream(outputFile, true));
			printWriter.write(message + "\n");		
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if (printWriter != null) {
				printWriter.flush();
				printWriter.close();
			}
		}
	}
}
