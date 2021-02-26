package server;

import client.data.Message;

import java.io.Serializable;
import java.util.ArrayList;


/*
It's just a small class to store all relevant data of users while they are offline. It will be saved in a file.
 */
public class OfflineUser implements Serializable {
    private String name;
    private String password;
    private ArrayList<Message> undeliveredMessages = new ArrayList<>();

    public OfflineUser(ClientUser user) {
        setName(user.getName());
        setPassword(user.getPassword());
    }

    /*
    Use only if messaging an unknown user
     */
    public OfflineUser(Message message) {
        setName(message.getTo());
        getUndeliveredMessages().add(message);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<Message> getUndeliveredMessages() {
        return undeliveredMessages;
    }

    public void setUndeliveredMessages(ArrayList<Message> undeliveredMessages) {
        this.undeliveredMessages = undeliveredMessages;
    }
}
