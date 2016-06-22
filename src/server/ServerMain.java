package server;

import java.io.*;

import utils.FileLogWriter;
import utils.Logger;

public final class ServerMain {
	
	private static final int PORT = 10000;
	private static final String STR_INPUT_CHAR = "#: ";
	private static final String STR_INVALID_PARAMETER = "Invalid parameter <path>";	
	private static final String STR_GREETING = "Server started. Type 'help' to get a command's list.";
	private static final String STR_SET_DIR_OK = "Shared directory changed to ";
	private static final String STR_ERROR_READ_FROM_CONSOLE = "Read from console failed: ";

	private static final String STR_HELP = "Command's list:\n"
			+ "setdir:<path to dir> - set shared directory.\n"
			+ "quit                 - exit from server application.";
	
	
	private static BufferedReader consoleInput = null;
	private static ClientAcceptor clientAcceptor = null;
	
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
	
	public static void startServer() {
		Logger.write(Statistic.loadStatistic());
		Statistic.startSaveStatisticTimer(30);
		
		clientAcceptor = new ClientAcceptor(PORT);
		clientAcceptor.start();
	}
	
	public static void stopServer() {
		clientAcceptor.interrupt();
		while (clientAcceptor.isAlive()) {};
		
		try {
			if (consoleInput != null)
				consoleInput.close();
		} catch (Exception e) {
			Logger.write(e.getMessage());
			consoleInput = null;
		}
		
		Statistic.stopSaveStatisticTimer();
		Logger.write(Statistic.saveStatistic());
	}
	
	public static void main(String[] args) {
		Logger.addWriter(new FileLogWriter(Strings.LOG_FILE));
		
		startServer();
		System.out.println(STR_GREETING);
		
		boolean stopServer = false;
		while (!stopServer) {
			System.out.print(STR_INPUT_CHAR);
			String rawCommand = readFromConsole();
			String[] command = rawCommand.split("[:]", 2);
			switch (command[0]) {
			case "help": {
				System.out.println(STR_HELP);
				break;
			}
			case "setdir" : {
				if (command.length == 2)
					if (Statistic.setCustomSharedDir(command[1])) {
						Logger.write(STR_SET_DIR_OK + command[1]);
						System.out.println(STR_SET_DIR_OK + command[1]);
						break;
					}
				System.out.println(STR_INVALID_PARAMETER);
				break;
			}
			case "quit" : {
				stopServer();
				stopServer = true;
				break;
			}
			}
		}
		
	}
}
