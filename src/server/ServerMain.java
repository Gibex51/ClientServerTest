package server;

import java.io.*;

public final class ServerMain {
	
	private static final String STR_INPUT_CHAR = "#: ";
	private static final int PORT = 10000;
	
	public static void main(String[] args) throws IOException {
		Statistic.loadStatistic();
		Statistic.startSaveStatisticTimer(30);
		
		ClientAcceptor clientAcceptor = new ClientAcceptor(PORT);
		clientAcceptor.start();
		BufferedReader inFromConsole = new BufferedReader( new InputStreamReader(System.in));		
		
		boolean stopServer = false;
		while (!stopServer) {
			System.out.print(STR_INPUT_CHAR);
			String command = inFromConsole.readLine();
			switch (command) {
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
