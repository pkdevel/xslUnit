package de.pkdevel.xslunit.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Duration;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.pkdevel.xslunit.XslUnit;

public final class XslUnitGuiController implements Initializable, ControlledScreen {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(XslUnitGuiController.class);
	
	private ScreensController screenController;
	
	@FXML
	private TextArea xml;
	
	private File xmlFile;
	
	@FXML
	private TextArea xsl;
	
	private File xslFile;
	
	@FXML
	private TextArea result;
	
	private XslUnit unit;
	
	private Document document;
	
	private XPathExpression expression;
	
	private Transformer transformer;
	
	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		this.unit = new XslUnit();
		
		this.xml.textProperty().addListener(new ChangeListener<String>() {
			
			@Override
			public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
				XslUnitGuiController.this.xmlChanged();
			}
		});
		this.xsl.textProperty().addListener(new ChangeListener<String>() {
			
			@Override
			public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
				XslUnitGuiController.this.xslChanged();
			}
		});
		
		try {
			final String xml = loadResource("example.xml");
			this.xml.setText(xml);
			
			final String xslt = loadResource("example.xslt");
			this.xsl.setText(xslt);
		}
		catch (final IOException e) {
			logError(e);
		}
	}
	
	private static String loadResource(final String filename) throws IOException {
		@SuppressWarnings("resource")
		final InputStream resource = ClassLoader.getSystemResourceAsStream("META-INF/xslunit/" + filename);
		final String result = IOUtils.toString(resource, StandardCharsets.UTF_8);
		IOUtils.closeQuietly(resource);
		
		return result;
	}
	
	@Override
	public void setController(final ScreensController screenController) {
		this.screenController = screenController;
	}
	
	public void xmlChanged() {
		this.document = null;
		
		try {
			this.document = this.unit.parseDOM(this.xml.getText());
			this.updateResult();
		}
		catch (ParserConfigurationException | SAXException | IOException e) {
			this.result.setText("Invalid XML: " + e.getMessage());
			logError(e);
		}
	}
	
	public void xslChanged() {
		this.transformer = null;
		this.expression = null;
		
		if (StringUtils.startsWith(this.xsl.getText(), "<")) {
			try {
				final Document xslDOM = this.unit.parseDOM(this.xsl.getText());
				this.transformer = this.unit.createTransformer(xslDOM);
				this.updateResult();
			}
			catch (final ParserConfigurationException | SAXException | IOException | TransformerException e) {
				this.result.setText("Invalid xslt: " + e.getMessage());
				logError(e);
			}
		}
		else {
			try {
				this.expression = this.unit.createXPath(this.xsl.getText());
				this.updateResult();
			}
			catch (final XPathExpressionException e) {
				this.result.setText("Invalid XPath expression: " + e.getMessage());
				logError(e);
			}
		}
	}
	
	private void updateResult() {
		if (this.document == null || this.expression == null && this.transformer == null) {
			this.result.setText(null);
		}
		else if (this.expression != null) {
			this.performXpath();
		}
		else if (this.transformer != null) {
			this.performXslt();
		}
	}
	
	private void performXpath() {
		try {
			final String result = this.unit.xPath(this.document, this.expression);
			this.result.setText(result);
		}
		catch (final XPathExpressionException e) {
			this.result.setText("Error on xpath evaluation: " + e.getMessage());
			logError(e);
		}
	}
	
	private void performXslt() {
		try {
			final String result = this.unit.transform(this.document, this.transformer);
			this.result.setText(result);
		}
		catch (final TransformerException e) {
			this.result.setText("Error performing transformation: " + e.getMessage());
			logError(e);
		}
	}
	
	@FXML
	public void formatXml() {
		if (this.document != null) {
			try {
				this.xml.setText(this.unit.format(this.document));
			}
			catch (final TransformerException e) {
				this.result.setText("Error performing format: " + e.getMessage());
				logError(e);
			}
		}
	}
	
	@FXML
	public void formatXsl() {
		try {
			final Document dom = this.unit.parseDOM(this.xsl.getText());
			this.xsl.setText(this.unit.format(dom));
		}
		catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			this.result.setText("Error performing format: " + e.getMessage());
			logError(e);
		}
	}
	
	@FXML
	public void openXml() {
		this.xmlFile = this.openFile(this.xml);
	}
	
	@FXML
	public void openXsl() {
		this.xslFile = this.openFile(this.xsl);
	}
	
	private File openFile(final TextArea text) {
		final FileChooser fileChooser = new FileChooser();
		final File file = fileChooser.showOpenDialog(this.screenController.getPrimaryStage());
		LOGGER.debug("Opening file {}", file);
		
		try {
			final String result = FileUtils.readFileToString(file);
			try {
				this.unit.parseDOM(result);
				text.setText(result);
				
				return file;
			}
			catch (ParserConfigurationException | SAXException e) {
				LOGGER.error("File doesn't seem to be a DOM", e);
			}
		}
		catch (final IOException e) {
		}
		return null;
	}
	
	@FXML
	public void saveXml() {
		this.save(this.xml, this.defaultFile(this.xmlFile));
	}
	
	@FXML
	public void saveXsl() {
		this.save(this.xsl, this.defaultFile(this.xslFile));
	}
	
	private File defaultFile(final File file) {
		if (file == null) {
			final FileChooser fileChooser = new FileChooser();
			return fileChooser.showSaveDialog(this.screenController.getPrimaryStage());
		}
		
		return file;
	}
	
	private void save(final TextArea text, final File file) {
		try {
			LOGGER.debug("Saving file {}", file);
			FileUtils.writeStringToFile(file, text.getText(), StandardCharsets.UTF_8, false);
		}
		catch (final IOException e) {
			logError(e);
		}
	}
	
	@FXML
	public void maxXml(final ActionEvent event) {
		this.maximize(this.xml, this.xsl, event);
	}
	
	@FXML
	public void maxXsl(final ActionEvent event) {
		this.maximize(this.xsl, this.xml, event);
	}
	
	private void maximize(final TextArea toMax, final TextArea other, final ActionEvent event) {
		final Node node = (Node) event.getSource();
		final Window window = node.getScene().getWindow();
		
		final double max = window.getWidth();
		final double maxHeight = window.getHeight();
		
		final KeyFrame start, end;
		if (toMax.getMaxWidth() == 0) {
			start = new KeyFrame(Duration.ZERO,
					new KeyValue(toMax.maxWidthProperty(), Double.valueOf(0)),
					new KeyValue(other.maxWidthProperty(), Double.valueOf(max)));
			end = new KeyFrame(new Duration(450),
					new EventHandler<ActionEvent>() {
						
						@Override
						public void handle(final ActionEvent arg0) {
							toMax.setMaxWidth(Double.MAX_VALUE);
						}
					},
					new KeyValue(toMax.maxWidthProperty(), Double.valueOf(max)),
					new KeyValue(other.maxWidthProperty(), Double.valueOf(0)));
		}
		else if (other.getMaxWidth() == 0) {
			start = new KeyFrame(Duration.ZERO,
					new KeyValue(toMax.maxWidthProperty(), Double.valueOf(max)),
					new KeyValue(other.maxWidthProperty(), Double.valueOf(0)),
					new KeyValue(this.result.maxHeightProperty(), Double.valueOf(0)));
			end = new KeyFrame(new Duration(350),
					new KeyValue(toMax.maxWidthProperty(), Double.valueOf(max / 2)),
					new KeyValue(other.maxWidthProperty(), Double.valueOf(max / 2)),
					new KeyValue(this.result.maxHeightProperty(), Double.valueOf(maxHeight / 2.5)));
		}
		else {
			start = new KeyFrame(Duration.ZERO,
					new KeyValue(toMax.maxWidthProperty(), Double.valueOf(max / 2)),
					new KeyValue(other.maxWidthProperty(), Double.valueOf(max / 2)),
					new KeyValue(this.result.maxHeightProperty(), Double.valueOf(maxHeight / 2.5)));
			end = new KeyFrame(new Duration(350),
					new EventHandler<ActionEvent>() {
						
						@Override
						public void handle(final ActionEvent arg0) {
							toMax.setMaxWidth(Double.MAX_VALUE);
						}
					},
					new KeyValue(toMax.maxWidthProperty(), Double.valueOf(max)),
					new KeyValue(other.maxWidthProperty(), Double.valueOf(0)),
					new KeyValue(this.result.maxHeightProperty(), Double.valueOf(0)));
		}
		
		final Timeline fadeIn = new Timeline(start, end);
		fadeIn.play();
	}
	
	private static void logError(final Throwable t) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.error(t.getMessage(), t);
		}
		else {
			LOGGER.error(t.getMessage());
		}
	}
	
}
