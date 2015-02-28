package de.pkdevel.xslunit.view;

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
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.pkdevel.xslunit.XslUnit;

public final class MainGuiController implements Initializable {
	
	@FXML
	private TextArea xml;
	
	@FXML
	private TextArea xsl;
	
	@FXML
	private TextArea result;
	
	private XslUnit unit;
	
	private Document document;
	
	private XPathExpression expression;
	
	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		this.unit = new XslUnit();
		
		try {
			final String xml = FileUtils.readFileToString(new File("src/test/resources/example.xml"), StandardCharsets.UTF_8);
			this.xml.setText(xml);
			
			final String xslt = FileUtils.readFileToString(new File("src/test/resources/example.xslt"), StandardCharsets.UTF_8);
			this.xsl.setText(xslt);
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
		
		/* java 8 lambda expressions
		this.xml.textProperty().addListener((observable, oldValue, newValue) -> {
			this.xmlChanged();
		});
		this.xsl.textProperty().addListener((observable, oldValue, newValue) -> {
			this.xslChanged();
		});
		*/
		
		this.xml.textProperty().addListener(new ChangeListener<String>() {
			
			@Override
			public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
				MainGuiController.this.xmlChanged();
			}
		});
		this.xsl.textProperty().addListener(new ChangeListener<String>() {
			
			@Override
			public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
				MainGuiController.this.xslChanged();
			}
		});
	}
	
	public void xmlChanged() {
		try {
			this.document = this.unit.parseDOM(this.xml.getText());
			this.xslChanged();
		}
		catch (ParserConfigurationException | SAXException | IOException e) {
			this.result.setText("Invalid XML");
			this.document = null;
			e.printStackTrace();
		}
	}
	
	public void xslChanged() {
		if (this.document == null) {
			return;
		}
		if (StringUtils.isEmpty(this.xsl.getText())) {
			return;
		}
		
		if (StringUtils.startsWith(this.xsl.getText(), "<")) {
			this.performXslt();
		}
		else {
			this.performXpath();
		}
	}
	
	private void performXpath() {
		try {
			this.expression = this.unit.createXPath(this.xsl.getText());
			final String result = this.unit.xPath(this.document, this.expression);
			this.result.setText(result);
		}
		catch (XPathFactoryConfigurationException | XPathExpressionException e) {
			this.result.setText("Invalid expression");
			this.expression = null;
			e.printStackTrace();
		}
	}
	
	private void performXslt() {
		try {
			final Document xslt = this.unit.parseDOM(this.xsl.getText());
			final String result = this.unit.transform(this.document, xslt);
			this.result.setText(result);
		}
		catch (ParserConfigurationException | SAXException | IOException | TransformerFactoryConfigurationError | TransformerException e) {
			this.result.setText("Invalid xslt");
			this.expression = null;
			e.printStackTrace();
		}
	}
	
}