package io.github.pmckeown.dependencytrack.vexupload;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojo;
import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.dependencytrack.project.ProjectAction;
import io.github.pmckeown.util.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.inject.Inject;


@Mojo(name = "upload-vex", defaultPhase = LifecyclePhase.VERIFY)
public class UploadVexMojo extends AbstractDependencyTrackMojo {
/* 
    //@Parameter(property = "projectUuid", required = true)
    private String projectUuid;

    @Parameter(property = "InputDirectory", defaultValue = "${project.build.directory}", required = true)
    private File outputDirectory;

    @Parameter(property = "InputFileName", defaultValue = "vex.json", required = true)
    private String outputFileName;

    private final UploadVexClient vexClient;
    private ProjectAction projectAction;
    

    
    @Inject
    private MavenProject mavenProject;

    @Inject
    public UploadVexMojo(CommonConfig commonConfig,  Logger logger, UploadVexClient vexClient, ProjectAction p_action) {
        super(commonConfig, logger);
        this.vexClient = vexClient;
        this.projectAction=p_action;
        
    }

    @Override
    public void performAction() throws MojoExecutionException, MojoFailureException {
        if (mavenProject.getParent() != null) {
            logger.info("Skipping execution in subproject: " + mavenProject.getName());
            return;
        }
        try {
            // Get project details
            Project project = projectAction.getProject(projectName, projectVersion);
            projectUuid = project.getUuid();

            // Prepare upload request
            UploadVexRequest uploadRequest = new UploadVexRequest(projectUuid, projectName, projectVersion);
            Response<String> response = vexClient.uploadVex(uploadRequest);

            // Handle response
            if (response.isSuccess()) {
                logger.info("VEX file uploaded successfully");
            } else {
                logger.warn("Failed to upload VEX file. Status: " + response.getStatus() + ", Message: " + response.getStatusText());
                throw new MojoExecutionException("Failed to upload VEX file");
            }
        } catch (Exception e) {
            logger.error("An error occurred while uploading VEX file: " + e.getMessage());
            throw new MojoExecutionException("An error occurred while uploading VEX file", e);
        }
    }*/
        @Parameter(property = "dependency-track.vexLocation")
    private String vexLocation;

    @Parameter(property = "project", readonly = true, required = true)
    private MavenProject mavenProject;

    private final UploadVexAction uploadVexAction;

    @Inject
    public UploadVexMojo(UploadVexAction uploadVexAction, CommonConfig commonConfig, Logger logger) {
        super(commonConfig, logger);
        this.uploadVexAction = uploadVexAction;
    }

    @Override
    public void performAction() throws MojoExecutionException, MojoFailureException {
        try {
            if (!uploadVexAction.upload(getVexLocation())) {
                handleFailure("Vex upload failed");
            }
        } catch (DependencyTrackException ex) {
            handleFailure("Error occurred during upload", ex);
        }
    }

    public String getVexLocation() {
        if (StringUtils.isNotBlank(vexLocation)) {
            return vexLocation;
        } else {
            String defaultLocation = mavenProject.getBasedir() + "/target/vex.json";
            logger.debug("vexLocation not supplied so using: %s", defaultLocation);
            return defaultLocation;
        }
    }



    void setVexLocation(String bomLocation) {
        this.vexLocation = bomLocation;
    }

    void setMavenProject(MavenProject mp) {
        this.mavenProject = mp;
    }
        
    }
    

