package client;

import java.math.BigInteger;
import java.io.*;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.security.KeyStore;
import java.security.cert.*;

/*
 * This example shows how to set up a key manager to perform client
 * authentication.
 *
 * This program assumes that the client is not inside a firewall.
 * The application can be modified to connect to a server outside
 * the firewall by following SSLSocketClientWithTunneling.java.
 */

public class client {
  BufferedReader read;

  public static void main(String[] args) throws Exception {
    String host = null;
    int port = -1;
    for (int i = 0; i < args.length; i++) {
      System.out.println("args[" + i + "] = " + args[i]);
    }
    try { /* get input parameters */
      host = args[0];
      port = Integer.parseInt(args[1]);
    } catch (Exception e) {
      System.out.println("USAGE: java client host port");
      //System.exit(-1);
      host = "localhost";
      port = 9876;
    }

    new client().run(host, port);
  }

  public void run(String host, int port) {
    try {
      read = new BufferedReader(new InputStreamReader(System.in));


      SSLSocket socket = createSocket(read, host, port);
      openConnection(socket);
      CommunicationsListener comms = new CommunicationsListener(socket);

      //Start listening to server
      while (comms.listen()) {
        String msg = getUserInput();
        if (msg.equalsIgnoreCase("quit")) {
          break;
        }

        comms.sendLine(msg);
      }

      System.out.println("Closing connection...");

      comms.close();
      read.close();

      System.out.println("Connection closed.");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  private SSLSocket createSocket(BufferedReader read, String host, int port) throws IOException {
    SSLSocketFactory factory = null;
    try {
      //FINAL -------------
      /*
      System.out.println("Opening keychain...");
      String username = getUserInput("Username:");

      KeychainFileReader keychain = new KeychainFileReader("/keychain.conf");

      if (!keychain.userExists(username)) {
        System.out.println("Keychain user doesn't exist");
        return null;
      }

      String id = keychain.getId(username);


      String password = getUserInput("Password:");
      char[] passwordChars = password.toCharArray();
*/
      //--------------------
      //DEBUG --------------

      char[] passwordChars = "password".toCharArray();
      String id = getUserInput("Select certificate id:");


      //--------------------




      KeyStore ks = KeyStore.getInstance("JKS");
      KeyStore ts = KeyStore.getInstance("JKS");
      KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
      TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
      SSLContext ctx = SSLContext.getInstance("TLSv1.2");
      // keystore password (storepass)

      InputStream ksInputStream = client.class.getResourceAsStream("/" + id + "/keystore");
      InputStream tsInputStream = client.class.getResourceAsStream("/" + id + "/truststore");



      ks.load(ksInputStream, passwordChars);
      // truststore password (storepass);
      ts.load(tsInputStream, passwordChars);
      kmf.init(ks, passwordChars); // user password (keypass)
      tmf.init(ts); // keystore can be used as truststore here
      ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
      factory = ctx.getSocketFactory();
    } catch (Exception e) {
      throw new IOException(e.getMessage());
    }

    SSLSocket socket = (SSLSocket)factory.createSocket(host, port);
    System.out.println("\nsocket before handshake:\n" + socket + "\n");
    return socket;
  }


  private void openConnection(SSLSocket socket) throws IOException {
    /*
     * send http request
     *
     * See SSLSocketClient.java for more information about why
     * there is a forced handshake here when using PrintWriters.
     */
    socket.startHandshake();
    SSLSession session = socket.getSession();
    Certificate[] cert = session.getPeerCertificates();
    String subject = ((X509Certificate) cert[0]).getSubjectX500Principal().getName();
    String issuer = ((X509Certificate) cert[0]).getIssuerX500Principal().getName();
    BigInteger serialNumber = ((X509Certificate) cert[0]).getSerialNumber();
    System.out.println("certificate name (subject DN field) on certificate received from server:\n" + subject + "\n");
    System.out.println("issuer:\n" + issuer + "\n");
    System.out.println("serial number " + serialNumber);
    System.out.println("socket after handshake:\n" + socket + "\n");
    System.out.println("secure connection established\n\n");
  }

  private String getUserInput(String prompt) throws IOException {
    System.out.println(prompt);
    System.out.print(">");
    return getUserInput();
  }

  private String getUserInput() throws IOException {
    return read.readLine();
  }
}
