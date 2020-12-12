package Client.gui;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import Client.data.Client;
import Client.data.Message;

public class Controller {
    public Client client;
    public TestView tView;

    public Controller(Client client){
        this.client = client;
        client.con = this;
    }

    public void sendMessage(TextArea textArea, TextField usernameField) {
        if(!(textArea.getText().isBlank() || !usernameField.getText().isBlank()))
        System.out.println("Sending message \"" + textArea.getText() + "\" to " + usernameField.getText());
        getClient().sendMessageToServer(new Message(getClient().getName(), usernameField.getText(), textArea.getText()));
    }

    public void displayTestMessage(Message message) {
        tView.lastMessageReceived.setText("Got \"" + message.getText() + "\"\n" +
                "from " + message.getFrom() + ".\n" +
                "Intended destination: " + message.getTo() + ".\n" +
                "Sent at " + message.getTimeSend());
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
