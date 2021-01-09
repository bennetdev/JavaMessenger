package client.gui;

import client.data.Chat;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import client.data.Client;
import client.data.Message;

public class Controller {
    private Client client;
    public TestView tView;

    public Controller(Client client){
        this.setClient(client);
        client.setController(this);
    }

    public void sendMessage(TextArea textArea, String receiverUsername, Chat chat) {
        if(!(textArea.getText().trim().isEmpty() || receiverUsername.trim().isEmpty())) {
            System.out.println("Sending message \"" + textArea.getText() + "\" to " + receiverUsername);
            Message message = new Message(getClient().getName(), receiverUsername, textArea.getText());
            getClient().sendMessageToServer(message);
            chat.getMessages().add(message);
            textArea.setText("");
        }
    }

    /*
    For Testing purposes
     */
    public void sendMessage(TextArea textArea, TextField usernameField) {
        if(!(textArea.getText().trim().isEmpty() || usernameField.getText().trim().isEmpty())) {
            System.out.println("Sending message \"" + textArea.getText() + "\" to " + usernameField.getText());
            getClient().sendMessageToServer(new Message(getClient().getName(), usernameField.getText(), textArea.getText()));
        }
    }

    public void receiveMessage(Message message) {
        for(Chat chat : getClient().getChats()) {
            if(chat.getUserName().equals(message.getFrom())) {
                System.out.println("Received: " + message);
                chat.getMessages().add(message);
            }
        }
    }

    //Getters and Setters
    public Client getClient() {
        return client;
    }

    private void setClient(Client client) {
        this.client = client;
    }
}
