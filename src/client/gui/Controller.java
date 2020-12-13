package client.gui;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import client.data.Client;
import client.data.Message;

public class Controller {
    public Client client;
    public TestView tView;

    public Controller(Client client){
        this.client = client;
        client.con = this;
    }

    public void sendMessage(TextArea textArea, TextField usernameField) {
        if(!(textArea.getText().isBlank() || usernameField.getText().isBlank())) {
            System.out.println("Sending message \"" + textArea.getText() + "\" to " + usernameField.getText());
            Message message = new Message(getClient().getName(), usernameField.getText(), textArea.getText(), Message.EncryptionMethod.CAESAR);
            message.encrypt("3", getClient().getCipher());
            getClient().sendMessageToServer(message);
        }
    }

    public void displayTestMessage(Message message) {
        message.decrypt("3", getClient().getCipher());
        tView.lastMessageReceived.setText("Got \"" + message.getText() + "\"\n" +
                "from " + message.getFrom() + ".\n" +
                "Intended destination: " + message.getTo() + ".\n" +
                "Sent at " + message.getTimeSend() + ".\n" +
                "Encrypted with " + message.getEncryptionMethod().name() + ".\n");
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
