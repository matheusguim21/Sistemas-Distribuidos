import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	public static void main(String[] args) throws UnknownHostException {
		try {
			Socket socket = new Socket("127.0.0.1", 7000);
			System.out.println("Client started");

			BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
			BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintStream serverWriter = new PrintStream(socket.getOutputStream());

			new Thread(() -> {
				try {
					while (true) {
						String message = serverReader.readLine();
						if (message == null) {
							break;
						}
						System.out.println("Received from server: " + message);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}).start();

			while (true) {
				System.out.print("Enter message: ");
				String message = consoleReader.readLine();
				serverWriter.println(message);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}