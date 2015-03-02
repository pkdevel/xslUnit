package de.pkdevel.xslunit;

import java.net.URL;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class XslUnitApplication extends Application {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(XslUnitApplication.class);
	
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
		
		final Preferences userPrefs = Preferences.userNodeForPackage(XslUnitApplication.class);
		final double x = userPrefs.getDouble("stage.x", 100);
		final double y = userPrefs.getDouble("stage.y", 100);
		final double width = userPrefs.getDouble("stage.width", 800);
		final double height = userPrefs.getDouble("stage.height", 600);
		LOGGER.debug("Found screen prefs: " + x + ":" + y + " " + width + "x" + height);
		
		if (Screen.getScreensForRectangle(x, y, width, height).size() != 0) {
			LOGGER.debug("Screen prefs seem to be legit, taking them into account");
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
		final double x = this.primaryStage.getX();
		final double y = this.primaryStage.getY();
		final double width = this.primaryStage.getWidth();
		final double height = this.primaryStage.getHeight();
		LOGGER.debug("Saving screen prefs: " + x + ":" + y + " " + width + "x" + height);
		
		final Preferences userPrefs = Preferences.userNodeForPackage(XslUnitApplication.class);
		userPrefs.putDouble("stage.x", x);
		userPrefs.putDouble("stage.y", y);
		userPrefs.putDouble("stage.width", width);
		userPrefs.putDouble("stage.height", height);
	}
	
}
