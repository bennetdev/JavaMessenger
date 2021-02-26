package client.data;

import client.gui.AppView;
import client.gui.Main;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/*
Every Application contains ONE instance of Controller. The controller connected to the Client instance and serves as
an interface between the data supply and the GUI. This means the controller holds only very little information but
plays a key role in combining looks with functionality. The GUI classes do not modify any chat data. This is done
remotely through this class only.
Just like Client, Controller is an effectively static class but we wanted to keep the chance to change that, later.
 */
public class Controller {
    private Client client;

    // logout != !getClient().isConnected(), because logout signifies if the user pressed logout, not that you're logged out
    public boolean logout = true, clientOpened = false;
    private String lastUser, lastPassword;
    public static final AudioClip
            MESSAGE_POP = getAudio("messagePop.mp3"),
            ERROR = getAudio("error.mp3"),
            NEW_MESSAGE_1 = getAudio("newMessage1.mp3"),
            NEW_MESSAGE_2 = getAudio("newMessage2.mp3"),
            NEW_MESSAGE = NEW_MESSAGE_2;
    private Thread saveParallelThread;


    public Controller(Client client) {
        setClient(client);
        client.setController(this);
    }

    public void sendMessage(TextArea textArea, Chat chat) {
        if(!(textArea.getText().trim().isEmpty())) {
            Message message = new Message(getClient().getName(), chat.getUsername(), textArea.getText(), chat.getCipher().getEncryptionMethod());
            System.out.println("Sending message " + message);
            message.setEncryptionMethod(chat.getCipher().getEncryptionMethod());

            String text = message.getText();
            message.encrypt(chat.getCipher());
            getClient().sendMessageToServer(message);
            message.setText(text);
            chat.getMessages().add(message);
            MESSAGE_POP.play();

            textArea.setText("");
        }
    }


    public void receiveMessage(Message message) {
        for(Chat chat : getClient().getChats()) {
            if(chat.getUsername().equals(message.getFrom())) {
                System.out.println("Received " + message);
                message.decrypt(chat.getCipher());
                chat.getMessages().add(message);

                if(AppView.primaryStage.isFocused()) MESSAGE_POP.play();
                else NEW_MESSAGE.play();
                return;
            }
        }
        // If a fitting chat was found, receiveMessage would have returned. So now we can add a new chat and the message
        getClient().getChats().add(new Chat(message));
    }

    public String connectAs(String username, String password) {
        client.setName(username);
        client.setPassword(password);
        String val = connect();
        if(val != null) {
            client.setName(null);
            client.setPassword(null);
        }
        return val;
    }

    // Returns null if everything went well. Returns quick exception message when it couldn't connect
    public String connect() {
        if(client.getName() == null) client.setName(lastUser);
        if(client.getPassword() == null) client.setPassword(lastPassword);
        String val = client.connectToServer(Main.ADDRESS, 1337);
//        String val = client.connectToServer("0", 1337);
        if(val == null && !clientOpened) readClientSave(client);
        client.setConnected(val == null, val);
        return val;
    }

    public void exit() {
        AppView.primaryStage.close();
        writeClientSave();
        if(getSaveParallelThread() != null) {
            try {
                //Wait 1 minute. If saving takes more than a minute, then cancel.
                getSaveParallelThread().join(60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeClientSave() {
        setSaveParallelThread(new Thread( () -> {
            try {
                File file2 = new File(Main.USERDATA + "latestUser.txt");
                FileOutputStream fOut2 = new FileOutputStream(file2, false);
                ObjectOutputStream oOut2 = new ObjectOutputStream(fOut2);
                oOut2.writeObject(getClient().getName());
                oOut2.writeObject(getClient().getPassword());
                oOut2.writeObject(logout);
                oOut2.close();
                fOut2.close();

                File file = new File(Main.USERDATA + client.getName() + ".txt");
                FileOutputStream fOut = new FileOutputStream(file, false);
                ObjectOutputStream oOut = new ObjectOutputStream(fOut);
                oOut.writeObject(new ClientSave(getClient()));
                oOut.close();
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        getSaveParallelThread().start();

    }

    public void readLoginData() {
        try {
            FileInputStream fIn2 = new FileInputStream(Main.USERDATA + "latestUser.txt");
            ObjectInputStream oIn2 = new ObjectInputStream(fIn2);
            lastUser = (String) oIn2.readObject();
            lastPassword = (String) oIn2.readObject();
            logout = (boolean) oIn2.readObject();
            oIn2.close();
            fIn2.close();
        } catch(IOException | ClassNotFoundException e) {
            if(e instanceof FileNotFoundException) return;
            e.printStackTrace();
        }
    }

    public void readClientSave(Client client) {
        try {
            String name;

            if(logout || lastUser == null || lastUser.isEmpty()) {
                name = client.getName();
            }
            else {
                name = lastUser;
            }

            if(!name.isEmpty()) {
                FileInputStream fIn = new FileInputStream(Main.USERDATA + name + ".txt");
                ObjectInputStream oIn = new ObjectInputStream(fIn);
                ClientSave clientSave = (ClientSave) oIn.readObject();
                clientSave.clientOpen(client);
                clientOpened = true;
                oIn.close();
                fIn.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            if(e instanceof FileNotFoundException) return;
            e.printStackTrace();
        }
    }

    public void deleteSelectedChat(AppView appView, Chat temp) {
        ((VBox) AppView.openedChat.getChatHBox().getParent()).getChildren().remove(AppView.openedChat.getChatHBox());
        getClient().getChats().remove(AppView.openedChat);
        appView.openChat(null);
        AppView.openedChat = temp;
    }

    private static AudioClip getAudio(String fileName) {
        try {
            return new AudioClip(new URL(Main.RESOURCES + fileName).toExternalForm());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            //Just a funny backup I created XD
            return new AudioClip("https://archive.org/download/chant-what-005-jic/Chant%20What%20005.mp3");
        }
    }

    //Getters and Setters
    public Client getClient() {
        return client;
    }

    private void setClient(Client client) {
        this.client = client;
    }

    public Thread getSaveParallelThread() {
        return saveParallelThread;
    }

    public void setSaveParallelThread(Thread saveParallelThread) {
        this.saveParallelThread = saveParallelThread;
    }
}
