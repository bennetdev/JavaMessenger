package client.gui;

import client.data.Client;
import client.data.Controller;
import client.gui.customComponents.borderless.CustomStage;
import javafx.application.Application;
import javafx.stage.Stage;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/*
The Main class is only responsible for providing utility constants like RESOURCES (effectively final) as well as the
entry point for the client. It also prints any errors it catches but they are not expected to be thrown anyways.
 */
public class Main extends Application {

    public static ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    private Controller controller;
    public static URI RESOURCES;
    public static String USERDATA;

    // "mrwhite.ddnss.de" or "0"
    public static String ADDRESS;


    public static void main(String[] args) {
        try {
            ADDRESS = args[0];
        } catch (Exception ignored) {
            ADDRESS = "0";
        }
        System.out.println(ADDRESS);
        try {
            launch(args);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            RESOURCES = this.getClass().getResource("resources/").toURI();
            USERDATA = this.getClass().getResource("userdata/").toURI().getPath();

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
