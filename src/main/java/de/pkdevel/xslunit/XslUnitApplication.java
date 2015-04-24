package de.pkdevel.xslunit;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pkdevel.xslunit.controller.ScreensController;
import de.pkdevel.xslunit.controller.ScreensController.Screens;

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
		
		final ScreensController screensController = new ScreensController(primaryStage);
		final Scene scene = new Scene(screensController);
		
		this.primaryStage.setScene(scene);
		this.primaryStage.setTitle("XSL Unit");
		this.primaryStage.show();
		
		screensController.setScreen(Screens.MAIN);
	}
	
	private void loadFrame() {
		final Frame frame = Frame.fromPreferences(Preferences.userNodeForPackage(XslUnitApplication.class));
		if (Screen.getScreensForRectangle(frame.x, frame.y, frame.width, frame.height).size() != 0) {
			if (frame.isDefault()) {
				LOGGER.debug("Found valid screen prefs: " + frame);
			}
			
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
		
		try {
			userPrefs.flush();
		}
		catch (final BackingStoreException e) {
			LOGGER.error("Couldn't flush screen preferences", e);
		}
	}
	
}
