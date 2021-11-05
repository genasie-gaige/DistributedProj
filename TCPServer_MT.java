import java.io.*;
import java.net.*;
import java.util.Scanner;


// Uses Multithreading to simulate several client/servers operating at once.
public class TCPServer_MT extends Thread {
	public static void main(String[] args) {
		for (int i = 1; i <= 6; ++i) {
			new TCPServer_MT(1111 * i).start();
		}
	}

	private final int portNumber;
	private TCPServer_MT(final int portNumber) {
		this.portNumber = portNumber;
	}

	public void run() {
		try {
			// Variables for setting up connection and communication
			Socket socket = null; // socket to connect with ServerRouter
			PrintWriter out = null; // for writing to ServerRouter
			BufferedReader in = null; // for reading form ServerRouter
			InetAddress addr = InetAddress.getLocalHost();
			String host = addr.getHostAddress(); // Server machine's IP
			String routerName = "DESKTOP-DAGAVGF"; // ServerRouter host name

			// Tries to connect to the ServerRouter
			try {
				socket = new Socket(routerName, portNumber);
				out = new PrintWriter(socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			} catch (UnknownHostException e) {
				System.err.println(portNumber + ": Don't know about router: " + routerName);
				System.exit(1);
			} catch (IOException e) {
				System.err.println(portNumber + ": Couldn't get I/O for the connection to: " + routerName);
				System.exit(1);
			}

			// Variables for message passing
			Reader reader = new FileReader("input/in.txt"); /*ADD*/
			BufferedReader fromFile = new BufferedReader(reader); /*ADD*/
			String fromServer; // messages sent to ServerRouter
			String fromClient; // messages received from ServerRouter
			String address = "192.168.1.72"; // destination IP (Client)
			long t0, t1, t; /*ADD*/

			// Communication process (initial sends/receives)
			out.println(address);// initial send (IP of the destination Client)
			fromClient = in.readLine();// initial receive from router (verification of connection)
			System.out.println(portNumber + ": ServerRouter: " + fromClient);
			out.println(host); /*ADD*/
			t0 = System.currentTimeMillis(); /*ADD*/

			// Communication while loop
			while ((fromClient = in.readLine()) != null) {
				System.out.println(portNumber + ": Client said: " + fromClient);
				fromServer = fromClient.toUpperCase(); // converting received message to upper case
				System.out.println(portNumber + ": Server said: " + fromServer);
				out.println(fromServer); // sending the converted message back to the Client via ServerRouter
				
				
				/*ADD*/
				t1 = System.currentTimeMillis();
				t = t1 - t0;
				System.out.println(portNumber + ": Cycle time: " + t);

				fromServer = fromFile.readLine(); // reading strings from a file
				if (fromServer == null) {
					System.out.println(portNumber + ": END OF FILE");
				} else {
					//System.out.println(portNumber + ": Client: " + fromUser);
					out.println(fromServer); // sending the strings to the Server via ServerRouter
					t0 = System.currentTimeMillis();
				}
				/*ADD*/

				if (fromClient.equals("Bye.")) // exit statement
					break;
			}

			// closing connections
			out.close();
			in.close();
			socket.close();
		} catch (IOException e) {
			System.err.println(portNumber + ": An error occurred.");
			System.exit(1);
		}
	}
}
