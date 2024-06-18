package io.github.pmckeown.dependencytrack.vexupload;

public class VexProcessingResponseBuilder {

    private boolean processing = false;

    private VexProcessingResponseBuilder() {
        // Use factory methods
    }

    public static VexProcessingResponseBuilder aVexProcessingResponse() {
        return new VexProcessingResponseBuilder();
    }

    public VexProcessingResponseBuilder withProcessing(boolean p) {
        this.processing = p;
        return this;
    }

    public VexProcessingResponse build() {
        return new VexProcessingResponse(processing);
    }
}
