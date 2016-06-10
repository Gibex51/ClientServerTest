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

	private static final int PORT = 10000;
	private static final String HOST = "localhost";
	
	public static void main(String[] args) throws IOException {
		Logger.AddWriter(new FileLogWriter(LOG_FILE));
		
		File folder = new File(DOWNLOAD_DIRECTORY);
		if (!folder.exists()) folder.mkdirs();
		
		SocketConnection connection = new SocketConnection(HOST, PORT);
		
		BufferedReader consoleInput = new BufferedReader( new InputStreamReader(System.in));
		boolean quit = false;
		while (!quit) {
			System.out.print(STR_INPUT_CHAR);
			String rawCommand = consoleInput.readLine();
			Logger.Write(STR_INPUT_COMMAND + rawCommand);
			String[] command = rawCommand.split("[:]");
			switch (command[0]) {
			case "quit": {
				quit = true;
				break;
			}
			case "get": {
				connection.WriteLine(rawCommand);
				if (command.length == 2) {
					File file = new File(DOWNLOAD_DIRECTORY + PATH_SEP + FileUtils.filterFileName(command[1]));
					try {
						if (!file.exists()) file.createNewFile();
						String serverAnswer = connection.ReadFile(file);
						System.out.println(STR_SERVER_ANSWER + serverAnswer);
						Logger.Write(serverAnswer);
						if (file.length() == 0) file.delete();
					} catch (Exception e) {
					}
				} else {
					String serverAnswer = connection.ReadLine();
					Logger.Write(STR_SERVER_ANSWER + serverAnswer);
					System.out.println(serverAnswer);
				}
				break;
			}
			default: {
				connection.WriteLine(rawCommand);
				String serverAnswer = connection.ReadLine();
				Logger.Write(STR_SERVER_ANSWER + serverAnswer);
				System.out.println(serverAnswer);
			}
			}
		}
		connection.CloseConnection();
	}
}
