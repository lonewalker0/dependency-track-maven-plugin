package io.github.pmckeown.dependencytrack.modifyvex;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class XmlUtils {

    public static Document parseXmlFile(File file) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();
        return doc;
    }

    public static String getCveFromElement(Element element) {
        String cve = null;
        if (element.getElementsByTagName("cve").getLength() > 0) {
            Node cveNode = element.getElementsByTagName("cve").item(0);
            if (cveNode != null) {
                cve = cveNode.getTextContent();
            }
        } else if (element.getElementsByTagName("vulnerabilityName").getLength() > 0) {
            Node vulnerabilityNameNode = element.getElementsByTagName("vulnerabilityName").item(0);
            if (vulnerabilityNameNode != null) {
                cve = vulnerabilityNameNode.getTextContent();
            }
        }
        return cve;
    }
}
