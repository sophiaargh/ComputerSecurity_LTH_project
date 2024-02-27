package client;

import java.io.*;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class KeychainFileReader {
    private BufferedReader reader;
    Map<String, String> ids = new HashMap<>();

    public KeychainFileReader(String path) throws IOException {
        InputStream inputStream = KeychainFileReader.class.getResourceAsStream(path);

        reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            // Split the line into components using ":" as a delimiter
            String[] parts = line.split(":");
            if (parts.length == 2) {
                String id = parts[0];
                String username = parts[1];

                ids.put(username, id);


            } else {
                System.err.println("Invalid line format: " + line);
            }
        }
    }

    public String getId(String username) {
        return ids.get(username);
    }

    public boolean userExists(String username) {
        return ids.containsKey(username);
    }

}