package de.pkdevel.xslunit;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
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

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public final class XslUnit {
	
	public Document parseDOM(final String xml) throws ParserConfigurationException, SAXException, IOException {
		if (StringUtils.isEmpty(xml)) {
			return null;
		}
		
		final DocumentBuilder documentBuilder = createDocumentBuilder();
		final InputStream xmlReader = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
		
		return documentBuilder.parse(xmlReader);
	}
	
	private static DocumentBuilder createDocumentBuilder() throws ParserConfigurationException {
		final DocumentBuilderFactory documentBuilderFactory = net.sf.saxon.dom.DocumentBuilderFactoryImpl.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		
		return documentBuilderFactory.newDocumentBuilder();
	}
	
	public String transform(final Document document, final Transformer transformer) throws TransformerFactoryConfigurationError, TransformerException {
		if (document == null || transformer == null) {
			return null;
		}
		
		final StringWriter result = new StringWriter();
		final StreamResult streamResult = new StreamResult(result);
		final DOMSource xmlSource = new DOMSource(document);
		transformer.transform(xmlSource, streamResult);
		
		return result.toString();
	}
	
	public Transformer createTransformer(final Document xslt) throws TransformerFactoryConfigurationError, TransformerException {
		if (xslt == null) {
			return null;
		}
		
		final TransformerFactory transformerFactory = net.sf.saxon.TransformerFactoryImpl.newInstance();
		final DOMSource xsltSource = new DOMSource(xslt);
		
		return transformerFactory.newTransformer(xsltSource);
	}
	
	public XPathExpression createXPath(final String xPathExpression) throws XPathFactoryConfigurationException, XPathExpressionException {
		if (StringUtils.isEmpty(xPathExpression)) {
			return null;
		}
		
		final XPathFactory factory = net.sf.saxon.xpath.XPathFactoryImpl.newInstance(NamespaceConstant.OBJECT_MODEL_SAXON);
		final XPath xPath = factory.newXPath();
		
		return xPath.compile(xPathExpression);
	}
	
	public String xPath(final Document document, final XPathExpression xPathExpression) throws XPathExpressionException {
		if (document == null || xPathExpression == null) {
			return null;
		}
		
		return (String) xPathExpression.evaluate(document, XPathConstants.STRING);
	}
	
	public String format(final Document dom) throws TransformerException {
		final Transformer transformer = net.sf.saxon.TransformerFactoryImpl.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		
		final StreamResult result = new StreamResult(new StringWriter());
		final DOMSource source = new DOMSource(dom);
		transformer.transform(source, result);
		
		return result.getWriter().toString();
	}
	
}
