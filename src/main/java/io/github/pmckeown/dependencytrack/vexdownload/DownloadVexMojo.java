package io.github.pmckeown.dependencytrack.vexdownload;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojo;
import io.github.pmckeown.dependencytrack.CommonConfig;
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.inject.Inject;


@Mojo(name = "download-vex", defaultPhase = LifecyclePhase.VERIFY)
public class DownloadVexMojo extends AbstractDependencyTrackMojo {

    //@Parameter(property = "projectUuid", required = true)
    private String projectUuid;

    @Parameter(property = "outputDirectory", defaultValue = "${project.build.directory}", required = true)
    private File outputDirectory;

    @Parameter(property = "outputFileName", defaultValue = "vex.json", required = true)
    private String outputFileName;

    private final VexClient vexClient;
    private ProjectAction projectAction;
    

    
    @Inject
    private MavenProject mavenProject;

    @Inject
    public DownloadVexMojo(CommonConfig commonConfig,  Logger logger, VexClient vexClient, ProjectAction p_action) {
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
            logger.debug("Attempting to get project with name: " + projectName + " and version: " + projectVersion);
            Project project = projectAction.getProject(projectName, projectVersion);
            if (project == null) {
                throw new MojoExecutionException("Project not found");
            }
            logger.debug("Project found with UUID: " + project.getUuid());
            projectUuid=project.getUuid();
            Response<String> vexResponse = vexClient.downloadVex(projectUuid);
            if (vexResponse.isSuccess() && vexResponse.getBody().isPresent()) {
                String vexContent = vexResponse.getBody().get();
                logger.info("VEX Content downloaded successfully.");
                saveVexContentToFile(vexContent);
            } else {
                logger.warn("Failed to download VEX content. Status: " + vexResponse.getStatus() + ", Message: " + vexResponse.getStatusText());
                throw new MojoExecutionException("Failed to download VEX file");
            }

            
        } catch (Exception e) {
            logger.error("An error occurred while downloading VEX file: " + e.getMessage());
            throw new MojoExecutionException("An error occurred while downloading VEX file", e);
        }
    }
    private void saveVexContentToFile(String vexContent) throws MojoExecutionException {
        if (!outputDirectory.exists()) {
            if (!outputDirectory.mkdirs()) {
                throw new MojoExecutionException("Failed to create output directory: " + outputDirectory.getAbsolutePath());
            }
        }

        File outputFile = new File(outputDirectory, outputFileName);

        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(vexContent);
            logger.info("VEX Content saved to file: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            logger.error("Failed to save VEX content to file: " + e.getMessage());
            throw new MojoExecutionException("Failed to save VEX content to file", e);
        }
    }

    public void setMavenProject(MavenProject mp) {
        this.mavenProject = mp;
    }
    public void setProjectUuid(String projectUuid) {
        this.projectUuid = projectUuid;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }
    
}
