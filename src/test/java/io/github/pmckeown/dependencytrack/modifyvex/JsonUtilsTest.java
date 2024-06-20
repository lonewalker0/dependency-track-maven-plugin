package io.github.pmckeown.dependencytrack.modifyvex;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;

import java.io.File;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JsonUtilsTest {

    private File mockFile;
    private JSONObject mockJson;

    @BeforeEach
    void setUp() {
        mockFile = new File("src/test/resources/suppression.xml");
        mockJson = new JSONObject();
    }

    @Test
    void testParseJsonFile() throws Exception {
        File vexFile = new File("src/test/resources/vex.json");

        JSONObject jsonObject = JsonUtils.parseJsonFile(vexFile);

        // Verify the JSON structure, assuming "specVersion" is a known key
        assertNotNull(jsonObject);
        assertEquals("1.5", jsonObject.getString("specVersion"));
        assertTrue(jsonObject.has("metadata"));
        assertTrue(jsonObject.has("vulnerabilities"));
    }

    @Test
    void testWriteJsonToFile(@TempDir Path tempDir) throws Exception {
        mockJson.put("key", "value");

        File tempFile = tempDir.resolve("tempFile.json").toFile();

        JsonUtils.writeJsonToFile(tempFile, mockJson);

        String content = FileUtils.readFileToString(tempFile, StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(content);
        assertEquals("value", jsonObject.getString("key"));
    }

    

    @Test
    void testCveExistsAndModify_FalsePositive() {
        JSONArray vulnerabilities = new JSONArray();
        JSONObject cveObject = new JSONObject();
        cveObject.put("id", "CVE-1234-5678");
        vulnerabilities.put(cveObject);
        mockJson.put("vulnerabilities", vulnerabilities);

        boolean result = JsonUtils.cveExistsAndModify(mockJson, "CVE-1234-5678");

        assertTrue(result);
        assertEquals("false_positive", cveObject.getJSONObject("analysis").getString("state"));
    }

    @Test
    void testCveExistsAndModify_NoChangeNeeded() {
        JSONArray vulnerabilities = new JSONArray();
        JSONObject cveObject = new JSONObject();
        cveObject.put("id", "CVE-1234-5678");
        JSONObject analysisObject = new JSONObject();
        analysisObject.put("state", "false_positive");
        cveObject.put("analysis", analysisObject);
        vulnerabilities.put(cveObject);
        mockJson.put("vulnerabilities", vulnerabilities);

        boolean result = JsonUtils.cveExistsAndModify(mockJson, "CVE-1234-5678");

        assertFalse(result);
    }

    @Test
    void testCveExistsAndModify_CveNotFound() {
        JSONArray vulnerabilities = new JSONArray();
        JSONObject cveObject = new JSONObject();
        cveObject.put("id", "CVE-0000-0000");
        vulnerabilities.put(cveObject);
        mockJson.put("vulnerabilities", vulnerabilities);

        boolean result = JsonUtils.cveExistsAndModify(mockJson, "CVE-1234-5678");

        assertFalse(result);
    }
}

