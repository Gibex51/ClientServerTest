package utils;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

public class SocketConnection {
	
	private final String STR_SOCKET_ERROR = "Socket initialization failed: ";
	private final String STR_SOCKET_ERROR_EX = STR_SOCKET_ERROR + "[port: %d host: %s]: ";
	private final String STR_SOCKET_CLOSED_OK = "Socket closed";
	private final String STR_SOCKET_CLOSE_FAILED = "Socket close failed: ";
	private final String STR_UNKNOWN_DATA = "Unknown data";
	private final String STR_RECEIVE_FILE_OK = "Receive file sucsessfully";
	
	private final byte DATA_IS_STRING = 0;
	private final byte DATA_IS_FILE = 1;
	
	Socket socket = null;
	DataOutputStream outStream = null;
	DataInputStream inStream = null;
	
	private void initializeIOStreams() throws IOException {
		outStream = new DataOutputStream(socket.getOutputStream());
		inStream = new DataInputStream(socket.getInputStream());
	}
	
	public SocketConnection(String host, int port) {
		try {
			socket = new Socket(host, port);
			initializeIOStreams();
		} catch (Exception e) {
			Logger.write(String.format(STR_SOCKET_ERROR_EX, port, host) + e.getMessage());
		}
	}
	
	public SocketConnection(Socket socket) {
		try {
			this.socket = socket;
			initializeIOStreams();
		} catch (Exception e) {
			Logger.write(STR_SOCKET_ERROR + e.getMessage());
		}
	}
	
	public void writeLine(String message) throws IOException {
		outStream.writeByte(DATA_IS_STRING);	
		outStream.writeUTF(message);
	}
	
	public String readLine() throws IOException {
		byte flag = inStream.readByte();
		if (flag != DATA_IS_STRING)
			return "";
		return inStream.readUTF();
	}
	
	public void writeFile(File file) throws IOException {		
		outStream.writeByte(DATA_IS_FILE);
		
		long fileSize = file.length();
		byte[] sizeInBytes = ByteBuffer.allocate(Long.SIZE).putLong(fileSize).array();
		outStream.write(sizeInBytes, 0, 8);
		
		int bufferSize = 262144;
		byte[] buffer = new byte[bufferSize];
		int readedTotal = 0;
		BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
		while (readedTotal < fileSize) {
			int readed = reader.read(buffer, 0, bufferSize);
			readedTotal += readed;
			outStream.write(buffer, 0, readed);
		}
		reader.close();
	}
	
	public String readFile(File file) throws IOException {
		byte flag = inStream.readByte();
		if (flag != DATA_IS_FILE) {	
			if (flag == DATA_IS_STRING)
				return inStream.readUTF();
			else
				return STR_UNKNOWN_DATA;
		}
		
		byte[] buffer = new byte[8];
		if (inStream.read(buffer, 0, 8) != 8) 
			return STR_UNKNOWN_DATA;
		long fileSize = ByteBuffer.wrap(buffer).getLong();
	
		int bufferSize = 262144;
		buffer = new byte[bufferSize];
		int readedTotal = 0;
		BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(file));
		while (readedTotal < fileSize) {
			int readed = inStream.read(buffer, 0, bufferSize);		
			if (readed < 0) break;
			writer.write(buffer, 0, readed);
			readedTotal += readed;
		}
		writer.close();
		return STR_RECEIVE_FILE_OK;
	}
	
	public void closeConnection() {
		if ((socket == null) || (socket.isClosed())) return;
		try {
			socket.close();
			Logger.write(STR_SOCKET_CLOSED_OK);
		} catch(Exception e) {
			Logger.write(STR_SOCKET_CLOSE_FAILED + e.getMessage());
		}
	}
	
	@Override
	public String toString() {		
		return socket.toString();	
	}
}
