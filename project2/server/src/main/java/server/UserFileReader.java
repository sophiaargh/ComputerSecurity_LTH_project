package server;

import java.io.*;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class UserFileReader {
    private BufferedReader reader;
    Map<String, Integer> ids = new HashMap<>();
    Map<String, String> names = new HashMap<>();
    Map<String, String> passwords = new HashMap<>();
    Map<String, BigInteger> serials = new HashMap<>();
    Map<String, String> roles = new HashMap<>();
    Map<String, String> divisions = new HashMap<>();


    public UserFileReader(String path) throws IOException {
        InputStream inputStream = UserFileReader.class.getResourceAsStream(path);

        reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            // Split the line into components using ":" as a delimiter
            String[] parts = line.split(":");
            if (parts.length == 7) {
                int id = Integer.valueOf(parts[0]);
                String username = parts[1];
                String name = parts[2];
                String password = parts[3];
                String serial = parts[4];
                String role = parts[5];
                String division = parts[6];

                ids.put(username, id);
                names.put(username, name);
                passwords.put(username, password);
                serials.put(username, new BigInteger(serial));
                roles.put(username, role);
                divisions.put(username, division);


            } else {
                System.err.println("Invalid line format: " + line);
            }
        }
    }

    public int getId(String username) {
        return ids.get(username);
    }

    public String getName(String username) {
        return names.get(username);
    }

    public String getPassword(String username) {
        return passwords.get(username);
    }

    public boolean userExists(String username) {
        return passwords.containsKey(username);
    }

    public BigInteger getSerial(String username) {return  serials.get(username);}

    public String getRole(String username) {
        return roles.get(username);
    }

    public String getDivision(String username) {
        return divisions.get(username);
    }
}