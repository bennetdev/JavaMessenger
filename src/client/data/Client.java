package client.data;

import client.gui.Controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Scanner scanner;
    private String name;
    public Controller con;

    public Client(String name){
        this.name = name;
        scanner = new Scanner(System.in);
        //Commented out for GUI testing
//        boolean connected = connectToServer("localhost", 1337);
//        System.out.println(connected);
    }


    //noGUI
    public static void main(String[] args) {
        Client client = new Client("Bennet");
        boolean connected = client.connectToServer("localhost", 1337);
        System.out.println(connected);
        while (true){
            System.out.println("To: ");
            String to = client.getScanner().nextLine();
            System.out.println("Text: ");
            String input = client.getScanner().nextLine();
            client.sendMessageToServer(new Message("Bennet", to, input));
        }

    }

    // Initialize connection to server at address:port
    private boolean connectToServer(String address, int port){
        try {
            setSocket(new Socket(address, port));
            setOutput(new ObjectOutputStream(socket.getOutputStream()));
            setInput(new ObjectInputStream(socket.getInputStream()));
            // Send name to server for identification
            sendTextToServer(name);
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
                    con.displayTestMessage(message);
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
        return name;
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
}
