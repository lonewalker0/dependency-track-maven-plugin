package io.github.pmckeown.dependencytrack.vexupload;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Encapsulates the response payload for uploading a VEX
 */
public class UploadVexResponse {

    private String token;

    @JsonCreator
    public UploadVexResponse(@JsonProperty("token") String token) {
        this.token = token;
    }

    /*public String getToken() {
        return token;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }*/
}

