package io.github.pmckeown.dependencytrack.vexupload;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.util.BomEncoder;
import io.github.pmckeown.util.Logger;
import io.github.pmckeown.dependencytrack.Response;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

/**
 * Handles uploading VEX files
 */
@Singleton
public class UploadVexAction {

    private UploadVexClient vexClient;
    private BomEncoder bomEncoder;
    private CommonConfig commonConfig;
    private Logger logger;

    @Inject
    public UploadVexAction(UploadVexClient vexClient, BomEncoder bomEncoder, CommonConfig commonConfig, Logger logger) {
        this.vexClient = vexClient;
        this.bomEncoder = bomEncoder;
        this.commonConfig = commonConfig;
        this.logger = logger;
    }

    public boolean upload(String vexLocation) throws DependencyTrackException {
        logger.info("Project Name: %s", commonConfig.getProjectName());
        logger.info("Project Version: %s", commonConfig.getProjectVersion());

        Optional<String> encodedVexOptional = bomEncoder.encodeBom(vexLocation, logger);
        if (!encodedVexOptional.isPresent()) {
            logger.error("No vex.json could be located at: %s", vexLocation);
            return false;
        }

        Optional<UploadVexResponse> uploadVexResponse = doUpload(encodedVexOptional.get());

        return uploadVexResponse.isPresent();
    }

    private Optional<UploadVexResponse> doUpload(String encodedVex) throws DependencyTrackException {
        try {
            Response<UploadVexResponse> response = vexClient.uploadVex(new UploadVexRequest(
                    commonConfig.getProjectName(), commonConfig.getProjectVersion(), encodedVex));

            if (response.isSuccess()) {
                logger.info("VEX uploaded to Dependency Track server");
                return response.getBody();
            } else {
                String message = String.format("Failure integrating with Dependency Track: %d %s", response.getStatus(),
                        response.getStatusText());
                logger.error(message);
                throw new DependencyTrackException(message);
            }
        } catch (Exception ex) {
            throw new DependencyTrackException(ex.getMessage(), ex);
        }
    }
}
