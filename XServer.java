// File Name: XServer.java
// Date Started: 2/15/2024
// Date of last Update: 03/05/2024
// Description: Server Implementation File

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class XServer {

    XServer() {
        startServer();
    }

    private void startServer() {
        serverRunning = false;
        try {
            this.serverSocket = new ServerSocket(PORT);
            System.out.println("Server listening on port: " + PORT);
            serverRunning = true;

            new Thread(() -> {
                while (serverRunning) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        System.out.println("Client connected: " + clientSocket);

                        new Thread(() -> handleClient(clientSocket)).start();
                    } catch (IOException e) {
                        System.out.println("Error accepting client connection: " + e.getMessage());
                    }
                }
            }).start();
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
        }
    }

    private void handleClient(Socket clientSocket) {
        UserDataBase udb = new UserDataBase() ;
        UserFileManager ufm = new UserFileManager() ;
        try (
                ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream())
        ) {
            if(clientSocket != null && !clientSocket.isClosed()) {
                Commands command = (Commands) inputStream.readObject();
                User user = command.getUser();
                String useCommand = command.getCommand();
                System.out.println(useCommand) ;
                if (!user.loggedIn() && (!"LOGIN".equals(useCommand) && !"CREATE ACCOUNT".equals(useCommand))) {
                    command.setCommand("NOT LOGGED IN");
                    outputStream.writeObject(command);
                } else {
                    switch (useCommand) {
                        case "CREATE ACCOUNT":
                            createAccount(command, udb, outputStream);
                            break;
                        case "LOGIN":
                            loginUser(command, udb, ufm, outputStream) ;
                            break;
                        case "SEND MESSAGE":
                            sendMessage(command, udb, ufm, outputStream ) ;
                            break;
                        case "REFRESH":
                            refresh(command, udb, ufm, outputStream) ;
                            break;
                        case "DELETE":
                            deleteMessage(command, udb, ufm, outputStream) ;
                            break;
                        case "DISCONNECT":
                            command.getUser().setLoggedIn(false) ;
                            command.setCommand("Disconnected" ) ;
                            System.out.println(command.getUser().getUsername() + " " + command.getUser().loggedIn()) ;
                            outputStream.writeObject(command) ;
                            break ;
                        default:
                            break;
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e ) {
            System.out.println("Error handling client connection: " + e.getMessage());
        }finally {
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println("Error closing client socket: " + e.getMessage());
            }
        }
    }

    private void createAccount(Commands command, UserDataBase udb,
                               ObjectOutputStream outputStream) throws IOException {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy - HH:mm:ss:SSS");
        String formattedDateTime = currentDateTime.format(formatter);
        command.getUser().setDateJoined(formattedDateTime);
        String save = udb.saveUser(command.getUser() );
        if ("ACCOUNT_CREATED".equals(save)) {
            command.setCommand(save);
        } else {
            command.setCommand("USER NAME TAKEN");
        }
        outputStream.writeObject(command);
        outputStream.flush();
    }

    private void loginUser(Commands command, UserDataBase udb,
        UserFileManager ufm, ObjectOutputStream outputStream ) throws IOException {
        Commands response ;
        User confirmedUser = udb.confirmUser(command.getUser().getUsername(), command.getUser().getPassword());
        if (confirmedUser == null) {
            String why = udb.whyFailed(command.getUser().getUsername(), command.getUser().getPassword());
            response = new Commands(why);
            outputStream.writeObject(response);
        } else {
            ufm = new UserFileManager(confirmedUser);
            confirmedUser.setLoggedIn(true);
            response = new Commands("SUCCESS", confirmedUser,
            ufm.readMessageContents(confirmedUser), udb.fetchUsers());
            outputStream.writeObject(response);
            ufm.clear();
        }
    }

    private void sendMessage(Commands command, UserDataBase udb, UserFileManager ufm,
                             ObjectOutputStream outputStream) throws IOException {
        User sender = command.getUser();
        if(udb.confirmUser(command.getMessageToSend().getReceiver())) {
            if(ufm.writeFile(command.getMessageToSend())){
                command.setCommand("SUCCESS");
            } else {
                command.setCommand("FAILED TO SEND");
            }
        } else {
            command.setCommand("INVALID USER") ;
        }
        System.out.println(command.getCommand()) ;
        List<String> aUsers = udb.fetchUsers() ;
        command.setAllUsers(aUsers) ;
        Message[] myMessages = ufm.readMessageContents(sender) ;
        command.setMyMessages(myMessages);
        ufm.clear();
        outputStream.writeObject(command) ;
    }

    private void refresh(Commands command, UserDataBase udb, UserFileManager ufm,
                         ObjectOutputStream outputStream) throws IOException {
        command.setCommand("SUCCESS") ;
        Message[] myMess = ufm.readMessageContents(command.getUser()) ;
        command.setMyMessages(myMess);
        command.setAllUsers(udb.fetchUsers()) ;
        outputStream.writeObject(command) ;
    }

    private void deleteMessage(Commands command, UserDataBase udb,
                               UserFileManager ufm, ObjectOutputStream outputStream) throws IOException {
        if(ufm.deleteFile(command.getUser(), command.getMessageToSend())){
            command.setCommand("DELETED" ) ;
        } else {
            command.setCommand("FAILED TO DELETE") ;
        }
        Message[] myMessages = ufm.readMessageContents(command.getUser()) ;
        command.setMyMessages(myMessages);
        command.setAllUsers(udb.fetchUsers()) ;
        outputStream.writeObject(command) ;
    }

    private ServerSocket serverSocket;
    private final int PORT = 6999;
    private Boolean serverRunning;
}
