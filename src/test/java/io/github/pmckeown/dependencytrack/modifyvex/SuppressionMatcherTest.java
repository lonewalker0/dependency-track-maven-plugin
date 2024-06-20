package io.github.pmckeown.dependencytrack.modifyvex;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.logging.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

//import io.takari.maven.testing.executor.MavenRuntime.MavenRuntimeBuilder;


import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;





import org.mockito.junit.MockitoJUnitRunner;







//@RunWith(MavenJUnitTestRunner.class)
//@MavenVersions({"3.6.3"})
@RunWith(MockitoJUnitRunner.class)
public class SuppressionMatcherTest {
    private Log mockLog;

    @Before
    public void setUp() {
        mockLog = Mockito.mock(Log.class);
    }

    @Test
    public void testMatchSuppressions_CveExists_ModifiesAnalysis() throws Exception {
        
        File xmlFile = new File("target/test-classes/suppression.xml");
        File jsonfile= new File("target/test-classes/vex.json");
        
        
        

        SuppressionMatcher matcher = new SuppressionMatcher(xmlFile, jsonfile,mockLog); 
        matcher.matchSuppressions();
        File modifiedJsonFile = new File("target/test-classes/vex.json");
        String modifiedJsonContent = FileUtils.readFileToString(modifiedJsonFile, StandardCharsets.UTF_8);
        JSONObject modifiedJson = new JSONObject(modifiedJsonContent);
        
        assertTrue(modifiedJson.has("vulnerabilities"));

        JSONArray vulnerabilities = modifiedJson.getJSONArray("vulnerabilities");
        boolean foundCve = false;
        for (int i = 0; i < vulnerabilities.length(); i++) {
            JSONObject vulnerability = vulnerabilities.getJSONObject(i);
            if (vulnerability.getString("id").equals("CVE-2020-8908")) {
                assertTrue(vulnerability.has("analysis"));
                JSONObject analysis = vulnerability.getJSONObject("analysis");
                assertTrue(analysis.has("state"));
                assertEquals("false_positive", analysis.getString("state"));
                foundCve = true;
                break;
            }
        }
        assertTrue(foundCve);
    }

    @Test
    public void testMatchSuppressions_CveNotExists() throws Exception {
    // Load sample XML file
    File xmlFile = new File("target/test-classes/suppression.xml");
    // Create a JSON file with no vulnerabilities
    File jsonFile = new File("target/test-classes/vex.json");

    SuppressionMatcher matcher = new SuppressionMatcher(xmlFile, jsonFile, mockLog);
    matcher.matchSuppressions();

    // Assert that no modification is made to the JSON file
    String originalJsonContent = FileUtils.readFileToString(jsonFile, StandardCharsets.UTF_8);
    JSONObject originalJson = new JSONObject(originalJsonContent);
    assertTrue(originalJson.has("vulnerabilities"));
    JSONArray vulnerabilities = originalJson.getJSONArray("vulnerabilities");
    assertEquals(26, vulnerabilities.length());
}
    @Test
public void testMatchSuppressions_NoCveOrVulnerabilityName() throws Exception {

    File xmlFile = new File("target/test-classes/suppression_no_cve.xml");
    File jsonFile = new File("target/test-classes/vex.json");

    // Verify if files exist
    assertTrue(xmlFile.exists());
    assertTrue(jsonFile.exists());

    SuppressionMatcher matcher = new SuppressionMatcher(xmlFile, jsonFile, mockLog);
    matcher.matchSuppressions();

    

    // Assert that no modification is made to the JSON file
    String originalJsonContent = FileUtils.readFileToString(jsonFile, StandardCharsets.UTF_8);
    JSONObject originalJson = new JSONObject(originalJsonContent);
    assertTrue(originalJson.has("vulnerabilities"));
    JSONArray vulnerabilities = originalJson.getJSONArray("vulnerabilities");
    assertEquals(26, vulnerabilities.length());
}

@Test
public void testMatchSuppressions_NoCveFound() throws Exception {
    // Load sample XML file with a CVE element
    File xmlFile = new File("target/test-classes/suppression.xml");
    File jsonFile = new File("target/test-classes/vex_no_cve.json");

    // Verify if file exists
    assertTrue(xmlFile.exists());
    assertTrue(jsonFile.exists());

    SuppressionMatcher matcher = new SuppressionMatcher(xmlFile, jsonFile, mockLog);
    matcher.matchSuppressions();

    // Verify that the log.info message is invoked when CVEs are not found
    verify(mockLog).info("CVE not found: CVE-2020-8908");
    verify(mockLog).info("CVE not found: CVE-2024-30171");

    // Assert that no modification is made to the JSON file
    String originalJsonContent = FileUtils.readFileToString(jsonFile, StandardCharsets.UTF_8);
    JSONObject originalJson = new JSONObject(originalJsonContent);
    assertTrue(originalJson.has("vulnerabilities"));
    JSONArray vulnerabilities = originalJson.getJSONArray("vulnerabilities");
    assertEquals(24, vulnerabilities.length());
}



}

