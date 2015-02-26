package de.pkdevel.xslunit;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import net.sf.saxon.om.NamespaceConstant;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public final class XslUnit /*extends DefaultHandler */{
	
	public Document parseXml(final String xml) throws ParserConfigurationException, SAXException, IOException {
		final DocumentBuilder documentBuilder = createDocumentBuilder();
		
		final InputStream xmlReader = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
		
		return documentBuilder.parse(xmlReader);
	}
	
	private static DocumentBuilder createDocumentBuilder() throws ParserConfigurationException {
		System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "net.sf.saxon.dom.DocumentBuilderFactoryImpl");
		
		final DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
		dfactory.setNamespaceAware(true);
		
		return dfactory.newDocumentBuilder();
	}
	
	public String transform(final Document document, final String xslt) {
		
		return null;
	}
	
	public String xpath(final Document document, final XPathExpression xpathExpression) throws XPathExpressionException, XPathFactoryConfigurationException {
		return (String) xpathExpression.evaluate(document, XPathConstants.STRING);
	}
	
	public XPathExpression createXpath(final String xpathExpression) throws XPathFactoryConfigurationException, XPathExpressionException {
		System.setProperty("javax.xml.xpath.XPathFactory:" + NamespaceConstant.OBJECT_MODEL_SAXON, "net.sf.saxon.xpath.XPathFactoryImpl");
		
		final XPathFactory factory = XPathFactory.newInstance(NamespaceConstant.OBJECT_MODEL_SAXON);
		final XPath xpath = factory.newXPath();
		final XPathExpression expr = xpath.compile(xpathExpression);
		return expr;
	}
	
}
