package client.gui;

import client.data.Controller;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/*
This small class implements less the functionality, more the looks and layout of the Login Window. It is used only in
AppView. Maybe I'll convert this to a function or something later within AppView because it ended up being so small.
 */
public class LoginView extends VBox {

    private final Button loginButton;
    private final PasswordField passwordTextField;
    private final TextField usernameTextField;
    private Label errorLabel;

    public LoginView() {
        setPrefSize(275 * 1.5, 196 * 1.5);
        setSpacing(10);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(30));

        setStyle( "-fx-background-image: url(\"client/gui/resources/mail.png\") no-repeat center center fixed;" +
                "-fx-background-size: cover;");

        final Label loginTitle = new Label("Hermes Messenger");
        getChildren().add(loginTitle);

        double maxWidth = 200;

        usernameTextField = (TextField) AppView.makeQuickTextControl(new TextField());
        usernameTextField.setMaxWidth(maxWidth);
        usernameTextField.setPromptText("Username");
        getChildren().add(usernameTextField);

        passwordTextField = (PasswordField) AppView.makeQuickTextControl(new PasswordField());
        passwordTextField.setMaxWidth(maxWidth);
        passwordTextField.setPromptText("Password");
        getChildren().add(passwordTextField);

        loginButton = new Button("Login");
        loginButton.setGraphic(new ImageView(Main.RESOURCES + "login.png"));
        loginButton.setTooltip(new Tooltip("If there is no user with that username, you will automatically sign up.\nChoose your password wisely!"));
        loginButton.setDefaultButton(true);
        getChildren().add(loginButton);
    }

    public void showError(String errorMessage) {
        Controller.ERROR.play();
        if(errorLabel == null) {
            errorLabel = new Label();
            errorLabel.setTextAlignment(TextAlignment.CENTER);
            errorLabel.setStyle("-fx-text-fill: rgb(255, 100, 100)");
            getChildren().add(0, errorLabel);
        }
        errorLabel.setText(errorMessage);
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
