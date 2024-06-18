package io.github.pmckeown.dependencytrack.modifyvex;

import org.apache.maven.plugin.logging.Log;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.File;

public class SuppressionMatcher {

    private final File suppressionXml;
    private final File vexJson;
    private final Log log;

    public SuppressionMatcher(File suppressionXml, File vexJson, Log log) {
        this.suppressionXml = suppressionXml;
        this.vexJson = vexJson;
        this.log = log;
    }

    public void matchSuppressions() throws Exception {
        // Parse the JSON file
        JSONObject json = JsonUtils.parseJsonFile(vexJson);

        // Parse the XML file
        Document doc = XmlUtils.parseXmlFile(suppressionXml);

        // Get the list of suppressions
        NodeList suppressionsList = doc.getElementsByTagName("suppress");

        // Iterate through suppressions
        for (int i = 0; i < suppressionsList.getLength(); i++) {
            Node suppressionNode = suppressionsList.item(i);
            if (suppressionNode.getNodeType() == Node.ELEMENT_NODE) {
                Element suppressionElement = (Element) suppressionNode;
                String cve = XmlUtils.getCveFromElement(suppressionElement);

                if (cve != null) {
                    // Check if the CVE exists in the JSON
                    if (JsonUtils.cveExistsAndModify(json, cve)) {
                        log.info("Updated CVE: " + cve);
                    } else {
                        log.info("CVE not found: " + cve);
                    }
                } else {
                    log.warn("No CVE or vulnerabilityName found in suppression element.");
                }
            }
        }

        // Write the modified JSON back to the file
        JsonUtils.writeJsonToFile(vexJson, json);
    }
}