package server;


import server.users.User;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import javax.net.*;
import javax.net.ssl.*;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;



public class server implements Runnable {
  private ServerSocket serverSocket = null;
  private CommunicationsBroadcaster comms;
  private static final Database database = new Database();
  private static final Logger eventLogger = new Logger();
  private static int numConnectedClients = 0;
  
  public server(ServerSocket ss) throws IOException {
    serverSocket = ss;
    newListener();
  }

  public void run() {
    try {
      SSLSocket socket=(SSLSocket)serverSocket.accept();
      newListener();
      SSLSession session = socket.getSession();
      Certificate[] cert = session.getPeerCertificates();
      String subject = ((X509Certificate) cert[0]).getSubjectX500Principal().getName();
      String issuer = ((X509Certificate) cert[0]).getIssuerX500Principal().getName();
      BigInteger serialNumber = ((X509Certificate) cert[0]).getSerialNumber();
      numConnectedClients++;
      System.out.println("client connected");
      System.out.println("client name (cert subject DN field): " + subject);
      System.out.println("issuer:\n" + issuer + "\n");
      System.out.println("serial number " + serialNumber);
      System.out.println(numConnectedClients + " concurrent connection(s)\n");

      comms = new CommunicationsBroadcaster(socket);


      comms.sendLine("Successfully established secure connection to server!");
      comms.sendLine("Please login to access system...");
      User user = Authenticator.authenticateUser(cert[0], comms, eventLogger);
      if (user != null) {
        System.out.println("Two-factor authentication successful");
        System.out.println("Login successful");
        new HospitalSystem(database, eventLogger).run(user, comms);

      } else {
        System.out.println("Two-factor authentication failed");
        System.out.println("Login failed");
        comms.sendLine("Two-factor authentication failed");

      }

      comms.close();
      numConnectedClients--;
      System.out.println("client disconnected");
      System.out.println(numConnectedClients + " concurrent connection(s)\n");
    } catch (IOException e) {
      System.out.println("Client died: " + e.getMessage());
      e.printStackTrace();
      return;
    } catch (Exception e) {
      try {
        System.out.print("Informing client that server crashed... ");
        comms.sendLine("Something went wrong, server crashed");
        comms.close();
        System.out.println("");
      } catch (Exception ee) {
        System.out.println("Couldn't inform client that server crashed");
      }

      System.out.println("Something went wrong: " + e.getMessage());
      return;
    }
  }





  private void newListener() { (new Thread(this)).start(); } // calls run()
  public static void main(String args[]) {
    System.out.println("\nServer Started\n");
    int port = -1;
    if (args.length >= 1) {
      port = Integer.parseInt(args[0]);
    } else {
      port = 9876;
    }
    String type = "TLSv1.2";
    try {
      ServerSocketFactory ssf = getServerSocketFactory(type);
      ServerSocket ss = ssf.createServerSocket(port);
      ((SSLServerSocket)ss).setNeedClientAuth(true); // enables client authentication
      new server(ss);
    } catch (IOException e) {
      System.out.println("Unable to start Server: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static ServerSocketFactory getServerSocketFactory(String type) {
    if (type.equals("TLSv1.2")) {
      SSLServerSocketFactory ssf = null;
      try { // set up key manager to perform server authentication
        SSLContext ctx = SSLContext.getInstance("TLSv1.2");
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        KeyStore ks = KeyStore.getInstance("JKS");
        KeyStore ts = KeyStore.getInstance("JKS");
        char[] password = "password".toCharArray();
        // keystore password (storepass)
        InputStream ksInputStream = server.class.getResourceAsStream("/serverkeystore");
        InputStream tsInputStream = server.class.getResourceAsStream("/servertruststore");

        ks.load(ksInputStream, password);
        // truststore password (storepass)
        ts.load(tsInputStream, password);
        kmf.init(ks, password); // certificate password (keypass)
        tmf.init(ts);  // possible to use keystore as truststore here
        ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        ssf = ctx.getServerSocketFactory();
        return ssf;
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      return ServerSocketFactory.getDefault();
    }
    return null;
  }


}
