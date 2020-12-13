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
        new TestView(primaryStage, new Controller(new Client("Bennet")));
//        new AppView(primaryStage, new Controller(new Client("Allah")));
    }

    @Override
    public void stop(){
        System.out.println("Stage is closing");
        System.exit(0);
    }
}
