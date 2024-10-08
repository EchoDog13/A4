// Java provides SSLSocket and SSLServerSocket classes, which are roughly 
// equivalent to Socket and ServerSocket:
//       SSLServerSocket listens on a port for incoming connections, like ServerSocket
//       SSLSocket connects to an SSLServerSocket, like Socket, and represents an individual 
//       connection accepted from an SSLServerSocket.
// To create a SSLSocket or SSLServerSocket, we must use "factories"

// Socket factories are a convenient way to set TLS parameters that will 
// apply to Sockets created from the factory, e.g:
//       Which TLS versions to support
//       Which Ciphers and Hashes to use
//       Which Keys to use and which Certificates to trust
// As you might guess by the names
//       SSLServerSocketFactory creates SSLServerSocket objects
//       SSLSocketFactory creates SSLSocket objects

// Java uses KeyStore objects to store Keys and Certificates
// A KeyStore object is used when encrypting and authenticating
// The files that contain Keys and Certificates are password protected

// THE CODE BELOW IS INCOMPLETE AND HAS PROBLEMS
// FOR EXAMPLE, IT IS MISSING THE NECESSARY EXCEPTION HANDLING

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.security.KeyStore;
import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.*;

/**
 * 
 */
public class MyTLSFileServer {

   private static SSLServerSocketFactory getSSF() {
      // Create an SSLServerSocketFactory that uses the SSL/TLS protocol
      SSLServerSocketFactory ssf = null;

      try {
         // Get an SSL Context that speaks some version of TLS,
         // a KeyManager that can hold certs in X.509 format,
         // and a JavaKeyStore (JKS) instance
         SSLContext ctx = SSLContext.getInstance("TLS");
         KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
         KeyStore ks = KeyStore.getInstance("JKS");

         // Store the passphrase to unlock the JKS file.
         char[] passphrase = "passcode".toCharArray();

         // Load the keystore file and password
         ks.load(new FileInputStream("server.jks"), passphrase);

         // Init the KeyManagerFactory with a source of key material.
         kmf.init(ks, passphrase);

         // Initialize the SSL context with the keys.
         ctx.init(kmf.getKeyManagers(), null, null);

         // Get the factory we will use to create our SSLServerSocket
         ssf = ctx.getServerSocketFactory();

         // Return the factory
         return ssf;
      } catch (Exception ex) {
         // If an exception occurs, print it out and return null
         System.out.println("Exception: " + ex.getMessage());
         return null;
      }
   }

   /**
    * Main method to start the server
    * 
    * @param args command line arguments of server port
    * 
    */
   public static void main(String args[]) {
      int serverPort = 52002; // default port
      if (args.length != 1) {
         // If the user does not supply a port number, print usage and exit
         System.out.println("Usage: java MyTLSFileServer <port>");
         System.exit(1);

      } else {
         serverPort = Integer.parseInt(args[0]);
      }

      try {
         // use the getSSF method to get a SSLServerSocketFactory and
         // create our SSLServerSocket, bound to specified port
         ServerSocketFactory ssf = getSSF();
         SSLServerSocket ss = (SSLServerSocket) ssf.createServerSocket(serverPort);
         System.out.println("Server started on port " + serverPort);

         // Enable only TLSv1.2 and TLSv1.3
         String EnabledProtocols[] = { "TLSv1.2", "TLSv1.3" };
         ss.setEnabledProtocols(EnabledProtocols);
         System.out.println("Ready to recieve connections");

         // Loop forever, accepting connections from clients
         SSLSocket s = (SSLSocket) ss.accept();
         // Start a new thread to handle the connection
         new Thread(new connectHandler(s)).start();

      } catch (Exception e) {
         System.out.println("Exception: " + e.getMessage());
      }
   }

   /**
    * Inner class to handle the connection
    */
   private static class connectHandler implements Runnable {
      private SSLSocket s;

      /**
       * Constructor
       * 
       * @param s the SSLSocket to handle
       */
      public connectHandler(SSLSocket s) {
         this.s = s;
      }

      // The run method to handle the connection
      @Override
      public void run() {
         // Print out the IP address of the client
         System.out.println("Connection from " + s.getInetAddress());

         // Try to read the file name from the client and send the file
         try {
            // Create a BufferedReader and PrintWriter for the socket
            BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter writer = new PrintWriter(s.getOutputStream(), true);

            // Read the requested file name from the client
            String fileName = reader.readLine();
            System.out.println("Requested file: " + fileName);

            // Open the file and prepare to send its content
            File file = new File(fileName);
            if (file.exists()) {
               writer.println("OK"); // Indicate the file is found and will be sent

               // Create a buffered stream for the file to be read into before sending
               BufferedReader fileIn = new BufferedReader(new FileReader(file));

               System.out.println("Sending file...");
               String line;

               // Read the file line by line and send it to the client
               System.out.println("Sending file...");
               while ((line = fileIn.readLine()) != null) {
                  writer.println(line);
                  System.out.println(line);
               }
               // Close the file BufferedReader
               fileIn.close();
               System.out.println("File sent successfully.");
            } else {
               // If the file does not exist, notify the client
               writer.println("File not found");
            }
         } catch (Exception e) {
            // If an exception occurs, print it out
            e.printStackTrace();
         }
      }
   }
}
