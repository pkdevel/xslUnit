package de.pkdevel.xslunit;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpression;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.Assert.assertEquals;

public class XslUnitTests {
	
	private XslUnit unit;
	
	@Before
	public void before() {
		this.unit = new XslUnit();
	}
	
	@Test
	public void testCreateDocument() throws Exception {
		final Document document = this.createDocument();
		
		assertThat(document).isNotNull();
		assertThat(document.toString()).startsWith("net.sf.saxon.dom.DocumentOverNodeInfo");
	}
	
	@Test
	public void testXpath() throws Exception {
		final Document document = this.createDocument();
		final XPathExpression expression = this.unit.createXpath("/ARTICLE/TITLE");
		final String result = this.unit.xpath(document, expression);
		
		assertEquals("A Sample Article", result);
	}
	
	private Document createDocument() throws IOException, ParserConfigurationException, SAXException {
		final String data = FileUtils.readFileToString(new File("src/test/resources/example.xml"), StandardCharsets.UTF_8);
		
		final Document document = this.unit.parseXml(data);
		return document;
	}
	
}
