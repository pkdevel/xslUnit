package de.pkdevel.xslunit;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class XslUnitApplication extends Application {
	
	public static void main(final String[] args) {
		if (args.length == 0) {
			launch(args);
		}
	}
	
	@Override
	public void start(final Stage stage) throws Exception {
		final URL fxml = ClassLoader.getSystemResource("MainGui.fxml");
		final Parent root = FXMLLoader.load(fxml);
		
		final Scene scene = new Scene(root, 800, 480);
		stage.setTitle("XSL Unit");
		stage.setScene(scene);
		stage.show();
	}
	
}
