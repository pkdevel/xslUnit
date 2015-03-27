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
		this.loadFrame();
		
		final URL fxml = ClassLoader.getSystemResource("META-INF/view/XslUnitGui.fxml");
		final Parent root = FXMLLoader.load(fxml);
		final Scene scene = new Scene(root);
		
		this.primaryStage.setScene(scene);
		this.primaryStage.setTitle("XSL Unit");
		this.primaryStage.show();
	}
	
	private void loadFrame() {
		final Preferences userPrefs = Preferences.userNodeForPackage(XslUnitApplication.class);
		final Frame frame = new Frame(userPrefs.getDouble("stage.x", 100), userPrefs.getDouble("stage.y", 100),
				userPrefs.getDouble("stage.width", 800), userPrefs.getDouble("stage.height", 600));
		LOGGER.debug("Found screen prefs: " + frame);
		
		if (Screen.getScreensForRectangle(frame.x, frame.y, frame.width, frame.height).size() != 0) {
			LOGGER.debug("Screen prefs seem to be legit, taking them into account");
			this.primaryStage.setX(frame.x);
			this.primaryStage.setY(frame.y);
			this.primaryStage.setWidth(frame.width);
			this.primaryStage.setHeight(frame.height);
		}
	}
	
	@Override
	public void stop() {
		final Frame frame = Frame.fromStage(this.primaryStage);
		LOGGER.debug("Saving screen prefs: " + frame);
		
		final Preferences userPrefs = Preferences.userNodeForPackage(XslUnitApplication.class);
		userPrefs.putDouble("stage.x", frame.x);
		userPrefs.putDouble("stage.y", frame.y);
		userPrefs.putDouble("stage.width", frame.width);
		userPrefs.putDouble("stage.height", frame.height);
	}
	
	private static final class Frame {
		
		final double x, y, width, height;
		
		Frame(final double x, final double y, final double width, final double height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
		
		static Frame fromStage(final Stage stage) {
			return new Frame(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
		}
		
		@Override
		public String toString() {
			return "Frame [x=" + this.x + ", y=" + this.y + ", width=" + this.width + ", height=" + this.height + "]";
		}
		
	}
	
}
