package client.gui;

import client.data.Message;
import client.data.cipher.Cipher;
import client.data.cipher.Rsa;
import client.gui.customComponents.PromptIntSpinner;
import javafx.beans.InvalidationListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;


public class EncryptionSettingsStage {

    private final Cipher cipher;

    private final Scene monoScene;
    private final Scene polyScene;
    private final Scene rsaScene;
    private final Stage popup;

    public EncryptionSettingsStage(Cipher cipher) {
        this.cipher = cipher;
        popup = new Stage(StageStyle.TRANSPARENT);
        getPopup().setOnShowing(e -> {
            getPopup().setX(MouseInfo.getPointerInfo().getLocation().getX());
            getPopup().setY(MouseInfo.getPointerInfo().getLocation().getY());
        });

        getPopup().focusedProperty().addListener(e -> {
            if(!getPopup().isFocused()) {

                /*
                 This is on purpose not equivalent to the cancelButtonEvents so that you can click off and come back
                 later without having to type everything in again.
                 */
                getPopup().close();
            }
        });

        monoScene = instantiateMonoScene();
        polyScene = instantiatePolyScene();
        rsaScene = instantiateRsaScene();
    }

    public void open(Message.EncryptionMethod encryptionMethod) {
        if(encryptionMethod == Message.EncryptionMethod.CAESAR) {
            getPopup().setScene(getMonoScene());
        } else if(encryptionMethod == Message.EncryptionMethod.VIGENERE) {
            getPopup().setScene(getPolyScene());
        } else if(encryptionMethod == Message.EncryptionMethod.RSA) {
            getPopup().setScene(getRsaScene());
        } else {
            return;
        }
        getPopup().show();
    }

    private Scene instantiateMonoScene() {
        VBox root = new VBox();
        root.setBackground(Background.EMPTY);
        root.setPadding(new Insets(5));
        root.setSpacing(5);

        HBox okCancel = new HBox();
        okCancel.setPadding(new Insets(0, 5, 5, 5));
        okCancel.setSpacing(5);
        okCancel.setAlignment(Pos.BOTTOM_CENTER);

        Button ok = new Button("Ok");
        PromptIntSpinner keySpinner = new PromptIntSpinner( "Key: ", false);
        ok.setDefaultButton(true);
        ok.setDisable(true);
        EventHandler<ActionEvent> okEvent = e -> {
            getCipher().getMonoAlphabetic().setKey(keySpinner.getValue());
            getPopup().close();
        };
        ok.setOnAction(okEvent);
        okCancel.getChildren().add(ok);

        Button cancel = new Button("Cancel");
        EventHandler<ActionEvent> cancelEvent = e -> {
            keySpinner.setVal(getCipher().getMonoAlphabetic().getKey());
            getPopup().close();
        };
        cancel.setOnAction(cancelEvent);
        okCancel.getChildren().add(cancel);

        root.setOnKeyPressed(e -> {
            if(e.getCode().equals(KeyCode.ESCAPE)) {
                cancelEvent.handle(null);
                e.consume();
            }
        });

        keySpinner.setPrefWidth(200);
        keySpinner.setTooltip(new Tooltip(
                "Key: Integer, same as chat partner, neither 0 nor multiple of " + Cipher.getUtfMaxValue()));
        keySpinner.valueProperty().addListener(e -> ok.setDisable(keySpinner.getVal() % Cipher.getUtfMaxValue() == 0));
        keySpinner.getEditor().setOnAction(e -> {
            if(!ok.isDisable()) okEvent.handle(null);
        });

        root.getChildren().addAll(keySpinner, okCancel);

        Scene popupScene = new Scene(root);
        popupScene.setFill(Color.rgb(255, 255, 255, 0.8));

        return popupScene;
    }


    private Scene instantiatePolyScene() {
        VBox root = new VBox();
        root.setBackground(Background.EMPTY);
        root.setPadding(new Insets(5));
        root.setSpacing(5);

        HBox okCancel = new HBox();
        okCancel.setPadding(new Insets(0, 5, 5, 5));
        okCancel.setSpacing(5);
        okCancel.setAlignment(Pos.BOTTOM_CENTER);

        Button ok = new Button("Ok");
        TextField keyTextField = new TextField();
        ok.setDefaultButton(true);
        ok.setDisable(true);
        ok.setOnAction(e -> {
            getCipher().getPolyAlphabetic().setKey(keyTextField.getText());
            getPopup().close();
        });
        okCancel.getChildren().add(ok);

        Button cancel = new Button("Cancel");
        EventHandler<ActionEvent> cancelEvent = e -> {
            keyTextField.setText(getCipher().getPolyAlphabetic().getKey());
            getPopup().close();
        };
        cancel.setOnAction(cancelEvent);
        okCancel.getChildren().add(cancel);

        root.setOnKeyPressed(e -> {
            if(e.getCode().equals(KeyCode.ESCAPE)) {
                cancelEvent.handle(null);
                e.consume();
            }
        });

        keyTextField.textProperty().addListener(e -> ok.setDisable(keyTextField.getText().equals("")));
        keyTextField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
        keyTextField.setPrefWidth(200);
        keyTextField.setPromptText("Key (String, same as chat partner, not empty)");
        root.getChildren().add(keyTextField);

        root.getChildren().add(okCancel);

        Scene popupScene = new Scene(root);
        popupScene.setFill(Color.rgb(255, 255, 255, 0.8));

        return popupScene;
    }


    @SuppressWarnings("CodeBlock2Expr")
    private Scene instantiateRsaScene() {
        VBox root = new VBox();
        root.setBackground(Background.EMPTY);
        root.setPadding(new Insets(5));
        root.setSpacing(5);

        HBox inputRoot = new HBox();
        inputRoot.setBackground(Background.EMPTY);
        inputRoot.setPadding(new Insets(5));
        inputRoot.setSpacing(5);

        VBox clientKeyBox = new VBox();
        clientKeyBox.setBackground(Background.EMPTY);
        clientKeyBox.setPadding(new Insets(5));
        clientKeyBox.setSpacing(5);

        HBox okCancel = new HBox();
        Button ok = new Button("Ok");
        Button cancel = new Button("Cancel");

        /*
         I have no god damn clue why I even have to set this padding (it's the default padding).
         It's to prevent a bug where the padding would flicker from 0,0,0,0 to normal while moving the mouse;
         chill at 0,0,0,0 when not hovering and chill at default when hovering. Makes no sense at all...
         */
        cancel.setPadding(new Insets(4, 8, 4, 8));

        HBox clientKeysAndRandom = new HBox();
        clientKeysAndRandom.setSpacing(20);
        Label clientKeysLabel = new Label("Your Keypair");
        Button fillWithRandomValues = new Button();
        fillWithRandomValues.setGraphic(new ImageView(AppView.RESOURCES + "renew.png"));
        fillWithRandomValues.setPadding(new Insets(2));
        fillWithRandomValues.setTooltip(new Tooltip("Fill fields with random valid values"));

        clientKeysAndRandom.getChildren().addAll(clientKeysLabel, fillWithRandomValues);

        PromptIntSpinner pSpinner = new PromptIntSpinner("p: ", true);
        PromptIntSpinner qSpinner = new PromptIntSpinner("q: ", true);
        TextField nTextField = new TextField("n: " + (pSpinner.getVal() * qSpinner.getVal()));
        nTextField.setEditable(false);
        nTextField.setFocusTraversable(false);

        //TODO make pretty
        nTextField.setStyle("    -fx-highlight-fill: -fx-accent;\n" +
                "    -fx-highlight-text-fill: white;\n" +
                "    -fx-background-color:\n" +
                "        -fx-control-inner-background;\n" +
                "    -fx-background-insets: -0.2, 1, -1.4, 3;\n" +
                "    -fx-background-radius: 3, 2, 4, 0;\n" +
                "    -fx-prompt-text-fill: transparent;");
        InvalidationListener nLabelTextListener = e -> {
            nTextField.setText("n: " + (pSpinner.getVal() * qSpinner.getVal()));
        };
        PromptIntSpinner eSpinner = new PromptIntSpinner("e: ", true);

        CheckBox nBigEnough = new CheckBox("n > " + Cipher.getUtfMaxValue());
        nBigEnough.setStyle("-fx-opacity: 0.7");
        nBigEnough.setDisable(true);
        InvalidationListener nBigEnoughListener = e -> {
            nBigEnough.setSelected((pSpinner.getVal() * qSpinner.getVal()) > Cipher.getUtfMaxValue());
        };
        pSpinner.valueProperty().addListener(nBigEnoughListener);
        qSpinner.valueProperty().addListener(nBigEnoughListener);
        pSpinner.valueProperty().addListener(nLabelTextListener);
        qSpinner.valueProperty().addListener(nLabelTextListener);

        CheckBox eSmallEnough = new CheckBox("e < (p-1) * (q-1)");
        eSmallEnough.setStyle("-fx-opacity: 0.7");
        eSmallEnough.setDisable(true);
        InvalidationListener eSmallEnoughListener = e -> {
            eSmallEnough.setSelected(eSpinner.getVal() < (pSpinner.getVal() - 1) * (qSpinner.getVal() - 1));
        };
        pSpinner.valueProperty().addListener(eSmallEnoughListener);
        qSpinner.valueProperty().addListener(eSmallEnoughListener);
        eSpinner.valueProperty().addListener(eSmallEnoughListener);

        CheckBox eInM = new CheckBox();
        eInM.setStyle("-fx-opacity: 0.7");
        eInM.setDisable(true);
        InvalidationListener eInMListener = e -> {
            eInM.setText("e != " + Cipher.primeFactorization((pSpinner.getVal() - 1) * (qSpinner.getVal() - 1)));
            eInM.setSelected(!Cipher.isEPrimeFactorOfM((pSpinner.getVal() - 1) * (qSpinner.getVal() - 1), eSpinner.getVal()));
        };
        pSpinner.valueProperty().addListener(eInMListener);
        qSpinner.valueProperty().addListener(eInMListener);
        eSpinner.valueProperty().addListener(eInMListener);

        InvalidationListener okEnableListener = e -> {
            ok.setDisable(!(nBigEnough.isSelected() && eSmallEnough.isSelected() && eInM.isSelected()));
        };

        nBigEnough.selectedProperty().addListener(okEnableListener);
        eSmallEnough.selectedProperty().addListener(okEnableListener);
        eInM.selectedProperty().addListener(okEnableListener);

        clientKeyBox.getChildren().addAll(clientKeysAndRandom, pSpinner, qSpinner, nTextField, eSpinner, nBigEnough, eSmallEnough, eInM);

        VBox partnerKeyBox = new VBox();
        partnerKeyBox.setBackground(Background.EMPTY);
        partnerKeyBox.setPadding(new Insets(5));
        partnerKeyBox.setSpacing(5);

        Label partnerKeysLabel = new Label("Keys of chat partner");

        PromptIntSpinner partnerESpinner = new PromptIntSpinner("e: ", true);
        PromptIntSpinner partnerNSpinner = new PromptIntSpinner("n: ", false);
        partnerNSpinner.setVal(Cipher.getUtfMaxValue());

        partnerKeyBox.getChildren().addAll(partnerKeysLabel, partnerESpinner, partnerNSpinner);

        inputRoot.getChildren().addAll(clientKeyBox, partnerKeyBox);

        EventHandler<ActionEvent> loadOldValuesEvent = e -> {
            Rsa rsa = getCipher().getRsa();
            pSpinner.setVal(rsa.getP());
            qSpinner.setVal(rsa.getQ());
            eSpinner.setVal(rsa.getE());
            partnerESpinner.setVal(rsa.getPartnerE());
            partnerNSpinner.setVal(rsa.getPartnerN());
        };
        loadOldValuesEvent.handle(null);
        okEnableListener.invalidated(null);

        EventHandler<ActionEvent> cancelEvent = e -> {
            loadOldValuesEvent.handle(null);
            getPopup().close();
        };
        root.setOnKeyPressed(e -> {
            if(e.getCode().equals(KeyCode.ESCAPE)) {
                cancelEvent.handle(null);
                e.consume();
            }
        });

        ok.setOnAction(e -> {
            getCipher().getRsa().generateKeys(pSpinner.getVal(), qSpinner.getVal(), eSpinner.getVal());
            getCipher().getRsa().setPartnerKey(partnerESpinner.getVal(), partnerNSpinner.getVal());
            getPopup().close();
        });

        fillWithRandomValues.setOnAction(e -> {
            long[] pqe = getCipher().getRsa().getRandomPQE();
            pSpinner.setVal(pqe[0]);
            qSpinner.setVal(pqe[1]);
            eSpinner.setVal(pqe[2]);
        });

        okCancel.setPadding(new Insets(0, 5, 5, 5));
        okCancel.setSpacing(5);
        okCancel.setAlignment(Pos.BOTTOM_CENTER);

        ok.setDefaultButton(true);
        okCancel.getChildren().add(ok);

        cancel.setOnAction(cancelEvent);
        okCancel.getChildren().add(cancel);

        root.getChildren().addAll(inputRoot, okCancel);

        Scene popupScene = new Scene(root);
        popupScene.setFill(Color.rgb(255, 255, 255, 0.8));

        return popupScene;
    }

    private Scene getMonoScene() {
        return monoScene;
    }

    private Scene getPolyScene() {
        return polyScene;
    }

    private Scene getRsaScene() {
        return rsaScene;
    }

    private Stage getPopup() {
        return popup;
    }

    public Cipher getCipher() {
        return cipher;
    }
}
