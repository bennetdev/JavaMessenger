package client.data;

import client.data.cipher.Cipher;
import client.gui.Controller;
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
    private Scanner scanner;
    private String name;
    private Cipher cipher;
    private Controller controller;
    private ObservableList<Chat> chats;

    public Client(String name){
        setCipher(new Cipher());
        setName(name);
        setChats(FXCollections.observableArrayList());
        //Testing
        Chat bennetChat = new Chat("Bennet");

        bennetChat.getMessages().add(new Message("Bennet", "Allah",
                "Lieber Kai,\nich wollte nur mal bescheid sagen, dass du ein" +
                "kleiner, dummer Hurensohn bist, von idem ich gehofft hätte, er wäre niemals geboren. Nur so am Rande." +
                "\nLG\nBennet"));
        bennetChat.getMessages().get(0).setTimeSend(LocalDateTime.now().minusDays(1).minusMinutes(88));

        bennetChat.getMessages().add(new Message("Bennet", "Allah",
                "Allahu Akbar"));
        bennetChat.getMessages().get(1).setTimeSend(LocalDateTime.now().minusDays(1).minusMinutes(69));

        bennetChat.getMessages().add(new Message("Allah", "Bennet",
                "Digga was? Willst du mich ficken oder was, AMK?!!?!!!"));

        bennetChat.getMessages().add(new Message("Bennet", "Allah",
                "Ist schon ganzschön cool, oder nicht? :D"));

        getChats().addAll(bennetChat, new Chat("Tobias"), new Chat("Kai"));

        for(int i = 1; i <= 30; i++) {
            getChats().add(new Chat("ExampleChat " + i));
        }

        connectToServer("0", 1337);
    }

    // Initialize connection to server at address:port
    private boolean connectToServer(String address, int port){
        try {
            setSocket(new Socket(address, port));
            setOutput(new ObjectOutputStream(getSocket().getOutputStream()));
            setInput(new ObjectInputStream(getSocket().getInputStream()));
            // Send name to server for identification
            sendTextToServer(getName());
            // Start Listener Thread to receive Messages
            Thread t = new Thread(new Listener());
            t.start();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Listen to Messages from server
    public class Listener implements Runnable{

        @Override
        public void run() {
            while(true){
                try {
                    // Read Input and cast to Message
                    Message message = (Message) getInput().readObject();
                    getController().displayTestMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
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
            System.out.println(getOutput());
            getOutput().writeObject(message);
            getOutput().flush();
        } catch (IOException e) {
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

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Scanner getScanner() {
        return scanner;
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

    private void setChats(ObservableList<Chat> chats) {
        this.chats = chats;
    }

    public Cipher getCipher() {
        return cipher;
    }

    public void setCipher(Cipher cipher) {
        this.cipher = cipher;
    }
}
