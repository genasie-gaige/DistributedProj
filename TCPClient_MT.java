import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TCPClient_MT extends Thread {
	public static void main(String[] args) throws IOException {
		for (int i = 1; i <= 6; ++i) {
			new TCPClient_MT(1111 * i).start();
		}
	}


	private final int portNumber;
	private TCPClient_MT(final int portNumber) {
		this.portNumber = portNumber;
	}

	public void run() {
		try {
			// Variables for setting up connection and communication
			Socket socket = null; // socket to connect with ServerRouter
			PrintWriter out = null; // for writing to ServerRouter
			BufferedReader in = null; // for reading form ServerRouter
			InetAddress addr = InetAddress.getLocalHost();
			String host = addr.getHostAddress(); // Client machine's IP
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
			Reader reader = new FileReader("input/in.txt");
			BufferedReader fromFile = new BufferedReader(reader); // reader for the string file
			String fromServer; // messages received from ServerRouter
			String fromUser; // messages sent to ServerRouter
			String address = "192.168.1.68"; // destination IP (Server)
			long t0, t1, t;

			// Communication process (initial sends/receives
			out.println(address);// initial send (IP of the destination Server)
			fromServer = in.readLine();//initial receive from router (verification of connection)
			System.out.println(portNumber + ": ServerRouter: " + fromServer);
			out.println(host); // Client sends the IP of its machine as initial send
			t0 = System.currentTimeMillis();

			// Communication while loop
			while ((fromServer = in.readLine()) != null) {
				//System.out.println(portNumber + ": Server: " + fromServer);
	
				t1 = System.currentTimeMillis();
				t = t1 - t0;
				System.out.println(portNumber + ": Cycle time: " + t);

				fromUser = fromFile.readLine(); // reading strings from a file
				if (fromUser == null) {
					System.out.println(portNumber + ": END OF FILE");
				} else {
					//System.out.println(portNumber + ": Client: " + fromUser);
					out.println(fromUser); // sending the strings to the Server via ServerRouter
					t0 = System.currentTimeMillis();
				}	
				
				/*ADD*/
				System.out.println(portNumber + ": Server said: " + fromServer);
				fromClient = fromServer.toUpperCase(); // converting received message to upper case
				System.out.println(portNumber + ": User said: " + fromUser);
				out.println(fromClient);
				/*ADD*/

				if (fromServer.equals("BYE.")) // exit statement
					break;
			}

			// closing connections
			out.close();
			in.close();
			socket.close();
		} catch (IOException e) {
			System.err.println(portNumber + ": An error has occurred.");
			System.exit(1);
		}
	}
}
