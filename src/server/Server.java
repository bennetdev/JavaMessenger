package server;

import client.data.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class Server {
    private ServerSocket server;
    private ArrayList<ClientUser> users;

    public Server(int port) {
        try{
            server = new ServerSocket(port);
            server.setSoTimeout(60 * 1000);
            setUsers(new ArrayList<>());
        }
        catch (SocketException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    // Accept incoming clients connecting
    public void listen(){
        while (true){
            try {
                Socket client = server.accept();
                // Create inputstream and outputstream for client
                ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
                output.flush();
                ObjectInputStream input = new ObjectInputStream(client.getInputStream());
                String name = input.readUTF();
                System.out.println("accepted: " + name);

                // Save t o user ArrayList
                getUsers().add(new ClientUser(client, name, output, input, this));
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void sendToClient(Socket client, String message){
        try {
            System.out.println("Message: " + message);
            PrintWriter output = new PrintWriter(client.getOutputStream());
            output.println(message + " was recieved");
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Send string to all current users
    public void broadcast(String message){
        for(ClientUser user : getUsers()){
            System.out.println(user.getName());
            try {
                user.getWriter().writeUTF(message);
                user.getWriter().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Send Message-Object to message.getTo()
    public void privateMessage(Message message){
        // Iterate over all current users
        for(ClientUser user : getUsers()){
            // if the current user is the recipient of the message
            if(user.getName().equalsIgnoreCase(message.getTo())){
                try {
                    // send message to user
                    user.getWriter().writeObject(message);
                    user.getWriter().flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }


    public static void main(String[] args) {
        Server server = new Server(1337);
        server.listen();
    }

    public ArrayList<ClientUser> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<ClientUser> users) {
        this.users = users;
    }
}
