package client.gui;

import client.data.Chat;
import client.data.Client;
import client.data.ClientSave;
import client.data.Message;
import javafx.scene.control.TextArea;

import java.io.*;

public class Controller {
    private Client client;
    public boolean logout = true;
    private String lastUser;

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


    public void receiveMessage(Message message) {
        for(Chat chat : getClient().getChats()) {
            if(chat.getUserName().equals(message.getFrom())) {
                System.out.println("Received: " + message);
                chat.getMessages().add(message);
            }
        }
    }

    public void connectAs(String username, String password) {
        client.setName(username);
        connect();
    }

    public void connect() {
        readClientSave(client);
        logout = false;
        client.connectToServer("0", 1337);
    }

    public void exit() {
        writeClientSave();
    }

    public void writeClientSave() {
        try {
            File file2 = new File("src/client/userdata/latestUser.txt");
            FileOutputStream fOut2 = new FileOutputStream(file2, false);
            ObjectOutputStream oOut2 = new ObjectOutputStream(fOut2);
            oOut2.writeObject(getClient().getName());
            oOut2.writeObject(logout);
            oOut2.close();
            fOut2.close();

            File file = new File("src/client/userdata/" + client.getName() + ".txt");
            FileOutputStream fOut = new FileOutputStream(file, false);
            ObjectOutputStream oOut = new ObjectOutputStream(fOut);
            oOut.writeObject(new ClientSave(getClient()));
            oOut.close();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readLoginData() {
        try {
            FileInputStream fIn2 = new FileInputStream("src/client/userdata/latestUser.txt");
            ObjectInputStream oIn2 = new ObjectInputStream(fIn2);
            lastUser = (String) oIn2.readObject();
            logout = (boolean) oIn2.readObject();
            oIn2.close();
            fIn2.close();
        } catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void readClientSave(Client client) {
        try {
            String name;
            if(logout || lastUser == null || lastUser.isEmpty()) name = client.getName();
            else name = lastUser;

            if(!name.isEmpty()) {
                FileInputStream fIn = new FileInputStream("src/client/userdata/" + name + ".txt");
                ObjectInputStream oIn = new ObjectInputStream(fIn);
                ClientSave clientSave = (ClientSave) oIn.readObject();
                clientSave.clientOpen(client);
                oIn.close();
                fIn.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
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
