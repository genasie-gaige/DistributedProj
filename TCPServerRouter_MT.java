import java.net.*;
import java.io.*;
public class TCPServerRouter_MT extends Thread {
   public static void main(String[] args) {
      for (int i = 1; i <= 6; ++i) {
         new TCPServerRouter_MT(1111 * i).start();
      }
   }

   private final int portNumber;
   private TCPServerRouter_MT(final int portNumber) {
      this.portNumber = portNumber;
   }

   public void run() {
      Object[][] routingTable = new Object[20][2];
      int ind = 0;


      Socket clientSocket = null; // socket for the thread

      boolean running = true;
		
      //Accepting connections
      ServerSocket serverSocket = null; // server socket for accepting connections
      try {
         serverSocket = new ServerSocket(portNumber);
         System.out.println(portNumber + ": ServerRouter is Listening on port: " + portNumber + ".");
      } catch (IOException e) {
         System.err.println(portNumber + ": Could not listen on port: " + portNumber + ".");
         Thread.currentThread().interrupt();
      }

      // Creating threads with accepted connections
      while (running) {
         try {
            clientSocket = serverSocket.accept();
            SThread_MT t = new SThread_MT(routingTable, clientSocket, ind, this.portNumber); // creates a thread with a random port
            t.start(); // starts the thread
            ind++; // increments the index
            System.out.println(portNumber + ": ServerRouter connected with Client/Server: " + clientSocket.getInetAddress().getHostAddress());
         } catch (IOException e) {
            System.err.println(portNumber + ": Client/Server failed to connect.");
            System.exit(1);
         }
      }//end while
      
      //closing connections
      try {
         clientSocket.close();
         serverSocket.close();
      } catch(IOException e) {
         System.err.println(portNumber + ": Could not close the client or server socket.");
         Thread.currentThread().interrupt();
      }
   }
}