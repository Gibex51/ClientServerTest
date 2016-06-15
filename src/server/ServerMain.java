package server;

import java.io.*;

public final class ServerMain {
	
	private static final String STR_INPUT_CHAR = "#: ";
	private static final int PORT = 10000;
	
	private static final String STR_INVALID_PARAMETER = "Invalid parameter <path>";	
	private static final String STR_GREETING = "Server started. Type 'help' to get a command's list.";
	private static final String STR_SET_DIR_OK = "Shared directory changed to ";
	
	private static final String STR_HELP = "Command's list:\n"
			+ "setdir:<path to dir> - set shared directory.\n"
			+ "quit                 - exit from server application.";
	
	public static void main(String[] args) throws IOException {
		Statistic.loadStatistic();
		Statistic.startSaveStatisticTimer(30);
		
		ClientAcceptor clientAcceptor = new ClientAcceptor(PORT);
		clientAcceptor.start();
		BufferedReader inFromConsole = new BufferedReader( new InputStreamReader(System.in));		
		System.out.println(STR_GREETING);
		
		boolean stopServer = false;
		while (!stopServer) {
			System.out.print(STR_INPUT_CHAR);
			String rawCommand = inFromConsole.readLine();
			String[] command = rawCommand.split("[:]", 2);
			switch (command[0]) {
			case "help": {
				System.out.println(STR_HELP);
				break;
			}
			case "setdir" : {
				if (command.length == 2)
					if (Statistic.setCustomSharedDir(command[1])) {
						System.out.println(STR_SET_DIR_OK + command[1]);
						break;
					}
				System.out.println(STR_INVALID_PARAMETER);
				break;
			}
			case "quit" : {
				clientAcceptor.interrupt();
				while (clientAcceptor.isAlive()) {};
				stopServer = true;
				Statistic.stopSaveStatisticTimer();
				Statistic.saveStatistic();
				break;
			}
			}
		}
		
	}
}
