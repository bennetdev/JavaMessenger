package server;

import client.data.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientUser {
    private Socket client;
    private Thread clientHandler;
    private String name;
    private ObjectOutputStream writer;

    public ClientUser(Socket client, String name, ObjectOutputStream writer, ObjectInputStream input, Server server) {
        this.client = client;
        this.name = name;
        this.writer = writer;

        clientHandler = new Thread(new ClientHandler(server, client, input));
        clientHandler.start();
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
                server.getUsers().remove(ClientUser.this);
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

    public void setClient(Socket client) {
        this.client = client;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ObjectOutputStream getWriter() {
        return writer;
    }

    public void setWriter(ObjectOutputStream writer) {
        this.writer = writer;
    }
}
