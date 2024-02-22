package server;


import server.users.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class HospitalSystem {

    public void run(User user, BufferedReader in, PrintWriter out) throws IOException {
        out.print("Successfully logged in!  -- ");
        out.print("id: " + user.getId());
        out.print("; Name:" + user.getName());
        out.print("; Division: " + user.getDiv());
        out.println("; Role: " + user.getClass());
        out.flush();

        String clientMsg = null;
        while ((clientMsg = in.readLine()) != null) {
            String rev = new StringBuilder(clientMsg).reverse().toString();
            System.out.println("received '" + clientMsg + "' from client");
            System.out.print("sending '" + rev + "' to client...");

            out.println(rev);
            out.flush();
            System.out.println("done\n");
        }
    }

}
