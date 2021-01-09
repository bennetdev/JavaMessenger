package client.gui;

import client.data.Client;
import client.gui.customComponents.borderless.CustomStage;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Main extends Application {

    public static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            //Builds entire GUI
//            new TestView(primaryStage, new Controller(new Client("Allah")));
            Client client = new Client("Bennet");
            new AppView(new CustomStage(), new Controller(client), client);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop(){
        System.exit(0);
    }
}
