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

public class MyTLSFileServer {

   private static SSLServerSocketFactory getSSF() {
      SSLServerSocketFactory ssf = null;

      try {
         // Get an SSL Context that speaks some version of TLS,
         // a KeyManager that can hold certs in X.509 format,
         // and a JavaKeyStore (JKS) instance
         SSLContext ctx = SSLContext.getInstance("TLS");
         KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
         KeyStore ks = KeyStore.getInstance("JKS");

         // Store the passphrase to unlock the JKS file.
         char[] passphrase = "serverKeyStore".toCharArray();

         // Load the keystore file. The passphrase is an optional parameter to
         // allow for integrity checking of the keystore. Could be null
         ks.load(new FileInputStream("server.jks"), passphrase);

         // Init the KeyManagerFactory with a source of key material.
         // The passphrase is necessary to unlock the private key contained.
         kmf.init(ks, passphrase);

         // Initialize the SSL context with the keys.
         ctx.init(kmf.getKeyManagers(), null, null);

         // Get the factory we will use to create our SSLServerSocket
         ssf = ctx.getServerSocketFactory();

         return ssf;
      } catch (Exception ex) {
         System.out.println("Exception: " + ex.getMessage());
         return null;
      }
   }

   public static void main(String args[]) {
      final int serverPort = 52002;

      try {
         // use the getSSF method to get a SSLServerSocketFactory and
         // create our SSLServerSocket, bound to specified port
         ServerSocketFactory ssf = getSSF();
         SSLServerSocket ss = (SSLServerSocket) ssf.createServerSocket(serverPort);
         System.out.println("Server started on port " + serverPort);

         String EnabledProtocols[] = { "TLSv1.2", "TLSv1.3" };
         ss.setEnabledProtocols(EnabledProtocols);
         System.out.println("Ready to recieve connections");

         SSLSocket s = (SSLSocket) ss.accept();

         connectHandler ch = new connectHandler(s);
         Thread t = new Thread(ch);
         t.start();

      } catch (Exception e) {
         // TODO: handle exception
      }
   }

   private static class connectHandler implements Runnable {
      private SSLSocket s;

      public connectHandler(SSLSocket s) {
         this.s = s;
      }

      public void run() {

         System.out.println("Connection from " + s.getInetAddress());
         try (
               BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
               PrintWriter writer = new PrintWriter(s.getOutputStream(), true)) {
            // Read the requested file name from the client
            String fileName = reader.readLine();
            System.out.println("Requested file: " + fileName);
            System.out.println("heelo");

            // Open the file and prepare to send its content
            File file = new File(fileName);
            if (file.exists() && file.isFile()) {
               writer.println("OK"); // Indicate the file is found and will be sent

               // Create a buffered stream for the file
               try (BufferedInputStream fileIn = new BufferedInputStream(new FileInputStream(file));
                     OutputStream out = s.getOutputStream()) {

                  byte[] buffer = new byte[4096];
                  int bytesRead;
                  while ((bytesRead = fileIn.read(buffer)) != -1) {
                     out.write(buffer, 0, bytesRead);
                  }
                  out.flush();
               }
               System.out.println("File sent successfully.");
            } else {
               writer.println("File not found"); // Notify the client if the file does not exist
            }
         } catch (IOException e) {
            System.out.println("Exception during file transfer: " + e.getMessage());
         }
      }
   }
}
