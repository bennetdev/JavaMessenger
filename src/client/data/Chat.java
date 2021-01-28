package client.data;

import client.gui.customComponents.ChatHBox;
import client.gui.customComponents.ChatView;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Random;


/*
Holds all the data of Chats
 */
public class Chat implements Serializable {

    //Save on Server
    private String userName;
    private ObservableList<Message> messages = FXCollections.observableArrayList();

    //For GUI
    private LocalDateTime creationTime = LocalDateTime.now();
    private SimpleObjectProperty<Color> color = new SimpleObjectProperty<>();
    private transient ChatView chatView;
    private transient ChatHBox chatHBox;

    private static final Random RANDOM = new Random();

    public Chat(String userName) {
        double brightness;
        do {
            setColor(new Color(getRandom().nextDouble(), getRandom().nextDouble(), getRandom().nextDouble(), 1));
            brightness = getColor().getRed() + getColor().getGreen() + getColor().getBlue();
        }
        //Brightness tolerance: LightMode(0.3, 1), DarkMode(2, 2.7)
        while (brightness < 0.3 || brightness > 1);

        this.setUserName(userName);
    }

    public Message getLastMessage() {
        if(getMessages().size() < 1) return null;
        else return getMessages().get(getMessages().size() - 1);
    }

    @Override
    public String toString() {
        if(getLastMessage() != null) {
            if(getLastMessage().getText().length() > 50) {
                return "Chat with " + getUserName() + ", last Message: \"" +
                        getLastMessage().getText().replaceAll("\\n", "   ").substring(0, 40) + "\"";
            } else {
                return "Chat with " + getUserName() + ", last Message: \"" +
                        getLastMessage().getText().replaceAll("\\n", "   ") + "\"";
            }
        } else {
            return "Chat with " + getUserName() + ", no messages yet";
        }
    }

    public Color getColor() {
        return color.getValue();
    }

    public void setColor(Color color) {
        this.color.setValue(color);
    }

    public SimpleObjectProperty<Color> getColorProperty() {
        return color;
    }

    public void setColorProperty(SimpleObjectProperty<Color> color) {
        this.color = color;
    }

    private Random getRandom() {
        return RANDOM;
    }

    public ObservableList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ObservableList<Message> messages) {
        this.messages = messages;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ChatView getChatView() {
        return chatView;
    }

    public void setChatView(ChatView chatView) {
        this.chatView = chatView;
    }

    public ChatHBox getChatHBox() {
        return chatHBox;
    }

    public void setChatHBox(ChatHBox chatHBox) {
        this.chatHBox = chatHBox;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }
}
