import java.io.*;
import java.net.*;
import java.lang.Exception;
public class SThread_MT extends Thread {
	private Object[][] rTable; // routing table
	private PrintWriter out, outTo; // writers (for writing back to the machine and to destination)
	private BufferedReader in; // reader (for reading from the machine connected to)
	private String inputLine, outputLine, destination, addr; // communication strings
	private Socket outSocket; // socket for communicating with a destination
	private int ind; // indext in the routing table


	private final int portNumber;

	// Constructor
	SThread_MT(Object[][] rTable, Socket toClient, int index, int portNumber) throws IOException {
		out = new PrintWriter(toClient.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(toClient.getInputStream()));
		this.rTable = rTable;
		addr = toClient.getInetAddress().getHostAddress();
		rTable[index][0] = addr; // IP addresses 
		rTable[index][1] = toClient; // sockets for communication
		ind = index;

		this.portNumber = portNumber;
	}
	
	// Run method (will run for each machine that connects to the ServerRouter)
	public void run() {
		try {
			// Initial sends/receives
			destination = in.readLine(); // initial read (the destination for writing)
			System.out.println(portNumber + ": Forwarding to " + destination);
			out.println("Connected to the router."); // confirmation of connection
		
			// waits 10 seconds to let the routing table fill with all machines' information
			try {
    			Thread.currentThread().sleep(10000); 
	   		} catch (InterruptedException ie) {
				System.out.println(portNumber + ": Thread interrupted");
			}

			// loops through the routing table to find the destination
			for (int i = 0; i < 20; i++) {
				if (destination.equals((String) rTable[i][0])){
					outSocket = (Socket) rTable[i][1]; // gets the socket for communication from the table
					System.out.println(portNumber + ": Found destination: " + destination);
					outTo = new PrintWriter(outSocket.getOutputStream(), true); // assigns a writer
				}
			}
		
			// Communication loop	
			while ((inputLine = in.readLine()) != null) {
            	//System.out.println("Client/Server said: " + inputLine);
            	outputLine = inputLine; // passes the input from the machine to the output string for the destination
				if (outSocket != null) {
					outTo.println(outputLine); // writes to the destination
				}
            	if (inputLine.equals("Bye."))// exit statement
					break;

       		} // end while
		} // end try
		catch (IOException e) {
            System.err.println(portNumber + ": Could not listen to socket.");
        	System.exit(1);
    	}
	}
}