package io.github.pmckeown.dependencytrack.modifyvex;
import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.util.Logger;
import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import java.io.File;

import javax.inject.Inject;

/**
 * Goal which performs suppression matching between suppression XML and VEX JSON files.
 *
 * 
 * @phase process-resources
 */
@Mojo(name = "modify-vex", defaultPhase = LifecyclePhase.VERIFY)
public class SuppressionXmlVexJsonMojo extends AbstractMojo {

    /**
     * Location of the suppression XML file.
     *
      
     * @required
     */
    @Parameter(property="suppressionXml", defaultValue="${project.basedir}/suppression.xml", required=true)
    //@Parameter(property = "suppressionXml", defaultValue = "${project.build.directory}", required = true)
    private File suppressionXml;

    /**
     * Location of the VEX JSON file.
     *
     * @parameter property="vexJson" default-value="${project.basedir}/vex.json"
     * @required
     */
    @Parameter(property="vexJson", defaultValue="${project.basedir}/vex.json", required=true)
    private File vexJson;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    public SuppressionXmlVexJsonMojo() {}

    public SuppressionXmlVexJsonMojo(File suppressionXml, File vexJson, MavenProject project) {
        this.suppressionXml = suppressionXml;
        this.vexJson = vexJson;
        this.project = project;
    }
    
    
    /*public void execute() throws MojoExecutionException {
        Log log = getLog(); // Ottieni l'istanza del log da AbstractMojo
        log.info("Starting suppression matching...");

        String multiModuleProjectDir = System.getProperty("maven.multiModuleProjectDirectory");
        if (multiModuleProjectDir != null && !multiModuleProjectDir.equals(project.getBasedir().getAbsolutePath())) {
            log.info("Skipping module: " + project.getName());
            return;
        }

        try {
            SuppressionMatcher suppressionMatcher = new SuppressionMatcher(suppressionXml, vexJson, log);
            suppressionMatcher.matchSuppressions();
            log.info("Suppression matching completed successfully.");
        } catch (Exception e) {
            throw new MojoExecutionException("Error matching suppressions", e);
        }
    }*/
    public void execute() throws MojoExecutionException {
        Log log = getLog();
        log.info("Starting suppression matching...");

        performSuppressionMatching(log);
    }

    void performSuppressionMatching(Log log) throws MojoExecutionException {
        String multiModuleProjectDir = System.getProperty("maven.multiModuleProjectDirectory");
        if (multiModuleProjectDir != null && !multiModuleProjectDir.equals(project.getBasedir().getAbsolutePath())) {
            log.info("Skipping module: " + project.getName());
            return;
        }
        if (!suppressionXml.exists()) {
            log.error("Suppression XML file not found: " + suppressionXml.getAbsolutePath());
            throw new MojoExecutionException("Suppression XML file not found: " + suppressionXml.getAbsolutePath());
        }

        if (!vexJson.exists()) {
            log.error("VEX JSON file not found: " + vexJson.getAbsolutePath());
            throw new MojoExecutionException("VEX JSON file not found: " + vexJson.getAbsolutePath());
        }
        

        try {
            SuppressionMatcher suppressionMatcher = new SuppressionMatcher(suppressionXml, vexJson, log);
            suppressionMatcher.matchSuppressions();
            log.info("Suppression matching completed successfully.");
        } catch (Exception e) {
            throw new MojoExecutionException("Error matching suppressions", e);
        }
    }

    // Setters for testing
    public void setSuppressionXml(File suppressionXml) {
        this.suppressionXml = suppressionXml;
    }

    public void setVexJson(File vexJson) {
        this.vexJson = vexJson;
    }

    public void setProject(MavenProject project) {
        this.project = project;
    }
}
