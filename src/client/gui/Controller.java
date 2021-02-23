package client.gui;

import client.data.Chat;
import client.data.Client;
import client.data.ClientSave;
import client.data.Message;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.io.*;

public class Controller {
    private Client client;

    // logout != !getClient().isConnected(), because logout signifies if the user pressed logout, not that you're logged out
    public boolean logout = true;
    private String lastUser;
    private static final String USERDATA = Main.ROOT_URL + File.separator + "userdata" + File.separator ;

    public Controller(Client client) {
        File userData = new File(Main.ROOT_URL + File.separator + "userdata");
        userData.mkdir();
        setClient(client);
        client.setController(this);
    }

    public void sendMessage(TextArea textArea, Chat chat) {
        if(!(textArea.getText().trim().isEmpty())) {
            Message message = new Message(getClient().getName(), chat.getUserName(), textArea.getText());
            System.out.println("Sending message " + message);
            message.setEncryptionMethod(chat.getCipher().getEncryptionMethod());

            String text = message.getText();
            message.encrypt(chat.getCipher());
            getClient().sendMessageToServer(message);
            message.setText(text);
            chat.getMessages().add(message);

            textArea.setText("");
        }
    }


    public void receiveMessage(Message message) {
        for(Chat chat : getClient().getChats()) {
            if(chat.getUserName().equals(message.getFrom())) {
                System.out.println("Received " + message);
                message.decrypt(chat.getCipher());
                System.out.println("Decrypted " + message);
                System.out.println();
                chat.getMessages().add(message);
                return;
            }
        }
        // If a fitting chat was found, receiveMessage would have returned. So now we can add a new chat and the message
        getClient().getChats().add(new Chat(message));
    }

    public String connectAs(String username, String password) {
        client.setName(username);
        client.setPassword(password);
        String val = connect();
        if(val != null) {
            client.setName(null);
            client.setPassword(null);
        }
        return val;
    }

    // Returns null if everything went well. Returns quick exception message when it couldn't connect
    public String connect() {
        readClientSave(client);
        String val = client.connectToServer("0", 1337);
        client.setConnected(val == null, val);
        return val;
//        return client.connectToServer("mrwhite.ddnss.de", 1337);
    }

    public void exit() {
        writeClientSave();
    }

    public void writeClientSave() {
        try {
//            IntStream.range(0, 50).forEach(e -> client.getChats().get(0).getMessages().add(new Message("Bennet", "Tobias", "test123123123123123")));
            File file2 = new File(USERDATA + "latestUser.txt");
            FileOutputStream fOut2 = new FileOutputStream(file2, false);
            ObjectOutputStream oOut2 = new ObjectOutputStream(fOut2);
            oOut2.writeObject(getClient().getName());
            oOut2.writeObject(logout);
            oOut2.close();
            fOut2.close();

            File file = new File(USERDATA + client.getName() + ".txt");
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
            FileInputStream fIn2 = new FileInputStream(USERDATA + "latestUser.txt");
            ObjectInputStream oIn2 = new ObjectInputStream(fIn2);
            lastUser = (String) oIn2.readObject();
            logout = (boolean) oIn2.readObject();
            oIn2.close();
            fIn2.close();
        } catch(IOException | ClassNotFoundException e) {
            if(e instanceof FileNotFoundException) return;
            e.printStackTrace();
        }
    }

    public void readClientSave(Client client) {
        try {
            String name;
            if(logout || lastUser == null || lastUser.isEmpty()) name = client.getName();
            else name = lastUser;

            if(!name.isEmpty()) {
                FileInputStream fIn = new FileInputStream(USERDATA + name + ".txt");
                ObjectInputStream oIn = new ObjectInputStream(fIn);
                ClientSave clientSave = (ClientSave) oIn.readObject();
                clientSave.clientOpen(client, logout);
                oIn.close();
                fIn.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            if(e instanceof FileNotFoundException) return;
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

    public void deleteSelectedChat(AppView appView, Chat temp) {
        ((VBox) AppView.openedChat.getChatHBox().getParent()).getChildren().remove(AppView.openedChat.getChatHBox());
        getClient().getChats().remove(AppView.openedChat);
        appView.openChat(null);
        AppView.openedChat = temp;
    }
}
