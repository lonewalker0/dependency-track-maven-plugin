package io.github.pmckeown.dependencytrack.modifyvex;


import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.nio.charset.StandardCharsets;

public class JsonUtils {

    public static JSONObject parseJsonFile(File file) throws Exception {
        String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        return new JSONObject(content);
    }

    public static void writeJsonToFile(File file, JSONObject json) throws Exception {
        FileUtils.writeStringToFile(file, json.toString(4), StandardCharsets.UTF_8);
    }

    public static boolean cveExistsAndModify(JSONObject json, String cve) {
        JSONArray suppressions = json.getJSONArray("vulnerabilities");
        for (int i = 0; i < suppressions.length(); i++) {
            JSONObject suppress = suppressions.getJSONObject(i);
            if (suppress.getString("id").equals(cve)) {
                if (!suppress.has("analysis")) {
                    suppress.put("analysis", new JSONObject());
                }
                if (!suppress.getJSONObject("analysis").optString("state").equals("false_positive")) {
                    suppress.getJSONObject("analysis").put("state", "false_positive");
                    return true;
                }
                return false;
            }
        }
        return false;
    }
}
