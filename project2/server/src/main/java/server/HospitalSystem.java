package server;


import server.users.Doctor;
import server.users.Patient;
import server.users.Permissions;
import server.users.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class HospitalSystem {
    /**
     * Displays the possible actions according to the user's role
     * @param user
     * @param out
     */
    public void displayAction(User user, PrintWriter out){
        out.print("What do you want to do? ");
        String role = user.getRole();
        out.print("You are a " + role + " you can:");
        switch (role) {
            case "Patient" -> out.println("read: press 1, quit: 'q'");
            case "Nurse" -> out.println("read: press 1, write: press 2, quit: 'q'");
            case "Doctor" -> out.println("read: press 1, write: press 2, create: press 3, quit: 'q'");
            case "Government Agency" -> out.println("read: press 1, delete: press 4, quit: 'q'");
        }
        out.flush();
    }
    public void action(User user, BufferedReader in, PrintWriter out) throws IOException {
        String action = in.readLine();
        System.out.println("Client provided action: " + action);
        String quit = "q";
        Permissions pAction = Permissions.NONE;
        switch (action) {
            case "q" -> out.println("quitting");

            //TODO: quit
            case "1" -> pAction = Permissions.READ;
            case "2" -> pAction = Permissions.WRITE;
            case "3" -> pAction = Permissions.CREATE;
            case "4" -> pAction = Permissions.DELETE;
            default -> out.println(action + " is not a valid action");
        }
        out.flush();
        if (user.getPerms().contains(pAction)){
            out.println("Excecuting "+ pAction);
        }else{
            out.println("You are not authorized to do that");
        }


        out.flush();

    }
    public void run(User user, BufferedReader in, PrintWriter out) throws IOException {
        out.print("Successfully logged in!  -- ");
        out.print("id: " + user.getId());
        out.print("; Name:" + user.getName());
        out.print("; Division: " + user.getDiv());
        out.println("; Role: " + user.getRole());
        out.flush();

        displayAction(user, out);
        action(user, in, out);

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
