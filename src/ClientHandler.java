import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.io.PrintStream;

import java.net.Socket;

import java.util.List;

public class ClientHandler {
	private final Socket socket;
	private final List<ClientHandler> clients;
	private final BufferedReader reader;
	private final PrintStream writer;

	public ClientHandler(Socket socket, List<ClientHandler> clients) throws IOException {
		this.socket = socket;
		this.clients = clients;
		this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.writer = new PrintStream(socket.getOutputStream());
	}


	public void run() {
		try {
			while (true) {
				String message = reader.readLine();
				if (message == null) {
					break;
				}
				broadcast(message);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	public void sendMessage(String message) {
		writer.println(message);
	}

	private void broadcast(String message) {
		for (ClientHandler client : clients) {
			if (client != this) {
				client.sendMessage(message);
			}
		}
	}

	private void close() {
		try {
			socket.close();
			clients.remove(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}

