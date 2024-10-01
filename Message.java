import java.io.Serializable;

public class Message implements Serializable {
    private String sender;
    private String receiver;
    private byte[] file;
    private String originalFilename;

    Message(String sender, String receiver, byte[] file, String originalFilename) {
        this.sender = sender;
        this.receiver = receiver;
        this.file = file;
        this.originalFilename = originalFilename;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public byte[] getFile() {
        return file;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }
}