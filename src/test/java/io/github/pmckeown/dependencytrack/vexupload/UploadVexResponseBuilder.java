package io.github.pmckeown.dependencytrack.vexupload;

public class UploadVexResponseBuilder {

    private String token = "12345678-1234-1234-1234-123456789012";

    private UploadVexResponseBuilder() {
        // Use factory methods
    }

    public static UploadVexResponseBuilder anUploadVexResponse() {
        return new UploadVexResponseBuilder();
    }

    public UploadVexResponseBuilder withToken(String t) {
        this.token = t;
        return this;
    }

    public UploadVexResponse build() {
        return new UploadVexResponse(token);
    }
}