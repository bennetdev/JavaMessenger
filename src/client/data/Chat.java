package client.data;

import client.data.cipher.Cipher;
import client.gui.customComponents.ChatHBox;
import client.gui.customComponents.ChatView;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;


/*
Instances of Chat hold all the data necessary to visually and logically represent a chat. This mainly includes
the chats messages and the name of the chat partner but also the color used to represent that user in the GUI.
Apart from storing data, there are some limited utility methods like configureColor() or @Override toString().
 */
public class Chat {

    // Logical Data
    private String userName;
    private final ObservableList<Message> messages = FXCollections.observableArrayList();
    private Cipher cipher;
    private final SimpleBooleanProperty online = new SimpleBooleanProperty();

    // Data for GUI
    private LocalDateTime creationTime = LocalDateTime.now();
    private SimpleObjectProperty<Color> color = new SimpleObjectProperty<>();
    private ChatView chatView;
    private ChatHBox chatHBox;

    private static final Random RANDOM = new Random();

    public Chat(String userName) {
        commonConstructorCode();
        setUserName(userName);
    }

    public Chat(Message message) {
        commonConstructorCode();
        setUserName(message.getFrom());
        getMessages().add(message);
    }

    private void commonConstructorCode() {
        configureColor();
        setCipher(new Cipher());
    }

    private void configureColor() {
        double brightness;
        do {
            setColor(new Color(getRandom().nextDouble(), getRandom().nextDouble(), getRandom().nextDouble(), 1));
            brightness = getColor().getRed() + getColor().getGreen() + getColor().getBlue();
        }
        //Brightness tolerance: LightMode(0.3, 1), DarkMode(2, 2.7)
        while (brightness < 0.3 || brightness > 1);

    }

    public Message getLastMessage() {
        if(getMessages().size() < 1) return null;
        else return getMessages().get(getMessages().size() - 1);
    }

    @Override
    public String toString() {
        if(getLastMessage() != null) {
            if(getLastMessage().getText().length() > 50) {
                return "Chat with " + getUsername() + ", last Message: \"" +
                        getLastMessage().getText().replaceAll("\\n", "   ").substring(0, 40) + "\"  Online: " + isOnline();
            } else {
                return "Chat with " + getUsername() + ", last Message: \"" +
                        getLastMessage().getText().replaceAll("\\n", "   ") + "\"  Online: " + isOnline();
            }
        } else {
            return "Chat with " + getUsername() + ", no messages yet";
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

    public String getUsername() {
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

    public Cipher getCipher() {
        return cipher;
    }

    public void setCipher(Cipher cipher) {
        this.cipher = cipher;
    }

    public SimpleBooleanProperty getOnlineProperty() {
        return online;
    }
    public void setOnline(boolean online) {
        this.online.setValue(online);
    }
    public boolean isOnline() {
        return online.getValue();
    }

    public static ArrayList<String> getUsernameList(Collection<Chat> chats) {
        ArrayList<String> names = new ArrayList<>();
        for(Chat chat : chats) {
            names.add(chat.getUsername());
        }
        return names;
    }
}
