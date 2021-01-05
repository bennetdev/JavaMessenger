package client.gui.customComponents;

import client.data.Chat;
import javafx.beans.property.SimpleObjectProperty;


//Just a tiny subclass of HBox so I can save chat in a convenient location
public class HBox extends javafx.scene.layout.HBox {
    public SimpleObjectProperty<Chat> chat = new SimpleObjectProperty<>();

    public HBox(Chat chat) {
        setChat(chat);
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
