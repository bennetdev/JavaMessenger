package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import client.data.Message;

public class ClientHandler implements Runnable{
    private Socket client;
    private ObjectInputStream input;
    private Server server;

    public ClientHandler(Server server, Socket client, ObjectInputStream input){
        setServer(server);
        setClient(client);
        setInput(input);

    }

    @Override
    public void run() {
        Message message;
        try {
            while(true){
                message = (Message) getInput().readObject();
                System.out.println(message.getText());
                server.privateMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Socket getClient() {
        return client;
    }

    public void setClient(Socket client) {
        this.client = client;
    }

    public ObjectInputStream getInput() {
        return input;
    }

    public void setInput(ObjectInputStream input) {
        this.input = input;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }
}
