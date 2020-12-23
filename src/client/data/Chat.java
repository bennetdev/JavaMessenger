package client.data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Random;


/*
Holds all the data of Chats
 */
public class Chat {

    private ObservableList<Message> messages = FXCollections.observableArrayList();
    private Color color;
    private String userName;

    private static final Random RANDOM = new Random();

    public Chat(String userName) {
        setColor(new Color(getRandom().nextDouble(), getRandom().nextDouble(), getRandom().nextDouble(), 1));
        this.userName = userName;
    }

    @Override
    public String toString() {
        if(messages.size() > 0) {
            return "Chat with " + userName + ", last Message: \"" + messages.get(messages.size() - 1) + "\"";
        } else {
            return "Chat with " + userName + ", no messages yet";
        }
    }

    public Color getColor() {
        return color;
    }

    private void setColor(Color color) {
        this.color = color;
    }

    private Random getRandom() {
        return RANDOM;
    }
}
