package Server;

import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientUser {
    private Socket client;
    private String name;
    private ObjectOutputStream writer;

    public ClientUser(Socket client, String name, ObjectOutputStream writer) {
        this.client = client;
        this.name = name;
        this.writer = writer;
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
