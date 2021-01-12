package client.gui.customComponents;

import client.gui.AppView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginView extends VBox {

    private final Button loginButton;
    private final TextField passwordTextField;
    private final TextField usernameTextField;

    public LoginView() {
        setPrefSize(275 * 1.5, 196 * 1.5);
        setSpacing(10);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(30));
//        Image mailImage = new Image(String.valueOf(getClass().getResource("/client/gui/resources/mail.png")),
//                0, 0, false,true);
//        loginRoot.setBackground(new Background(new BackgroundImage(mailImage, BackgroundRepeat.REPEAT,
//                BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));
        setStyle( "-fx-background-image: url(\"client/gui/resources/mail.png\") no-repeat center center fixed;" +
                "-fx-background-size: cover;");

        final Label loginTitle = new Label("Hermes Messenger");
        getChildren().add(loginTitle);

        double maxWidth = 200;

        usernameTextField = (TextField) AppView.makeQuickTextControl(new TextField());
        usernameTextField.setMaxWidth(maxWidth);
        usernameTextField.setPromptText("Username");
        getChildren().add(usernameTextField);

        passwordTextField = (TextField) AppView.makeQuickTextControl(new TextField());
        passwordTextField.setMaxWidth(maxWidth);
        passwordTextField.setPromptText("Password");
        getChildren().add(passwordTextField);

        loginButton = new Button("Login");
        loginButton.setDefaultButton(true);
        getChildren().add(loginButton);
    }

    public Button getLoginButton() {
        return loginButton;
    }

    public String getUsername() {
        return usernameTextField.getText();
    }

    public String getPassword() {
        return passwordTextField.getText();
    }
}
