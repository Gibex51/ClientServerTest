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
	private static final String STR_WRITE_FILE_FAILED = "Write downloaded file failed: ";
	private static final String STR_SAVE_FILE_TO = "Downloading file to: ";	
	private static final String STR_GREETING = "Client started. Type 'help' to get a command's list.";
	
	private static final String STR_HELP = "Command's list:\n"
			+ "list           - return list of files in shared directory.\n"
			+ "get:<filename> - download file with name <filename> from shared directory.\n"
			+ "quit           - exit from client application.";

	private static final int PORT = 10000;
	private static final String HOST = "localhost";
	
	public static void main(String[] args) throws IOException {
		Logger.addWriter(new FileLogWriter(LOG_FILE));
		
		File folder = new File(DOWNLOAD_DIRECTORY);
		if (!folder.exists()) folder.mkdirs();
		
		SocketConnection connection = new SocketConnection(HOST, PORT);
		
		BufferedReader consoleInput = new BufferedReader( new InputStreamReader(System.in));
		System.out.println(STR_GREETING);
		
		boolean quit = false;
		while (!quit) {
			System.out.print(STR_INPUT_CHAR);
			String rawCommand = consoleInput.readLine();
			Logger.write(STR_INPUT_COMMAND + rawCommand);
			String[] command = rawCommand.split("[:]", 2);
			switch (command[0]) {
			case "quit": {
				quit = true;
				break;
			}
			case "help": {
				System.out.println(STR_HELP);
				break;
			}
			case "get": {
				connection.writeLine(rawCommand);
				if (command.length == 2) {
					File file = new File(DOWNLOAD_DIRECTORY + PATH_SEP + FileUtils.filterFileName(command[1]));
					try {
						if (!file.exists()) file.createNewFile();
						System.out.println(STR_SAVE_FILE_TO + file.getAbsolutePath());
						String serverAnswer = connection.readFile(file);
						System.out.println(STR_SERVER_ANSWER + serverAnswer);
						Logger.write(serverAnswer);
						if (file.length() == 0) file.delete();
					} catch (Exception e) {
						Logger.write(STR_WRITE_FILE_FAILED + e.getMessage());
					}
				} else {
					String serverAnswer = connection.readLine();
					Logger.write(STR_SERVER_ANSWER + serverAnswer);
					System.out.println(serverAnswer);
				}
				break;
			}
			default: {
				connection.writeLine(rawCommand);
				String serverAnswer = connection.readLine();
				Logger.write(STR_SERVER_ANSWER + serverAnswer);
				System.out.println(serverAnswer);
			}
			}
		}
		connection.closeConnection();
	}
}
