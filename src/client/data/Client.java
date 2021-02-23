package client.data;

import client.gui.Controller;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private SimpleStringProperty nameProperty = new SimpleStringProperty();
    private String password;
    private Controller controller;
    private ObservableList<Chat> chats;
    private SimpleBooleanProperty connected = new SimpleBooleanProperty();
    private String latestConnectErrorMessage;

    public Client(String name){
        setName(name);
        setChats(FXCollections.observableArrayList());
    }

    public Client(){
        setChats(FXCollections.observableArrayList());
    }

    private void fillChatsForTesting() {
        for(int i = 20; i >= 1; i--) {
            getChats().add(new Chat("ExampleChat " + i));
        }
    }

    /*
     Initialize connection to server at address:port
     Returns null if everything went well. Returns quick exception message when it couldn't connect
     */
    public String connectToServer(String address, int port) {
        try {
            setSocket(new Socket(address, port));
            setOutput(new ObjectOutputStream(getSocket().getOutputStream()));
            setInput(new ObjectInputStream(getSocket().getInputStream()));

            // Send name, password to server for identification
            sendTextToServer(getName());
            sendTextToServer(getPassword());
            String serverConnectionResponse = getInput().readUTF();
            if(serverConnectionResponse.contains("Denied")) {
                return serverConnectionResponse;
            } else {
                // Start Listener Thread to receive Messages
                Thread t = new Thread(new MessageListener());
                t.start();
                return null;
            }
        } catch (ConnectException e) {
            e.printStackTrace();
            String message = "Can't reach server " + address + ":" + port + ". Is it offline?";
            setConnected(false, message);
            return message;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            String message = "Can't reach server " + address + ":" + port + ", unknown IP";
            setConnected(false, message);
            return message;
        } catch (IOException e) {
            e.printStackTrace();
            String message = "Oops, something went wrong! Look for the stackTrace";
            setConnected(false, message);
            return message;
        }
    }

    public String getLatestConnectErrorMessage() {
        return latestConnectErrorMessage;
    }

    public void setLatestConnectErrorMessage(String latestConnectErrorMessage) {
        this.latestConnectErrorMessage = latestConnectErrorMessage;
    }


    // Listen to Messages from server
    public class MessageListener implements Runnable {

        @Override
        public void run() {
            setConnected(true, null);
            while(isConnected()) {
                try {
                    // Read Input and cast to Message
                    Message message = (Message) getInput().readObject();
                    Platform.runLater(() -> getController().receiveMessage(message));
                } catch (EOFException e) {
                    e.printStackTrace();
                    setConnected(false, "Wrong login data, logout and retry");
                    controller.logout = true;
                    return;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                    setConnected(false, "Server closed connection unexpectedly, now offline");
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
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return getName() + ", connected?: " + isConnected();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isConnected() {
        return connected.get();
    }

    public void setConnected(boolean connected, String message) {
        setLatestConnectErrorMessage(connected ? null : message);
        if(connected) controller.logout = false;
        this.connected.set(connected);
    }

    public SimpleBooleanProperty getConnectedProperty() {
        return connected;
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
}
