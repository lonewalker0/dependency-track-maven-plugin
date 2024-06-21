package io.github.pmckeown.dependencytrack.vexupload;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Encapsulates the request payload for uploading a VEX
 */
public class UploadVexRequest {

    private String projectName;
    private String projectVersion;
    
    private String base64EncodedVex;

    UploadVexRequest(String projectName, String projectVersion, String base64EncodedVex) {
        this.projectName = projectName;
        this.projectVersion = projectVersion;
        
        this.base64EncodedVex = base64EncodedVex;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getProjectVersion() {
        return projectVersion;
    }

    

    public String getVex() {
        return base64EncodedVex;
    }

    /*public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }*/
}
