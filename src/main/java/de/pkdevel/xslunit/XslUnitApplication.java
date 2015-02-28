package de.pkdevel.xslunit;

import java.net.URL;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public final class XslUnitApplication extends Application {
	
	private Stage primaryStage;
	
	public static void main(final String[] args) {
		launch(args);
	}
	
	@Override
	public void start(final Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		
		final URL fxml = ClassLoader.getSystemResource("META-INF/view/XslUnitGui.fxml");
		final Parent root = FXMLLoader.load(fxml);
		final Scene scene = new Scene(root);
		final Preferences userPrefs = Preferences.userNodeForPackage(this.getClass());
		final double x = userPrefs.getDouble("stage.x", 100);
		final double y = userPrefs.getDouble("stage.y", 100);
		final double width = userPrefs.getDouble("stage.width", 800);
		final double height = userPrefs.getDouble("stage.height", 600);
		
		if (Screen.getScreensForRectangle(x, y, width, height).size() != 0) {
			this.primaryStage.setX(x);
			this.primaryStage.setY(y);
			this.primaryStage.setWidth(width);
			this.primaryStage.setHeight(height);
		}
		this.primaryStage.setScene(scene);
		this.primaryStage.setTitle("XSL Unit");
		this.primaryStage.show();
	}
	
	@Override
	public void stop() {
		final Preferences userPrefs = Preferences.userNodeForPackage(this.getClass());
		userPrefs.putDouble("stage.x", this.primaryStage.getX());
		userPrefs.putDouble("stage.y", this.primaryStage.getY());
		userPrefs.putDouble("stage.width", this.primaryStage.getWidth());
		userPrefs.putDouble("stage.height", this.primaryStage.getHeight());
	}
	
}
