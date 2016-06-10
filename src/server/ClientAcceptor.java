package server;

import java.net.*;
import java.util.*;
import utils.*;

public class ClientAcceptor extends Thread {
	
	private final String STR_SERV_SOCKET_INIT_OK = "Server socket initialized on port: %d";
	private final String STR_SERV_SOCKET_INIT_FAILED = "Server socket initialization failed: ";
	private final String STR_SERV_SOCKET_CLOSED = "Server socket closed";
	private final String STR_SERV_SOCKET_CLOSE_FAILED = "Server socket close failed: ";
	private final String STR_WAIT_CLOSING_CONNECTIONS = "Wait for closing connections...";
	private final String STR_CONNECTIONS_CLOSED = "All connections closed";
	private final String STR_SOCKET_ACCEPTED = "Socket accepted: ";
	private final String STR_SOCKET_ACCEPT_FAILED = "Socket accept failed: ";
	
	private ServerSocket serverSocket = null;
	private ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	
	public ClientAcceptor(int port) {
		try {
			serverSocket = new ServerSocket(port);
			serverSocket.setSoTimeout(10000);
			Logger.Write(String.format(STR_SERV_SOCKET_INIT_OK, port));
		} catch (Exception e) {		
			Logger.Write(STR_SERV_SOCKET_INIT_FAILED + e.getMessage());
		}
	}
	
	private void CloseConnections() {
		Logger.Write(STR_WAIT_CLOSING_CONNECTIONS);
		for (ClientThread clientThread : clients) {
			if (!clientThread.isAlive()) continue;
			clientThread.InterruptThread();
			while (clientThread.isAlive()) {}
		}
		clients.clear();
		Logger.Write(STR_CONNECTIONS_CLOSED);
	}
	
	private void CloseSocket() {
		if ((serverSocket == null) || (serverSocket.isClosed())) return;
		try {
			serverSocket.close();
			Logger.Write(STR_SERV_SOCKET_CLOSED);
		} catch (Exception e) {
			Logger.Write(STR_SERV_SOCKET_CLOSE_FAILED + e.getMessage());
		}
	}
	
	@Override
	public void run() {
		while (true) {
			if (Thread.interrupted()) {
				CloseConnections();
				CloseSocket();
				return;
			}
			try {
				Socket socket = serverSocket.accept();
				if (socket != null) {
					Logger.Write(STR_SOCKET_ACCEPTED + socket.toString());
					ClientThread clientThread = new ClientThread(socket);
					clientThread.start();
					clients.add(clientThread);
				}
			} catch (Exception e) {
				if (e.getClass() != SocketTimeoutException.class)
					Logger.Write(STR_SOCKET_ACCEPT_FAILED + e.getMessage());
			}
		}
	}
}
