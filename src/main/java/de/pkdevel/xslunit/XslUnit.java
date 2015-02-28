package de.pkdevel.xslunit;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import net.sf.saxon.om.NamespaceConstant;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public final class XslUnit {
	
	public XslUnit() {
		System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "net.sf.saxon.dom.DocumentBuilderFactoryImpl");
		System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
		System.setProperty("javax.xml.xpath.XPathFactory:" + NamespaceConstant.OBJECT_MODEL_SAXON, "net.sf.saxon.xpath.XPathFactoryImpl");
	}
	
	public Document parseDOM(final String xml) throws ParserConfigurationException, SAXException, IOException {
		final DocumentBuilder documentBuilder = createDocumentBuilder();
		
		final InputStream xmlReader = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
		
		return documentBuilder.parse(xmlReader);
	}
	
	private static DocumentBuilder createDocumentBuilder() throws ParserConfigurationException {
		final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		
		return documentBuilderFactory.newDocumentBuilder();
	}
	
	public String transform(final Document document, final Document xslt) throws TransformerFactoryConfigurationError, TransformerException {
		final TransformerFactory transformerFactory = TransformerFactory.newInstance();
		
		final DOMSource xsltSource = new DOMSource(xslt);
		final Transformer transformer = transformerFactory.newTransformer(xsltSource);
		
		final StringWriter result = new StringWriter();
		final StreamResult streamResult = new StreamResult(result);
		
		final DOMSource xmlSource = new DOMSource(document);
		transformer.transform(xmlSource, streamResult);
		
		return result.toString();
	}
	
	public XPathExpression createXPath(final String xPathExpression) throws XPathFactoryConfigurationException, XPathExpressionException {
		final XPathFactory factory = XPathFactory.newInstance(NamespaceConstant.OBJECT_MODEL_SAXON);
		final XPath xPath = factory.newXPath();
		
		return xPath.compile(xPathExpression);
	}
	
	public String xPath(final Document document, final XPathExpression xPathExpression) throws XPathExpressionException {
		return (String) xPathExpression.evaluate(document, XPathConstants.STRING);
	}
	
}
