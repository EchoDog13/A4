// The client is usually much more straight forward
// Defaults will load Java’s set of Trusted Certificates
// Java will validate there is a path to a trusted CA
// By default, Java will NOT do hostname validation,
// but the more secure thing to do is to check!

// THE CODE BELOW IS INCOMPLETE AND HAS PROBLEMS
// FOR EXAMPLE, IT IS MISSING THE NECESSARY EXCEPTION HANDLING

/*(i) loads the server’s signed
certificate and associated private key stored in server.jks,
 (ii) binds to a
port supplied as a command-line argument and then 
(iii) listens for incoming connections. */

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import java.security.KeyStore;
import java.security.cert.X509Certificate;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.io.*;

public class MyTLSFileClient {
  public static void main(String args[]) {
    // set the default host, port, and filename
    String host = "KB-MBP-M3.local";
    int port = 52002;
    String filename = "notes.md";

    // check if the host, port, and filename are provided as command-line arguments,
    // if so override the default values
    if (args.length == 3) {
      host = args[0];
      port = Integer.parseInt(args[1]);
      filename = args[2];

    } else {
      System.out.println("Usage: java MyTLSFileClient <host> <port> <filename>");
    }

    // create an SSLContext object
    try {
      // Create an SSLSocketFactory that uses the SSL/TLS protocol
      SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();

      System.out.println("Client finished");
      // create an SSLSocket object
      SSLSocket socket = (SSLSocket) factory.createSocket(host, port);

      // set HTTPS-style checking of HostName _before_
      // the handshake
      SSLParameters params = new SSLParameters();
      params.setEndpointIdentificationAlgorithm("HTTPS");
      socket.setSSLParameters(params);
      // explicitly starting the TLS handshake
      socket.startHandshake();

      // create a BufferedReader object for reading messages from the server
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      // create a PrintWriter object for sending messages to the server
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

      // filename = "notes.md";
      // send the filename to the server
      out.println(filename);

      // get the X509Certificate for this session
      SSLSession session = socket.getSession();
      X509Certificate cert = (X509Certificate) session.getPeerCertificates()[0];

      // extract the CommonName, and then compare it to the hostname
      String commonName;
      try {
        commonName = getCommonName(cert);
        System.out.println("Common Name: " + commonName);
      } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
      }

      // recieve the file
      String response = in.readLine();
      System.out.println("Response: " + response);
      if (response.equals("OK")) {
        File file = new File("_" + filename); // Create a file with the same name
        try (FileOutputStream fos = new FileOutputStream(file)) { // Use FileOutputStream to write data
          byte[] buffer = new byte[4096]; // Buffer for reading file data
          int bytesRead;
          socket.getInputStream();

          // Read from the socket input stream and write to the file output stream
          while ((bytesRead = socket.getInputStream().read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead); // Write the bytes read to the file
          }
          System.out.println("File received successfully.");
        } catch (IOException e) {
          System.err.println("Error receiving file: " + e.getMessage());
        }
      } else {
        System.out.println("File not found.");
      }
    } catch (Exception e) {
      // TODO: handle exception
      e.printStackTrace();
    }
  }

  /**
   * Extract the Common Name from an X509Certificate
   * 
   * @param cert the X509Certificate to extract the Common Name from
   * @return the Common Name
   */
  static String getCommonName(X509Certificate cert) {
    String name = cert.getSubjectX500Principal().getName();
    String cn = null;

    try {
      LdapName ln = new LdapName(name);

      // Rdn: Relative Distinguished Name
      for (Rdn rdn : ln.getRdns())
        if ("CN".equalsIgnoreCase(rdn.getType()))
          cn = rdn.getValue().toString();

    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    return cn;
  }

}
