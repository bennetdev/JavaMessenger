package data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket client;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Scanner scanner;
    private String name;

    public Client(String name){
        this.name = name;
        scanner = new Scanner(System.in);
        boolean connected = connectToServer("localhost", 1337);
        System.out.println(connected);
    }



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

    private boolean connectToServer(String address, int port){
        try {
            setClient(new Socket(address, port));
            setOutput(new ObjectOutputStream(client.getOutputStream()));
            setInput(new ObjectInputStream(client.getInputStream()));
            sendTextToServer(name);
            Thread t = new Thread(new Listener());
            t.start();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public class Listener implements Runnable{

        @Override
        public void run() {
            while(true){
                try {
                    Message message = (Message) getInput().readObject();
                    System.out.println("Recieved by " + message.getFrom() + ": " + message.getText());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendTextToServer(String text){
        try {
            getOutput().writeUTF(text);
            getOutput().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageToServer(Message message){
        try {
            getOutput().writeObject(message);
            getOutput().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getClient() {
        return client;
    }

    public void setClient(Socket client) {
        this.client = client;
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
