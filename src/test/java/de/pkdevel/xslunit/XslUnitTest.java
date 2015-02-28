package de.pkdevel.xslunit;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.xpath.XPathExpression;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.Assert.assertEquals;

@RunWith(BlockJUnit4ClassRunner.class)
public final class XslUnitTest {
	
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
		final Transformer transformer = this.unit.createTransformer(xslt);
		
		final String result = this.unit.transform(xml, transformer);
		final String expected = FileUtils.readFileToString(new File("src/test/resources/result.html"), StandardCharsets.UTF_8);
		
		assertEquals(expected, result);
	}
	
	private Document readDOM(final String filename) throws IOException, ParserConfigurationException, SAXException {
		final String data = FileUtils.readFileToString(new File("src/main/resources/META-INF/xslunit/" + filename), StandardCharsets.UTF_8);
		
		return this.unit.parseDOM(data);
	}
	
}
