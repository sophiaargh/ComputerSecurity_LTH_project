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

    try {
      BufferedReader read = new BufferedReader(new InputStreamReader(System.in));

      System.out.print(">");
      String id = read.readLine();

      SSLSocket socket = createSocket(read, host, port);
      openConnection(socket);

      CommunicationsListener comms = new CommunicationsListener(socket);


      login(read, comms);

      //Start by sending empty message
      while (comms.listen()) {
        //System.out.println("received '" + in.readLine() + "' from server\n");
        System.out.print(">");

        String msg = read.readLine();
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

  private static void login(BufferedReader read, CommunicationsListener comms) throws IOException {

  }


  private static SSLSocket createSocket(BufferedReader read, String host, int port) throws IOException {
    SSLSocketFactory factory = null;
    try {
      System.out.println("Opening keychain...");
      System.out.println("Keystore username:");
      System.out.print(">");
      String username = read.readLine();

      KeychainFileReader keychain = new KeychainFileReader("/keychain.conf");

      if (!keychain.userExists(username)) {
        System.out.println("Keychain user doesn't exist");
        return null;
      }

      String id = keychain.getId(username);


      System.out.println("Keystore password:");
      System.out.print(">");
      String password = read.readLine();
      char[] passwordChars = password.toCharArray();

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

  private static void openKeychain() throws IOException {

  }

  private static void openConnection(SSLSocket socket) throws IOException {
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
}
