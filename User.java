import java.io.Serializable;

public class User implements Serializable {
    private String username ;
    private String password ;
    private String emailAddress ;
    private String dateJoined ;
    private boolean loggedIn = false ;

    User(String username, String password, String emailAddress, String dateJoined){
        this.username = username ;
        this.password = password ;
        this.emailAddress = emailAddress ;
        this.dateJoined = dateJoined ;
        this.loggedIn = false ;
    }

    User(){
        this.username = null ;
        this.password = null ;
        this.emailAddress = null ;
        this.dateJoined = null ;
        this.loggedIn = false ;
    }

    User(String username, String password ){
        this.username = username ;
        this.password = password ;
        this.emailAddress = null ;
        this.dateJoined = null ;
        this.loggedIn = false ;
    }

    public String getUsername() {
        return this.username ;
    }

    public String getPassword(){
        return this.password ;
    }

    public String getEmailAddress() {
        return this.emailAddress ;
    }

    public String getDateJoined(){
        return this.dateJoined ;
    }

    public void setUsername(String username){
        this.username = username ;
    }

    public void setPassword(String password){
        this.password = password ;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress ;
    }

    public void setDateJoined(String dateJoined){
        this.dateJoined = dateJoined ;
    }

    public void setLoggedIn(boolean set){
        this.loggedIn = set ;
    }

    public boolean loggedIn() {
        return this.loggedIn ;
    }

}
