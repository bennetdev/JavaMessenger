package Client.gui;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import Client.data.Client;
import Client.data.Message;

public class Controller {
    private Client client;

    public Controller(Client client){
        this.client = client;
    }

    public void sendMessage(TextArea textArea, TextField usernameField) {
        System.out.println("Sending message \"" + textArea.getText() + "\" to " + usernameField.getText());
        getClient().sendMessageToServer(new Message(getClient().getName(), usernameField.getText(), textArea.getText()));
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
