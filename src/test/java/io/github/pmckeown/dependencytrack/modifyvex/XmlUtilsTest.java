package io.github.pmckeown.dependencytrack.modifyvex;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import org.xml.sax.InputSource;

import static org.junit.jupiter.api.Assertions.*;

class XmlUtilsTest {

    private File mockXmlFile;

    @BeforeEach
    void setUp() throws Exception {
        mockXmlFile = new File("src/test/resources/suppression.xml");
    }

    @Test
    void testParseXmlFile() throws Exception {
        Document document = XmlUtils.parseXmlFile(mockXmlFile);

        assertNotNull(document);
        assertEquals("suppressions", document.getDocumentElement().getNodeName());
    }

    @Test
void testGetCveFromElementWithCve() throws Exception {
    String xmlContent = "<suppress><cve>CVE-2020-8908</cve></suppress>";
    Document document = parseStringToDocument(xmlContent);
    Element element = (Element) document.getElementsByTagName("suppress").item(0);

    String cve = XmlUtils.getCveFromElement(element);

    assertEquals("CVE-2020-8908", cve);
}

@Test
void testGetCveFromElementWithVulnerabilityName() throws Exception {
    String xmlContent = "<suppress><vulnerabilityName>CVE-2024-30171</vulnerabilityName></suppress>";
    Document document = parseStringToDocument(xmlContent);
    Element element = (Element) document.getElementsByTagName("suppress").item(0);

    String cve = XmlUtils.getCveFromElement(element);

    assertEquals("CVE-2024-30171", cve);
}

@Test
void testGetCveFromElementWithNoCveOrVulnerabilityName() throws Exception {
    String xmlContent = "<suppress><packageUrl regex=\"true\">^pkg:maven/com\\.example/dependency@.*$</packageUrl></suppress>";
    Document document = parseStringToDocument(xmlContent);
    Element element = (Element) document.getElementsByTagName("suppress").item(0);

    String cve = XmlUtils.getCveFromElement(element);

    assertNull(cve);
}
    @Test
    void testParseXmlFileWithInvalidXml() {
        File invalidXmlFile = new File("src/test/resources/invalid.xml");
        assertThrows(Exception.class, () -> {
            XmlUtils.parseXmlFile(invalidXmlFile);
        });
    }

    @Test
    void testParseXmlFileWithNonExistentFile() {
        File nonExistentFile = new File("src/test/resources/nonexistent.xml");
        assertThrows(Exception.class, () -> {
            XmlUtils.parseXmlFile(nonExistentFile);
        });
    }

    private Document parseStringToDocument(String xmlContent) throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    InputSource is = new InputSource(new StringReader(xmlContent));
    return builder.parse(is);
}
}
