package utils;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

public class SocketConnection {
	
	private final String STR_UNKNOWN_DATA = "Unknown data";
	private final String STR_RECEIVE_FILE_OK = "Receive file sucsessfully";
	private final String STR_FILE_IS_NULL = "File is null";
	
	private final byte DATA_IS_STRING = 0;
	private final byte DATA_IS_FILE = 1;
	private final int BUFFER_SIZE = 262144;
	
	Socket socket = null;
	DataOutputStream outStream = null;
	DataInputStream inStream = null;
	
	private void initializeIOStreams() throws IOException {
		outStream = new DataOutputStream(socket.getOutputStream());
		inStream = new DataInputStream(socket.getInputStream());
	}
	
	public SocketConnection(String host, int port) throws UnknownHostException, IOException {
		socket = new Socket(host, port);
		initializeIOStreams();
	}
	
	public SocketConnection(Socket socket) throws IOException {
		this.socket = socket;
		initializeIOStreams();
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
		
		byte[] buffer = new byte[BUFFER_SIZE];
		int readedTotal = 0;
		BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
		while (readedTotal < fileSize) {
			int readed = reader.read(buffer, 0, BUFFER_SIZE);
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
	
		if (file == null) 
			return STR_FILE_IS_NULL;
		if (!file.exists()) file.createNewFile();
		BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(file));
				
		buffer = new byte[BUFFER_SIZE];
		int readedTotal = 0;		
		while (readedTotal < fileSize) {
			int readed = inStream.read(buffer, 0, BUFFER_SIZE);		
			if (readed < 0) break;
			writer.write(buffer, 0, readed);
			readedTotal += readed;
		}
		writer.close();
		return STR_RECEIVE_FILE_OK;
	}
	
	public void closeConnection() throws IOException {
		if ((socket == null) || (socket.isClosed())) return;
			socket.close();
	}
	
	@Override
	public String toString() {		
		return socket.toString();	
	}
}
