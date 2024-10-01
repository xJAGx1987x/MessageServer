import java.io.Serializable;
import java.util.List;

public class Commands implements Serializable {
    private User user;
    private String command;
    private Message messageToSend  ;
    private Message[] myMessages ;
    private List<String> allUsers;

    Commands(){
        this.command = null ;
        this.user = null ;
        this.messageToSend  = null ;
        this.myMessages = null ;
        this.allUsers = null ;
    }

    Commands(String command){
        this.command = command ;
        this.user = null ;
        this.messageToSend  = null ;
        this.myMessages = null ;
        this.allUsers = null ;
    }

    Commands(String command, User user){
        this.command = command ;
        this.user = user ;
        this.messageToSend = null ;
        this.myMessages = null ;
        this.allUsers = null ;
    }

    Commands(String command, User user, Message messageToSend){
        this.command = command ;
        this.user = user;
        this.messageToSend = messageToSend ;
        this.myMessages = null ;
        this.allUsers = null ;
    }

    Commands(String command, User user, Message[] myMessage, List<String> allUsers){
        this.command = command ;
        this.user = user;
        this.messageToSend = null ;
        this.myMessages = myMessage ;
        this.allUsers = allUsers ;
    }
    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    // Getter and setter for user
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Message getMessageToSend () {
        return messageToSend ;
    }

    public void setMessageToSend (Message messageToSend ) {
        this.messageToSend  = messageToSend ;
    }

    public Message[] getMyMessages(){
        return myMessages ;
    }

    public void setMyMessages(Message[] messages){
        this.myMessages = messages ;
    }

    public List<String> getAllUsers() {
        return allUsers;
    }

    public void setAllUsers(List<String> allUsers) {
        this.allUsers = allUsers;
    }

}
