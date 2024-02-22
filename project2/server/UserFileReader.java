package server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserFileReader {
    private BufferedReader reader;
    Map<String, Integer> ids = new HashMap<>();
    Map<String, String> names = new HashMap<>();
    Map<String, String> passwords = new HashMap<>();
    Map<String, String> roles = new HashMap<>();
    Map<String, String> divisions = new HashMap<>();


    public UserFileReader(String path) throws IOException {
        reader = new BufferedReader(new FileReader(path));

        String line;
        while ((line = reader.readLine()) != null) {
            // Split the line into components using ":" as a delimiter
            String[] parts = line.split(":");
            if (parts.length == 6) {
                int id = Integer.valueOf(parts[0]);
                String username = parts[1];
                String name = parts[2];
                String password = parts[3];
                String role = parts[4];
                String division = parts[5];

                ids.put(username, id);
                names.put(username, name);
                passwords.put(username, password);
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

    public String getRole(String username) {
        return roles.get(username);
    }

    public String getDivision(String username) {
        return divisions.get(username);
    }
}