package server;


import client.data.Message;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {
    private ServerSocket server;
    private final ArrayList<ClientUser> onlineUsers = new ArrayList();
    private ArrayList<OfflineUser> offlineUsers = new ArrayList();

    // Sorted by relevance and call order. If there are much more commands added, remove the less exact synonyms
    private static final String EXIT_SYNONYMS = "exit, stop, abort, cancel, close, quit, out, off, leave, die, finish, kill, end, halt, break, return, shut, terminate";
    private static final String INFO_SYNONYMS = "information, getusers();, online, offline, status, data, report, feedback";

    private Server(int port) {
        readData();
        initializeCommandListener();

        try{
            server = new ServerSocket(port);
            server.setSoTimeout(0);
        }
        catch (SocketException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void initializeCommandListener() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                saveData();
            }
        });
        Thread listener = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while(true) {
                String input = scanner.nextLine().toLowerCase();
                if(EXIT_SYNONYMS.contains(input)) {
                    System.out.println("Saving and exiting...");
                    System.exit(0);
                } else if(INFO_SYNONYMS.contains(input)) {
                    System.out.println("\nOnline:");
                    for(ClientUser user : getOnlineUsers()) System.out.println(user.getName());
                    System.out.println("\nOffline:");
                    for(ClientUser user : getOnlineUsers()) System.out.println(user.getName());
                }
            }
        });
        listener.start();
    }

    private void listenForLogins(){
        while(true) {
            try {
                Socket client = server.accept();
                ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
                output.flush();
                ObjectInputStream input = new ObjectInputStream(client.getInputStream());
                String name = input.readUTF();
                String password = input.readUTF();

                if(isOnline(name)) {
                    // Already online
                    writeUTFto(output, "Denied connection: user is already online");
                    client.close();
                    System.out.println("Denied: " + name + ", because user is already online");
                } else if(isOffline(name)) {
                    // Known user
                    OfflineUser offlineUser = getOfflineUser(name);

                    boolean acceptTempInternal = true;
                    if(offlineUser.getPassword() == null || offlineUser.getPassword().equals("")) {
                        // Password wasn't set yet

                        String oldPassword = offlineUser.getPassword();
                        offlineUser.setPassword(password);
                        System.out.println("Changed password of " + name +
                                " from \"" + oldPassword + "\" to \"" + password + "\"");

                    } else if(!offlineUser.getPassword().equals(password)) {
                        // Password is wrong

                        writeUTFto(output, "Denied connection: password is incorrect");
                        client.close();
                        acceptTempInternal = false;
                        System.out.println("Denied: " + name + " with password " + password + ", because password is wrong");
                    }
                    if(acceptTempInternal) {
                        // Accept known user

                        getOfflineUsers().remove(offlineUser);
                        ClientUser user = new ClientUser(client, name, output, input, this, password);
                        getOnlineUsers().add(user);
                        System.out.println("Accepted: " + name + " with password " + password);
                        writeUTFto(output, "Accepted connection");

                        for (Message m : offlineUser.getUndeliveredMessages()) privateMessage(m, user);
                    }
                } else {
                    // Accept new user
                    getOnlineUsers().add(new ClientUser(client, name, output, input, this, password));
                    System.out.println("Accepted new user: " + name + " with password " + password);
                    writeUTFto(output, "Accepted connection");
                }
            }
            catch (IOException e){
                e.printStackTrace();
                System.out.println("unexpected disconnect");
            }
        }
    }

    private void writeUTFto(ObjectOutputStream outputStream, String text) throws IOException {
        outputStream.writeUTF(text);
        outputStream.flush();
    }

    private void broadcast(String message){
        for(ClientUser user : getOnlineUsers()){
            System.out.println(user.getName());
            try {
                user.getWriter().writeUTF(message);
                user.getWriter().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //returns true if the message is delivered or will be delivered as soon as a user logs in
    public void privateMessage(Message message) {
        String username = message.getTo();
        if(isOnline(username)) {
            ClientUser user = getOnlineUser(username);
            try {
                user.getWriter().writeObject(message);
                user.getWriter().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(isOffline(username)) {
            getOfflineUser(username).getUndeliveredMessages().add(message);
        } else {
            //There is no user with that username
            getOfflineUsers().add(new OfflineUser(message));
        }
    }


    private void privateMessage(Message message, ClientUser toUser){
        String username = message.getTo();
        try {
            toUser.getWriter().writeObject(message);
            toUser.getWriter().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        try {
            Server server = new Server(1337);
            server.listenForLogins();
        } catch (Exception e) {
            System.out.println("uncaught exception");
            e.printStackTrace();
        }
    }


    private ClientUser getOnlineUser(String username) {
        for (ClientUser user : getOnlineUsers()) {
            if (user.getName().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }
    private OfflineUser getOfflineUser(String username) {
        for (OfflineUser user : getOfflineUsers()) {
            if (user.getName().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }

    private boolean isOnline(String username) {
        return getOnlineUser(username) != null;
    }
    private boolean isOffline(String username) {
        return getOfflineUser(username) != null;
    }

    private boolean exists(String username) {
        return isOnline(username) || isOffline(username);
    }

    private static final String SERVERDATA = System.getProperty("user.dir") + File.separator + "serverData" + File.separator ;
    private void saveData() {
        try {
            for(ClientUser c : getOnlineUsers()) getOfflineUsers().add(new OfflineUser(c));

            File serverData = new File(System.getProperty("user.dir") + File.separator + "serverData");
            serverData.mkdir();

            File file = new File(SERVERDATA + "serverData.txt");
            FileOutputStream fOut = new FileOutputStream(file, false);
            ObjectOutputStream oOut = new ObjectOutputStream(fOut);
            oOut.writeObject(getOfflineUsers());
            oOut.close();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Couldn't save data");
        }
    }

    private void readData() {
        try {
            FileInputStream fIn = new FileInputStream(SERVERDATA + "serverData.txt");
            ObjectInputStream oIn = new ObjectInputStream(fIn);
            setOfflineUsers((ArrayList<OfflineUser>) oIn.readObject());
            oIn.close();
            fIn.close();
        } catch (Exception e) {
            System.out.println("No serverData.txt found");
        }
    }

    public ArrayList<ClientUser> getOnlineUsers() {
        return onlineUsers;
    }

    public ArrayList<OfflineUser> getOfflineUsers() {
        return offlineUsers;
    }

    private void setOfflineUsers(ArrayList<OfflineUser> offlineUsers) {
        this.offlineUsers = offlineUsers;
    }
}
