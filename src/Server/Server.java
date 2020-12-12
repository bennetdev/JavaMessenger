package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import Client.data.Message;

public class Server {
    private ServerSocket server;
    private ArrayList<ClientUser> users;

    public Server(int port) {
        try{
            server = new ServerSocket(port);
            server.setSoTimeout(100000000);
            users = new ArrayList<>();
        }
        catch (SocketException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void listen(){
        while (true){
            try {
                Socket client = server.accept();
                ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
                output.flush();
                ObjectInputStream input = new ObjectInputStream(client.getInputStream());
                String name = input.readUTF();
                System.out.println("accepted: " +name);

                users.add(new ClientUser(client, name, output));
                Thread thread = new Thread(new ClientHandler(this, client, input));
                thread.start();
            }
            catch (IOException e){
                e.printStackTrace();
                break;
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
    public void broadcast(String message){
        for(ClientUser user : users){
            System.out.println(user.getName());
            try {
                user.getWriter().writeUTF(message);
                user.getWriter().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void privateMessage(Message message){
        for(ClientUser user : users){
            if(user.getName().equalsIgnoreCase(message.getTo())){
                try {
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
}
