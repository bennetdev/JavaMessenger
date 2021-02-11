package client.gui;

import client.data.Client;
import client.gui.customComponents.borderless.CustomStage;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Main extends Application {

    public static ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    private Controller controller;
    public static final String ROOT_URL = System.getProperty("user.dir");

    public static void main(String[] args) {
        try {
            launch(args);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Client client = new Client();
            controller = new Controller(client);
            new AppView(new CustomStage(), controller, client);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop(){
        controller.exit();
        System.exit(0);
    }
}
