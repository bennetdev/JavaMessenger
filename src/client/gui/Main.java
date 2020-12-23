package client.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import client.data.Client;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        //Builds entire GUI
//        new TestView(primaryStage, new Controller(new Client("Allah")));
        Client client = new Client("Allah");
        new AppView(primaryStage, new Controller(client), client);
    }

    @Override
    public void stop(){
        System.exit(0);
    }
}
