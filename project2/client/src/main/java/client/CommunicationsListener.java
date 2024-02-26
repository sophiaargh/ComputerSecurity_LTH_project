package client;

import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class CommunicationsListener {
    private final SSLSocket socket;
    private final PrintWriter out;
    private final BufferedReader in;
    public CommunicationsListener(SSLSocket socket) throws IOException {
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }


    /**
     * Will continiously listen to server messages until server send #END#
     * @throws IOException
     */
    public void listen() throws IOException {
        String msg;
        while (!(msg = in.readLine()).equals("#END#")) {
            System.out.println(msg);
        }
    }

    /**
     * Sends line to server and flushes instantly
     * @param msg
     */
    public void sendLine(String msg) {
        out.println(msg);
        out.flush();
    }

    /**
     * Closes connection and in + out
     * @throws IOException
     */
    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
