package io.github.pmckeown.dependencytrack.vexupload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VexProcessingResponse {

    private boolean processing;

    @JsonCreator
    public VexProcessingResponse(@JsonProperty("processing") boolean processing) {
        this.processing = processing;
    }

    public boolean isProcessing() {
        return processing;
    }
}
