import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserFileManager {

    UserFileManager(){ }

    UserFileManager(User user){
        this.user = user ;
        this.storagePath = USER_STORAGE + this.user.getUsername() ;
        this.messagePath = storagePath + "/MESSAGES/" ;
        confirmDatabase(storagePath) ;
        confirmMessages(messagePath) ;
    }

    public void confirmMessages(String path){
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();

            Path source = Paths.get("USER_DATA/Server/welcome.txt");
            Path target = Paths.get(directory.getPath(), "server_welcome.txt");

            try {
                Files.copy(source, target);
                System.out.println("File copied successfully.");
            } catch (IOException e) {
                throw new RuntimeException("Failed to copy file.", e);
            }
        }
    }

    public void confirmDatabase(String path){
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public Message[] readMessageContents(User user) throws IOException {
        String folderPath = "USER_DATA/" + user.getUsername() + "/MESSAGES/";

        File folder = new File(folderPath);

        List<Message> messages = new ArrayList<>();

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        String[] filenameParts = file.getName().split("_", 2);
                        if (filenameParts.length == 2) {
                            String senderName = filenameParts[0];
                            String originalFilename = filenameParts[1];

                            byte[] fileData = Files.readAllBytes(file.toPath());

                            Message message = new Message(senderName, user.getUsername(), fileData, originalFilename) ;
                            messages.add(message);
                        }
                    }
                }
            }
        } else {
            System.out.println("Folder does not exist or is not a directory.");
        }

        return messages.toArray(new Message[0]);
    }

    public void clear() {
        this.user = null;
        this.myMessages = null;
        this.sendFile = null;
        this.sendUser = null;
        this.command = null;
    }

    public boolean writeFile(Message message) {
        String sender = message.getSender();
        String receiver = message.getReceiver();
        byte[] fileBytes = message.getFile();

        String fileName = sender + "_" + message.getOriginalFilename();
        String filePath = USER_STORAGE + receiver + "/MESSAGES/" + fileName;

        confirmMessages(USER_STORAGE + receiver + "/MESSAGES/" ) ;

        String fileExtension = getFileExtension(fileName);
        if ("txt".equalsIgnoreCase(fileExtension) || "doc".equalsIgnoreCase(fileExtension)
                || "docx".equalsIgnoreCase(fileExtension) || "dat".equalsIgnoreCase(fileExtension)) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy - HH:mm:ss:SSS");
            String formattedDateTime = currentDateTime.format(formatter);
            String senderInfo = "From: " + sender + " @ " + formattedDateTime + "\n\n";
            return writeToFile(filePath, fileBytes, senderInfo);
        } else {
            return writeToFile(filePath, fileBytes);
        }
    }

    private synchronized boolean writeToFile(String filePath, byte[] fileBytes) {
        return writeToFile(filePath, fileBytes, "");
    }

    private synchronized boolean writeToFile(String filePath, byte[] fileBytes, String senderInfo) {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            if (!senderInfo.isEmpty()) {
                fos.write(senderInfo.getBytes());
            }

            fos.write(fileBytes);
            System.out.println("File '" + filePath + "' has been written successfully.");
            return true;
        } catch (IOException e) {
            System.err.println("Error writing file '" + filePath + "': " + e.getMessage());
            return false;
        }
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }

    public synchronized boolean deleteFile(User user, Message message){
        this.user = user ;
        String storagePath = USER_STORAGE + this.user.getUsername();
        String messagePath = storagePath + "/MESSAGES/";
        String filePath = messagePath + message.getSender() + "_" + message.getOriginalFilename() ;
        File fileToDelete = new File(filePath);

        if (fileToDelete.exists() && fileToDelete.isFile()) {

            if (fileToDelete.delete()) {
                System.out.println("File '" + filePath + "' deleted successfully.");
                return true;
            } else {
                System.err.println("Failed to delete file '" + filePath + "'.");
                return false;
            }
        } else {
            System.err.println("File '" + filePath + "' does not exist or is not a file.");
            return false;
        }
    }

    private final String USER_STORAGE = "USER_DATA/" ;
    private User user ;
    private File[] myMessages ;
    private File sendFile ;
    private User sendUser ;
    private Commands command;
    private String messagePath ;
    private String storagePath ;
}
