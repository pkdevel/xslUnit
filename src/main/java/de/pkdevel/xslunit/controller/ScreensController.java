package de.pkdevel.xslunit.controller;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ScreensController extends StackPane {
	
	public interface Screens {
		
		static final String MAIN = "XslUnitGui.fxml";
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ScreensController.class);
	
	private static final String THEME_LIGHT = "JMetroLightTheme.css";
	
	@SuppressWarnings("unused")
	private static final String THEME_DARK = "JMetroDarkTheme.css";
	
	private final Map<String, Node> screens;
	
	private final Stage primaryStage;
	
	private String theme;
	
	public ScreensController(final Stage primaryStage) {
		this.screens = new HashMap<>(1, 1);
		this.primaryStage = primaryStage;
		
		this.loadTheme(THEME_LIGHT);
	}
	
	public Stage getPrimaryStage() {
		return this.primaryStage;
	}
	
	public boolean setScreen(final String resource) {
		final Node screen = this.getScreen(resource);
		if (screen != null) {
			final DoubleProperty opacity = this.opacityProperty();
			if (this.getChildren().isEmpty()) {
				this.setOpacity(0);
				this.getChildren().add(screen);
				
				final Timeline fadeIn = new Timeline(
						new KeyFrame(Duration.ZERO, new KeyValue(opacity, Double.valueOf(0))),
						new KeyFrame(new Duration(1400), new KeyValue(opacity, Double.valueOf(1))));
				fadeIn.play();
			}
			else {
				final Timeline fade = new Timeline(
						new KeyFrame(Duration.ZERO, new KeyValue(opacity, Double.valueOf(1))),
						new KeyFrame(new Duration(800), new EventHandler<ActionEvent>() {
							
							@Override
							public void handle(final ActionEvent arg0) {
								ScreensController.this.getChildren().remove(0);
								ScreensController.this.getChildren().add(0, screen);
								
								final Timeline fadeIn = new Timeline(
										new KeyFrame(Duration.ZERO, new KeyValue(opacity, Double.valueOf(0))),
										new KeyFrame(new Duration(600), new KeyValue(opacity, Double.valueOf(1))));
								fadeIn.play();
							}
						}, new KeyValue(opacity, Double.valueOf(0))));
				fade.play();
			}
		}
		
		return false;
	}
	
	private Node getScreen(final String resource) {
		Node node = this.screens.get(resource);
		if (node == null) {
			if (this.loadScreen(resource)) {
				node = this.screens.get(resource);
			}
		}
		
		return node;
	}
	
	private boolean loadScreen(final String resource) {
		try {
			final URL fxml = ClassLoader.getSystemResource("META-INF/view/" + resource);
			notNull(fxml, "fxml is null");
			
			final FXMLLoader loader = new FXMLLoader(fxml);
			final Parent root = (Parent) loader.load();
			notNull(root, "root is null");
			if (this.theme != null) {
				root.getStylesheets().clear();
				root.getStylesheets().add(this.theme);
			}
			
			final ControlledScreen controller = loader.getController();
			notNull(controller, "controller is null");
			controller.setController(this);
			
			this.screens.put(resource, root);
			
			return true;
		}
		catch (final Exception e) {
			LOGGER.error("Could not load resource {}:", resource, e);
			return false;
		}
	}
	
	private void loadTheme(final String resource) {
		final URL themeResource = ClassLoader.getSystemResource("META-INF/view/" + resource);
		if (themeResource == null) {
			LOGGER.error("Could not load resource: " + resource);
			this.theme = null;
		}
		else {
			this.theme = themeResource.toExternalForm();
		}
		
		this.getStylesheets().clear();
		this.getStylesheets().add(this.theme);
		
		if (!this.screens.isEmpty()) {
			for (final Node node : this.screens.values()) {
				((Parent) node).getStylesheets().clear();
				((Parent) node).getStylesheets().add(this.theme);
			}
		}
	}
	
	private static void notNull(final Object obj, final String message) {
		if (obj == null) {
			throw new IllegalArgumentException(message);
		}
	}
	
}
