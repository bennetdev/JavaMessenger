package client.gui.customComponents.borderless;

import client.data.Message;
import client.data.cipher.Cipher;
import client.gui.AppView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;

public class EncryptionSettingsStage {

    private final Scene mono;
    private final Scene poly;
    private final Scene rsa;
    private final Stage popup;

    public EncryptionSettingsStage() {
        popup = new Stage(StageStyle.TRANSPARENT);
        popup.setOnShowing(e -> {
            popup.setX(MouseInfo.getPointerInfo().getLocation().getX());
            popup.setY(MouseInfo.getPointerInfo().getLocation().getY());
        });

        popup.focusedProperty().addListener(e -> {
            if(!popup.isFocused()) {
                popup.close();
            }
        });

        mono = getMonoScene();
        poly = getPolyScene();
        rsa = getRsaScene();
    }

    public void open(Message.EncryptionMethod encryptionMethod) {
        if(encryptionMethod == Message.EncryptionMethod.CAESAR) {
            popup.setScene(mono);
        } else if(encryptionMethod == Message.EncryptionMethod.VIGENERE) {
            popup.setScene(poly);
        } else if(encryptionMethod == Message.EncryptionMethod.RSA) {
            popup.setScene(rsa);
        } else {
            return;
        }
        popup.show();
    }

    private Scene getMonoScene() {
        VBox root = new VBox();
        root.setBackground(Background.EMPTY);
        root.setPadding(new Insets(5));
        root.setSpacing(5);

        HBox okCancel = new HBox();
        okCancel.setPadding(new Insets(0, 5, 5, 5));
        okCancel.setSpacing(5);
        okCancel.setAlignment(Pos.BOTTOM_CENTER);

        Button ok = new Button("Ok");
        ok.setDefaultButton(true);
        ok.setDisable(true);
        okCancel.getChildren().add(ok);

        Button cancel = new Button("Cancel");
        cancel.setOnAction(e -> popup.close());
        okCancel.getChildren().add(cancel);

        TextField keyTextField = new TextField();
        keyTextField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
        keyTextField.setPrefWidth(200);
        keyTextField.setPromptText("Key (Integer, same as chat partner)");
        keyTextField.textProperty().addListener(e -> {
            if(isInteger(keyTextField.getText())) {
                ok.setDisable(false);
                AppView.goodOrBadTextField(keyTextField, true);
            } else {
                ok.setDisable(true);
                AppView.goodOrBadTextField(keyTextField, false);
            }
        });
        root.getChildren().add(keyTextField);
        root.getChildren().add(okCancel);

        Scene popupScene = new Scene(root);
        popupScene.setFill(Color.rgb(127, 127, 127, 0.5));

        return popupScene;
    }


    private Scene getPolyScene() {
        VBox root = new VBox();
        root.setBackground(Background.EMPTY);
        root.setPadding(new Insets(5));
        root.setSpacing(5);

        HBox okCancel = new HBox();
        okCancel.setPadding(new Insets(0, 5, 5, 5));
        okCancel.setSpacing(5);
        okCancel.setAlignment(Pos.BOTTOM_CENTER);

        Button ok = new Button("Ok");
        ok.setDefaultButton(true);
        ok.setDisable(true);
        okCancel.getChildren().add(ok);

        Button cancel = new Button("Cancel");
        cancel.setOnAction(e -> popup.close());
        okCancel.getChildren().add(cancel);

        TextField keyTextField = new TextField();
        keyTextField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
        keyTextField.setPrefWidth(200);
        keyTextField.setPromptText("Key (String, same as chat partner)");
        root.getChildren().add(keyTextField);

        root.getChildren().add(okCancel);

        Scene popupScene = new Scene(root);
        popupScene.setFill(Color.rgb(127, 127, 127, 0.5));

        return popupScene;
    }


    @SuppressWarnings("CodeBlock2Expr")
    private Scene getRsaScene() {
        VBox root = new VBox();
        root.setBackground(Background.EMPTY);
        root.setPadding(new Insets(5));
        root.setSpacing(5);

        HBox okCancel = new HBox();
        okCancel.setPadding(new Insets(0, 5, 5, 5));
        okCancel.setSpacing(5);
        okCancel.setAlignment(Pos.BOTTOM_CENTER);

        Button ok = new Button("Ok");
        ok.setDefaultButton(true);
        ok.setDisable(true);
        okCancel.getChildren().add(ok);

        Button cancel = new Button("Cancel");
        cancel.setOnAction(e -> popup.close());
        okCancel.getChildren().add(cancel);

        HBox pqBox = new HBox();
        pqBox.setSpacing(5);
        root.getChildren().add(pqBox);

        TextField pTextField = new TextField();
        TextField qTextField = new TextField();
        TextField nTextField = new TextField();
        TextField mTextField = new TextField();
        TextField eTextField = new TextField();
        TextField dTextField = new TextField();

        pTextField.textProperty().addListener(e -> {
            ok.setDisable(checkRSAEntryValidity(pTextField, qTextField, eTextField, nTextField, mTextField, dTextField));
        });
        qTextField.textProperty().addListener(e -> {
            ok.setDisable(checkRSAEntryValidity(pTextField, qTextField, eTextField, nTextField, mTextField, dTextField));
        });
        eTextField.textProperty().addListener(e -> {
            ok.setDisable(checkRSAEntryValidity(pTextField, qTextField, eTextField, nTextField, mTextField, dTextField));
        });

        pTextField.setPrefWidth(100);
        pTextField.setPromptText("p: Prime Integer");
        pTextField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
        pqBox.getChildren().add(pTextField);

        qTextField.setPrefWidth(100);
        qTextField.setPromptText("q: Prime Integer");
        qTextField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
        pqBox.getChildren().add(qTextField);

        nTextField.setPromptText("n = p*q: n > 256");
        nTextField.setEditable(false);
        nTextField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
        root.getChildren().add(nTextField);

        mTextField.setPromptText("m: (p-1)*(q-1)");
        mTextField.setEditable(false);
        mTextField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
        root.getChildren().add(mTextField);

        eTextField.setTooltip(new Tooltip("Prime, not part of prime factorization of m, less than m"));
        eTextField.setPromptText("e: Prime Integer, read tooltip");
        eTextField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
        root.getChildren().add(eTextField);

        dTextField.setPromptText("d: extended euclidean algorithm");
        dTextField.setEditable(false);
        dTextField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
        root.getChildren().add(dTextField);

        root.getChildren().add(okCancel);

        Scene popupScene = new Scene(root);
        popupScene.setFill(Color.rgb(127, 127, 127, 0.5));

        return popupScene;
    }

    private static boolean checkRSAEntryValidity(TextField pTf, TextField qTf, TextField eTf, TextField nTf, TextField mTf, TextField dTf) {
        int p = -1, q = -1, e;
        boolean pg = false, qg = false, eg = false;
        if (isInteger(pTf.getText())) {
            p = Integer.parseInt(pTf.getText());
            if (Cipher.isPrimeNumber(p)) pg = true;
        }

        if (isInteger(qTf.getText())) {
            q = Integer.parseInt(qTf.getText());
            if (Cipher.isPrimeNumber(q)) qg = true;
        }

        if (p * q <= 256) {
            pg = false;
            qg = false;
        }

        if(isInteger(eTf.getText())) {
            e = Integer.parseInt(eTf.getText());
            if(p > 0 && q > 0) {
                int m = (p - 1) * (q - 1);
                if(e < m && !Cipher.primeFactorizationOfMContainsPrime(m, e) && e > 1) eg = true;
            }
        }

        AppView.goodOrBadTextField(pTf, pg);
        AppView.goodOrBadTextField(qTf, qg);
        AppView.goodOrBadTextField(eTf, eg);

        if(pg && qg) {
            nTf.setText(Integer.toString(p * q));
            mTf.setText(Integer.toString((p - 1) * (q - 1)));
            if(eg) dTf.setText("8869 xd");
        } else {
            nTf.setText("");
            mTf.setText("");
            dTf.setText("");
        }

        return !pg || !qg || !eg;
    }

    private static boolean isInteger(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}
