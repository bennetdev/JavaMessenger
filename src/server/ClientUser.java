package server;

import client.data.ConnectedInfo;
import client.data.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


/*
This class exists for every user that is currently connected to the server. It mainly consists of the ClientHandler
which is a functional interface providing the server with input messages as well holding the functionality to send and
receive data from clients.
 */
public class ClientUser {
    private final Socket client;
    private final ObjectOutputStream writer;
    private final String name;
    private final String password;

    public ClientUser(Socket client, String name, ObjectOutputStream writer, ObjectInputStream input, Server server, String password) {
        this.client = client;
        this.name = name;
        this.writer = writer;
        this.password = password;

        Thread clientHandler = new Thread(new ClientHandler(server, client, input));
        clientHandler.start();
    }

    public String getPassword() {
        return password;
    }

    private class ClientHandler implements Runnable {
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
                    System.out.println(message);
                    server.privateMessage(message);
                }
            } catch (IOException e) {
                System.out.println(name + " disconnected");
                server.getOnlineUsers().remove(ClientUser.this);
                server.broadcast(new ConnectedInfo(false, name));
                server.getOfflineUsers().add(new OfflineUser(ClientUser.this));
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



    public Socket getClient() {
        return client;
    }

    public String getName() {
        return name;
    }

    public ObjectOutputStream getWriter() {
        return writer;
    }

}
