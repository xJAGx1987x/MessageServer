// Name: Josh George
// File Name: ServerDriver.java
// Date Started: 2/15/2024
// Date of last Update: 03/05/2024
// Description: Implementation of User_Database. Used so the server can keep track
//  of known users in order to protect user data.

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDataBase {

    UserDataBase() {
        this.database = new HashMap<>() ;
        confirmDataBase() ;
        loadDatabase() ;
    }

    public synchronized String saveUser(User user) {
        loadDatabase();
        if (!database.containsKey(user.getUsername())) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(DB_PATH, true))) {
                String userData = user.getUsername() + " ~ " + user.getPassword()
                        + " ~ " + user.getEmailAddress() + " ~ " + user.getDateJoined() ;

                writer.write(userData);
                writer.newLine();
                writer.flush();
                database.put(user.getUsername(), user);
                System.out.println("User saved: " + user.getUsername());
                return "ACCOUNT_CREATED";
            } catch (IOException e) {
                System.out.println(e) ;
                new RuntimeException(e) ;
            }
        }
        return null ;
    }

    public synchronized User confirmUser(String username, String password) {
        loadDatabase();
        if (database.containsKey(username)) {
            User dbUser = database.get(username);
            if (dbUser.getPassword().equals(password)) {
                return dbUser;
            }
        }
        return null;
    }

    public String whyFailed(String username, String password){
        loadDatabase();
        if (database.containsKey(username)) {
            User dbUser = database.get(username);
            if (dbUser.getPassword().equals(password)) {
                return "SHOULDN'T HAVE" ;
            }
            return "Incorrect Password" ;
        }
        return "User Not Found";
    }

    public boolean confirmUser(String username ) {
        loadDatabase();
        if (database.containsKey(username)) {
            return true;
        }
        return false;
    }

    private void loadDatabase() {
        this.database.clear() ;
        try (BufferedReader dbReader = new BufferedReader(new FileReader(DB_PATH))) {
            String line;
            while ((line = dbReader.readLine()) != null) {
                String[] userData = line.split(" ~ ");
                if (userData.length == 4) {
                    String username = userData[0];
                    String password = userData[1];
                    String emailAddress = userData[2];
                    String dateJoined = userData[3];

                    User newUser = new User(username, password, emailAddress, dateJoined);
                    this.database.put(username, newUser) ;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> fetchUsers() {
        List<String> allUsers = new ArrayList<>();
        try (BufferedReader dbReader = new BufferedReader(new FileReader(DB_PATH))) {
            String line;
            while ((line = dbReader.readLine()) != null) {
                String[] userData = line.split(" ~ ");
                if (userData.length == 4) {
                    String username = userData[0];
                    allUsers.add(username);
                }
            }
            return allUsers ;
        } catch (IOException e) {
            e.printStackTrace();
            return null ;
        }
    }

    private synchronized void confirmDataBase() {
        try {
            Path path = Path.of(DB_PATH);

            if (!Files.exists(path)) {
                Files.createFile(path);
                System.out.println("File created: " + path);
            } else {
                System.out.println("File already exists: " + path);
            }
        } catch (IOException e) {
            System.err.println("Failed to create or access the file.");
            e.printStackTrace();
        }
    }

    private final String DB_PATH = "USER_DATA/Server/userDatabase.txt" ;
    private final Map<String, User> database ;
}
