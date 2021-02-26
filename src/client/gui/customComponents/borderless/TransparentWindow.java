
package client.gui.customComponents.borderless;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
This small class is a utility to prevent some code duplicates. It as also an interface for stages.
 */
public class TransparentWindow extends StackPane {
	

	@FXML
	private StackPane stackPane;
	

	private Logger logger = Logger.getLogger(getClass().getName());
	
	private Stage window = new Stage();


	public TransparentWindow() {
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("TransparentWindow.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "", ex);
		}
		
		window.setTitle("Transparent Window");
		window.initStyle(StageStyle.TRANSPARENT);
		window.initModality(Modality.NONE);
		window.setScene(new Scene(this, Color.TRANSPARENT));
	}
	

	public Stage getWindow() {
		return window;
	}

	public void close() {
		window.close();
	}

	public void show() {
		if (!window.isShowing())
			window.show();
		else
			window.requestFocus();
	}
}
