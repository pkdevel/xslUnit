package de.pkdevel.xslunit.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.apache.commons.io.FileUtils;
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
	
	@FXML
	private TextArea xsl;
	
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
			final String xml = FileUtils.readFileToString(new File("src/main/resources/META-INF/xslunit/example.xml"), StandardCharsets.UTF_8);
			this.xml.setText(xml);
			
			final String xslt = FileUtils.readFileToString(new File("src/main/resources/META-INF/xslunit/example.xslt"), StandardCharsets.UTF_8);
			this.xsl.setText(xslt);
		}
		catch (final IOException e) {
			logError(e);
		}
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
			catch (ParserConfigurationException | SAXException | IOException | TransformerFactoryConfigurationError | TransformerException e) {
				this.result.setText("Invalid xslt: " + e.getMessage());
				logError(e);
			}
		}
		else {
			try {
				this.expression = this.unit.createXPath(this.xsl.getText());
				this.updateResult();
			}
			catch (XPathFactoryConfigurationException | XPathExpressionException e) {
				this.result.setText("Invalid XPath expression: " + e.getMessage());
				logError(e);
			}
		}
	}
	
	private void updateResult() {
		if (this.document == null || (this.expression == null && this.transformer == null)) {
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
		catch (TransformerFactoryConfigurationError | TransformerException e) {
			this.result.setText("Error performing transformation: " + e.getMessage());
			logError(e);
		}
	}
	
	@FXML
	public void formatXml() {
		if (this.document != null)
			try {
				this.xml.setText(this.unit.format(this.document));
			}
			catch (final TransformerException e) {
				this.result.setText("Error performing format: " + e.getMessage());
				logError(e);
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
		final String xml = this.openFile();
		if (xml != null) {
			this.xml.setText(xml);
		}
	}
	
	@FXML
	public void openXsl() {
		final String xsl = this.openFile();
		if (xsl != null) {
			this.xsl.setText(xsl);
		}
	}
	
	private String openFile() {
		final File file = this.screenController.openFileDialog();
		try {
			return FileUtils.readFileToString(file);
		}
		catch (final IOException e) {
			LOGGER.error("Could not open file", e);
			return null;
		}
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
