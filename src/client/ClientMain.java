package client;

import java.io.*;
import utils.*;

public class ClientMain {	
	private static final String PATH_SEP = File.separator;
	private static final String APP_HOME = System.getProperty("user.home") + PATH_SEP + "testapp";
	private static final String DOWNLOAD_DIRECTORY = APP_HOME + PATH_SEP + "downloads";
	private static final String LOG_FILE = APP_HOME + PATH_SEP + "clientlog.txt";
	
	private static final String STR_INPUT_CHAR = "#: ";
	private static final String STR_INPUT_COMMAND = "Command from input: ";
	private static final String STR_SERVER_ANSWER = "Server answer: ";
	private static final String STR_SAVE_FILE_TO = "Downloading file to: ";	
	private static final String STR_GREETING = "Client started. Type 'help' to get a command's list.";
	private static final String STR_SOCKET_CREATE_ERROR = "Socket initialization failed [port: %d host: %s]: ";
	private static final String STR_SOCKET_CLOSED_OK = "Socket closed";
	private static final String STR_SOCKET_CLOSE_FAILED = "Socket close failed: ";
	private static final String STR_ERROR_READ_FROM_CONSOLE = "Read from console failed: ";
	private static final String STR_SOCKET_IO_ERROR = "Socket IO error: ";
	
	private static final String STR_HELP = "Command's list:\n"
			+ "list           - return list of files in shared directory.\n"
			+ "get:<filename> - download file with name <filename> from shared directory.\n"
			+ "quit           - exit from client application.";

	private static final int PORT = 10000;
	private static final String HOST = "localhost";
	
	private static SocketConnection connection = null;
	private static BufferedReader consoleInput = null;
	
	public static void startClient() {
		File folder = new File(DOWNLOAD_DIRECTORY);
		if (!folder.exists()) folder.mkdirs();
		
		try {
			connection = new SocketConnection(HOST, PORT);
		} catch (IOException e) {
			Logger.write(String.format(STR_SOCKET_CREATE_ERROR, PORT, HOST) + e.getMessage());
		}
	}
	
	public static void stopClient() {
		try {
			connection.closeConnection();
			Logger.write(STR_SOCKET_CLOSED_OK);
		} catch (IOException e) {
			Logger.write(STR_SOCKET_CLOSE_FAILED + e.getMessage());
		}
	}
	
	public static String readFromConsole() {
		if (consoleInput == null) 
			consoleInput = new BufferedReader( new InputStreamReader(System.in));
		
		String readString = "";
		try {
			readString = consoleInput.readLine();
		} catch (Exception e) {
			readString = "";
			Logger.write(STR_ERROR_READ_FROM_CONSOLE + e.getMessage());
		}
		return readString;
	}
	
	public static void main(String[] args) {
		Logger.addWriter(new FileLogWriter(LOG_FILE));
		startClient();
		System.out.println(STR_GREETING);
		
		boolean quit = false;
		while (!quit) {
			System.out.print(STR_INPUT_CHAR);
			String rawCommand = readFromConsole();
			Logger.write(STR_INPUT_COMMAND + rawCommand);
			String[] command = rawCommand.split("[:]", 2);
			switch (command[0]) {
			case "quit": {
				stopClient();
				quit = true;
				break;
			}
			case "help": {
				System.out.println(STR_HELP);
				break;
			}
			case "get": {
				try {
					connection.writeLine(rawCommand);
					File file = null;
					if (command.length == 2) {
						file = new File(DOWNLOAD_DIRECTORY + PATH_SEP + FileUtils.filterFileName(command[1]));
						System.out.println(STR_SAVE_FILE_TO + file.getAbsolutePath());
					}
					String serverAnswer = connection.readFile(file);
					System.out.println(STR_SERVER_ANSWER + serverAnswer);
					Logger.write(STR_SERVER_ANSWER + serverAnswer);
				} catch (IOException e) {
					Logger.write(STR_SOCKET_IO_ERROR + e.getMessage());
				}
				break;
			}
			default: {
				try {
					connection.writeLine(rawCommand);
					String serverAnswer = connection.readLine();
					Logger.write(STR_SERVER_ANSWER + serverAnswer);
					System.out.println(serverAnswer);
				} catch (IOException e) {
					Logger.write(STR_SOCKET_IO_ERROR + e.getMessage());
				}				
			}
			}
		}
	}
}
