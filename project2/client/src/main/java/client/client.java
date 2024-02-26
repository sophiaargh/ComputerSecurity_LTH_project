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
    }/*
    if (args.length < 2) {
      System.out.println("USAGE: java client host port");
      System.exit(-1);
    }*/
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
      SSLSocketFactory factory = null;
      try {
        char[] password = "password".toCharArray();
        KeyStore ks = KeyStore.getInstance("JKS");
        KeyStore ts = KeyStore.getInstance("JKS");
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        SSLContext ctx = SSLContext.getInstance("TLSv1.2");
        // keystore password (storepass)

        InputStream ksInputStream = client.class.getResourceAsStream("/0/keystore");
        InputStream tsInputStream = client.class.getResourceAsStream("/0/truststore");

        ks.load(ksInputStream, password);
        // truststore password (storepass);
        ts.load(tsInputStream, password);
        kmf.init(ks, password); // user password (keypass)
        tmf.init(ts); // keystore can be used as truststore here
        ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        factory = ctx.getSocketFactory();
      } catch (Exception e) {
        throw new IOException(e.getMessage());
      }
      SSLSocket socket = (SSLSocket)factory.createSocket(host, port);
      System.out.println("\nsocket before handshake:\n" + socket + "\n");

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


      CommunicationsListener comms = new CommunicationsListener(socket);
      BufferedReader read = new BufferedReader(new InputStreamReader(System.in));

      String msg;
      //login(comms);
      //Start by sending empty message
      for (;;) {

        comms.listen();
        //System.out.println("received '" + in.readLine() + "' from server\n");
        System.out.print(">");

        msg = read.readLine();
        if (msg.equalsIgnoreCase("quit")) {
          break;
        }
        System.out.print("sending '" + msg + "' to server...");
        comms.sendLine(msg);
        System.out.println("done");

      }
      comms.close();
      read.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  /*private static void login(CommunicationsListener comms) throws IOException {
    //Get username prompt and send client answer
    System.out.println(in.readLine());
    out.println(read.readLine());
    out.flush();

    //Get password prompt and send client anser
    System.out.println(in.readLine());
    out.println(read.readLine());
    out.flush();

    System.out.println(in.readLine());
  }*/
}
