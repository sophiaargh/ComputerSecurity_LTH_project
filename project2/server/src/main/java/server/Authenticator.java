package server;


import server.users.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Authenticator {

    /**
     * Tries to log in the user
     * @param comms
     * @return Null if login failed. User instance if login successful
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static User authenticateUser(CommunicationsBroadcaster comms) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        UserFileReader userFileReader = new UserFileReader("/users.txt");
        System.out.println("Logging in process");


        //Prompt client with to write username and get results
        comms.sendLine("Username: ");
        String username = comms.awaitClient();

        System.out.println("Client provided username: " + username);

        //Prompt client to write password and get results
        comms.sendLine("Password: ");
        String password = comms.awaitClient();

        System.out.println("Client provided password: " + password);

        if (!userFileReader.userExists(username)) {
            System.out.println("Provided username doesn't exist");
            return null;
        }

        //hash password with username as salt
        byte[] encodedhash = digest.digest((password + username).getBytes(StandardCharsets.UTF_8));
        String hashedPassword = bytesToHex(encodedhash);
        String correctHash = userFileReader.getPassword(username);

        System.out.println("Client provided password: " + password);
        System.out.println("Clients hashed password: " + hashedPassword);
        System.out.println("Correct hashed password: " + correctHash);

        if (hashedPassword.equals(correctHash)) {
            return createUserInstance(userFileReader, username);
        } else {
            return null;
        }
    }

    private static User createUserInstance(UserFileReader userFileReader, String username) {
        int id = userFileReader.getId(username);
        String name = userFileReader.getName(username);

        Division division = new Division(userFileReader.getDivision(username));
        return
            switch (userFileReader.getRole(username)) {
                case "doctor" -> new Doctor(name, id, division);
                case "nurse" -> new Nurse(name, id, division);
                case "patient" -> new Patient(name, id, division);
                case "govagency" -> new GovAgency(name, id, division);
                default -> null;
        };

        //System.out.println("Unknown role");

    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
