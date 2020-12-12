package Client.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import Client.data.Client;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        //Builds entire GUI
        new TestView(primaryStage, new Controller(new Client("Allah")));
    }
}
