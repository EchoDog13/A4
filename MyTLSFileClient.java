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
    String host = args[0];
    int port = Integer.parseInt(args[1]);
    String filename = args[2];

    // create an SSLContext object

    SSLContext context = createSSLContext();
    try {

      SSLSocketFactory factory = context.getSocketFactory();
      SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
      // set HTTPS-style checking of HostName _before_
      // the handshake

      try {

      } catch (Exception e) {
        // TODO: handle exception
      }
      SSLParameters params = new SSLParameters();
      params.setEndpointIdentificationAlgorithm("HTTPS");
      socket.setSSLParameters(params);

      try {

      } catch (Exception e) {
        // TODO: handle exception
      }
      socket.startHandshake(); // explicitly starting the TLS handshake

      // at this point, can use getInputStream and
      // getOutputStream methods as you would in a regular Socket
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

      // get the X509Certificate for this session
      SSLSession session = socket.getSession();
      X509Certificate cert = (X509Certificate) session.getPeerCertificates()[0];
      filename = "4c";
      out.write(filename);
      out.flush();

      // extract the CommonName, and then compare
      // getCommonName(cert);

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
        try (BufferedInputStream fileIn = new BufferedInputStream(socket.getInputStream());
            OutputStream fileOut = new FileOutputStream("received_" + filename)) {
          byte[] buffer = new byte[4096];
          int bytesRead;
          while ((bytesRead = fileIn.read(buffer)) != -1) {
            fileOut.write(buffer, 0, bytesRead);
          }
          fileOut.flush();
        }
        System.out.println("File received successfully.");
      } else {
        System.out.println("File not found.");
      }
    } catch (Exception e) {
      // TODO: handle exception
      System.out.println(e.getMessage());
    }
  }

  private static SSLContext createSSLContext() {
    SSLContext context = null;
    try {
      context = SSLContext.getInstance("TLS");
      KeyStore ks = KeyStore.getInstance("JKS");

      // load the keystore

    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    return context;
  }

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
