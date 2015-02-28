package de.pkdevel.xslunit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

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

public class XslUnitTests {
	
	private XslUnit unit;
	
	@Before
	public void before() {
		this.unit = new XslUnit();
	}
	
	@Test
	public void testCreateDocument() throws Exception {
		final Document xml = this.readDOM("example.xml");
		
		assertThat(xml).isNotNull();
		assertThat(xml.toString()).startsWith("net.sf.saxon.dom.DocumentOverNodeInfo");
	}
	
	@Test
	public void testXpath() throws Exception {
		final Document xml = this.readDOM("example.xml");
		final XPathExpression expression = this.unit.createXPath("/catalog/cd[artist='Bonnie Tyler']/title");
		final String result = this.unit.xPath(xml, expression);
		
		assertEquals("Hide your heart", result);
	}
	
	@Test
	public void testTransform() throws Exception {
		final Document xml = this.readDOM("example.xml");
		final Document xslt = this.readDOM("example.xslt");
		
		final String result = this.unit.transform(xml, xslt);
		
		assertEquals("A Sample Article", result);
	}
	
	private Document readDOM(final String filename) throws IOException, ParserConfigurationException, SAXException {
		final String data = FileUtils.readFileToString(new File("src/test/resources/" + filename), StandardCharsets.UTF_8);
		
		final Document document = this.unit.parseDOM(data);
		return document;
	}
	
}
