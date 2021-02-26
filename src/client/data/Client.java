package client.data;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;

/*
Every Application contains ONE instance of Client. The client is directly connected to the Server via
a Socket and InputStream and manages connections, input and output. It also holds the name and password of the user.
Because every instance of an Application holds ONE instance of Clien, technically every method could be made into
a function and every instance variable converted into a class variable. We did not do this to uphold the possibility
of managing more than one User per application, for example if you have multiple accounts or are a couple who doesn't
care if one person reads what the other writes.
 */
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
    private ObservableList<String> onlineUsers = FXCollections.observableArrayList();

    public Client(String name){
        setName(name);
        setChats(FXCollections.observableArrayList());
    }

    public Client(){
        setChats(FXCollections.observableArrayList());
    }

    /*
     Initialize connection to server at address:port
     Returns null if everything went well. Returns quick exception message when it couldn't connect
     */
    public String connectToServer(String address, int port) {
        try {
            System.out.println("connect");
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(address, port), 3 * 1000);
            setSocket(socket);
            System.out.println("socket pass");
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
                setOnlineUsers((ArrayList<String>) getInput().readObject());
                Thread t = new Thread(new MessageListener());
                t.start();
                return null;
            }
        } catch (ConnectException | SocketTimeoutException e) {
            e.printStackTrace();
            String message = "Can't reach server " + address + ":" + port + ". \nCheck the address and see if it's working\nfor other users.";
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
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return "Server and client are out of sync.";
        }
    }

    public String getLatestServerResponse() {
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
                    Object input = getInput().readObject();
                    if(input instanceof Message) {
                        Platform.runLater(() -> getController().receiveMessage((Message) input));
                    } else if(input instanceof ConnectedInfo) {
                        ConnectedInfo info = (ConnectedInfo) input;
                        applyInfo(info);
                    } else {
                        // To throw an exception
                        Message message = (Message) getInput().readObject();
                        if(message != null) Platform.runLater(() -> getController().receiveMessage(message));
                    }
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

    //Called only once and before chats are initialized
    private void setOnlineUsers(ArrayList<String> onlineUsers) {
        this.onlineUsers = FXCollections.observableList(onlineUsers);
        this.onlineUsers.addListener((ListChangeListener<? super String>) change -> {
            change.next();
            for(Chat chat : getChats()) {
                chat.setOnline(this.onlineUsers.contains(chat.getUsername()) || change.getAddedSubList().contains(chat.getUsername()));
            }
        });
        chats.addListener((ListChangeListener<? super Chat>) change -> {
            change.next();
            for(Chat chat : change.getAddedSubList()) {
                chat.setOnline(this.onlineUsers.contains(chat.getUsername()));
            }
        });
    }

    private void applyInfo(ConnectedInfo info) {
        if(info.isConnected()) onlineUsers.add(info.getUsername());
        else onlineUsers.remove(info.getUsername());
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
