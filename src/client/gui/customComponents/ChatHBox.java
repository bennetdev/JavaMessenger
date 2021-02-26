package client.gui.customComponents;

import client.data.Chat;
import javafx.beans.property.SimpleObjectProperty;


/*
This is just a tiny subclass of HBox so I can save an instance of Chat in a convenient location.
 */
public class ChatHBox extends javafx.scene.layout.HBox {

    public SimpleObjectProperty<Chat> chat = new SimpleObjectProperty<>();

    public ChatHBox(Chat chat) {
        setChat(chat);
        getChat().setChatHBox(this);
    }

    public SimpleObjectProperty<Chat> getChatProperty() {
        return chat;
    }

    public Chat getChat() {
        return chat.getValue();
    }

    public void setChat(Chat chat) {
        this.chat.setValue(chat);
    }
}
