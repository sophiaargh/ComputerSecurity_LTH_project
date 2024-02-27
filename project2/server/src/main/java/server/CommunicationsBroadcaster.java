package server;

import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class CommunicationsBroadcaster {
    private final SSLSocket socket;
    private final BufferedReader in;
    private final PrintWriter out;

    public CommunicationsBroadcaster(SSLSocket socket) throws IOException {
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    /**
     * Sends message line to client
     * @param msg
     */
    public void sendLine(String msg) {
        out.println(msg);
        out.flush();
    }

    /**
     * Sends #END# to signal to the client that it should stop listening and then listens to a one line response by client
     * @return Client's response
     * @throws IOException
     */
    public String awaitClient() throws IOException{
        out.println("#END#");
        out.flush();
        return in.readLine();
    }

    /**
     * Closes connection and broadcasts #QUIT# to client
     * @throws IOException
     */
    public void close() throws IOException {
        sendLine("#QUIT#");
        in.close();
        out.close();
        socket.close();
    }


}
