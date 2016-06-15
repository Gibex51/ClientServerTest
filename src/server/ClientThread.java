package server;

import java.io.*;
import java.net.*;
import utils.*;

public class ClientThread extends Thread {
	
	private final String STR_CLIENT_THREAD_CREATED = "Client thread created: ";
	private final String STR_CLIENT_THREAD_INTERRUPTED = "Client thread interrupted";
	private final String STR_RECEIVED_MESSAGE = "Received message: ";	
	
	private final String STR_OUT_FILE_NOT_EXISTS = "File not exists";
	private final String STR_OUT_INVALID_PARAM = "Invalid parameter. Use get:<filename>";
	private final String STR_OUT_UNKNOWN_COMMAND = "Unknown command: ";
	
	private SocketConnection connection = null;
	
	public ClientThread(Socket socket) {		
		connection = new SocketConnection(socket);
		Logger.write(STR_CLIENT_THREAD_CREATED + socket.toString());
	}
	
	public void interruptThread() {		
		connection.closeConnection();
		Thread.currentThread().interrupt();
		Logger.write(STR_CLIENT_THREAD_INTERRUPTED);
	}
	
	private void executeCommand(String message) throws IOException {
		Logger.write(STR_RECEIVED_MESSAGE + message);
		String[] command = message.split("[:]");
		switch (command[0]) {
		case "list": {
			connection.writeLine(Statistic.getFileListFromSharedDirectory());
			break;
		}
		case "get": {
			if (command.length == 2) {
				File file = Statistic.getFileFromSharedDirectory(command[1]);
				if (file.exists() && file.isFile())
					connection.writeFile(file);
				else
					connection.writeLine(STR_OUT_FILE_NOT_EXISTS);			
			} else {
				connection.writeLine(STR_OUT_INVALID_PARAM);
			}
			break;
		}
		default: {
			connection.writeLine(STR_OUT_UNKNOWN_COMMAND + command[0]);
		}
		}
	}
	
	@Override
	public void run() {
		while (true) {	
			if (Thread.interrupted()) {
				connection.closeConnection();
				return;
			}
			try {
				executeCommand(connection.readLine());
			} catch (Exception e) {
				interruptThread();
			}
		}
	}
	
}
