package client.data;

import client.data.cipher.Cipher;
import client.gui.Controller;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private StringProperty nameProperty = new SimpleStringProperty();
    private String password;
    private Cipher cipher;
    private Controller controller;
    private ObservableList<Chat> chats;

    public Client(String name){
        setCipher(new Cipher());
        setName(name);
        setChats(FXCollections.observableArrayList());
    }

    public Client(){
        setCipher(new Cipher());
        setChats(FXCollections.observableArrayList());
    }

    private void fillChatsForTesting() {
        for(int i = 10; i >= 1; i--) {
            getChats().add(new Chat("ExampleChat " + i));
        }

        if(getName().equals("Tobias")) {
            Chat bennetChat = new Chat("Bennet");

            bennetChat.getMessages().add(new Message("Bennet", "Tobias",
                    "Lieber Kai,\nich wollte nur mal bescheid sagen, dass du ein" +
                            "kleiner, dummer Hurensohn bist, von idem ich gehofft hätte, er wäre niemals geboren. Nur so am Rande." +
                            "\nLG\nBennet"));
            bennetChat.getMessages().get(0).setTimeSend(LocalDateTime.now().minusDays(1).minusMinutes(88));

            bennetChat.getMessages().add(new Message("Bennet", "Tobias",
                    "Allahu Akbar"));
            bennetChat.getMessages().get(1).setTimeSend(LocalDateTime.now().minusDays(1).minusMinutes(69));

            bennetChat.getMessages().add(new Message("Tobias", "Bennet",
                    "Digga was? Willst du mich ficken oder was, AMK?!!?!!!"));

            bennetChat.getMessages().add(new Message("Bennet", "Tobias",
                    "Ist schon ganzschön cool, oder nicht? :D"));

            getChats().addAll(new Chat("Kai"), bennetChat);
        }
        else if(getName().equals("Bennet")) {

            Chat tobiasChat = new Chat("Tobias");

            tobiasChat.getMessages().add(new Message("Bennet", "Tobias",
                    "Lieber Kai,\nich wollte nur mal bescheid sagen, dass du ein" +
                            "kleiner, dummer Hurensohn bist, von idem ich gehofft hätte, er wäre niemals geboren. Nur so am Rande." +
                            "\nLG\nBennet"));
            tobiasChat.getMessages().get(0).setTimeSend(LocalDateTime.now().minusDays(1).minusMinutes(88));

            tobiasChat.getMessages().add(new Message("Bennet", "Tobias",
                    "Allahu Akbar"));
            tobiasChat.getMessages().get(1).setTimeSend(LocalDateTime.now().minusDays(1).minusMinutes(69));

            tobiasChat.getMessages().add(new Message("Tobias", "Bennet",
                    "Digga was? Willst du mich ficken oder was, AMK?!!?!!!"));

            tobiasChat.getMessages().add(new Message("Bennet", "Tobias",
                    "Ist schon ganzschön cool, oder nicht? :D"));

            getChats().addAll(new Chat("Kai"), tobiasChat);
        }
    }

    // Initialize connection to server at address:port
    public boolean connectToServer(String address, int port){
        try {
//            fillChatsForTesting();

            setSocket(new Socket(address, port));
            setOutput(new ObjectOutputStream(getSocket().getOutputStream()));
            setInput(new ObjectInputStream(getSocket().getInputStream()));
            // Send name, password to server for identification
            sendTextToServer(getName());
            sendTextToServer(getPassword());
            // Start Listener Thread to receive Messages
            Thread t = new Thread(new Listener());
            t.start();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Listen to Messages from server
    public class Listener implements Runnable {

        @Override
        public void run() {
            boolean connected = true;
            while(connected){
                try {
                    // Read Input and cast to Message
                    Message message = (Message) getInput().readObject();
                    Platform.runLater(() -> getController().receiveMessage(message));
                } catch (IOException e) {
                    connected = false;
                    e.printStackTrace();
                    System.out.println("Server closed connection unexpectedly, exiting");
                    Platform.exit();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Send string to server
    private void sendTextToServer(String text){
        try {
            getOutput().writeUTF(text);
            getOutput().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Send Message-Object to Server
    public void sendMessageToServer(Message message){
        try {
            getOutput().writeObject(message);
            getOutput().flush();
        } catch (IOException e) {
            System.out.println(getOutput());
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return getName();
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ObjectOutputStream getOutput() {
        return output;
    }

    public void setOutput(ObjectOutputStream output) {
        this.output = output;
    }

    public ObjectInputStream getInput() {
        return input;
    }

    public void setInput(ObjectInputStream input) {
        this.input = input;
    }

    public String getName() {
        return nameProperty.getValue();
    }

    public void setName(String name) {
        nameProperty.setValue(name);
    }

    public StringProperty getNameProperty() {
        return nameProperty;
    }

    private Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public ObservableList<Chat> getChats() {
        return chats;
    }

    public void setChats(ObservableList<Chat> chats) {
        this.chats = chats;
    }

    public Cipher getCipher() {
        return cipher;
    }

    public void setCipher(Cipher cipher) {
        this.cipher = cipher;
    }
}
