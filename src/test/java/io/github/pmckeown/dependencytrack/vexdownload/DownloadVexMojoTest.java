package io.github.pmckeown.dependencytrack.vexdownload;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.dependencytrack.project.ProjectAction;
import io.github.pmckeown.util.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import java.util.Optional;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class DownloadVexMojoTest {

    private static final String PROJECT_NAME = "test";

    private static final String PROJECT_VERSION = "1.0";
    @InjectMocks
    private DownloadVexMojo downloadVexMojo;

    @Mock
    private MavenProject mavenProject;

    @Mock
    private VexClient vexClient;

    @Mock
    private ProjectAction projectAction;

    @Mock
    private Logger logger;

    @Mock
    private CommonConfig commonConfig;

    private File outputDirectory;
    private String outputFileName;

    @Before
    public void setup() {
        downloadVexMojo.setMavenProject(mavenProject);
        outputDirectory = new File("target/test-classes");
        outputFileName = "vex_test.json";
        downloadVexMojo.setOutputDirectory(outputDirectory);
        downloadVexMojo.setOutputFileName(outputFileName);
    }

    @Test
    public void testDownloadVexSuccess() throws Exception {
        when(mavenProject.getParent()).thenReturn(null);
        when(mavenProject.getName()).thenReturn(PROJECT_NAME);
        when(mavenProject.getVersion()).thenReturn(PROJECT_VERSION);
        String projectUuid = "test-uuid"; // Replace with the UUID you expect

        String vexContent = "{ \"vex\": \"content\" }";
        Response<String> vexResponse = new Response<>(200, "OK", true, Optional.of(vexContent));
        //when(vexClient.downloadVex("test-uuid")).thenReturn(new Response<>(200, "OK", true, Optional.of(vexContent)));
        when(vexClient.downloadVex(anyString())).thenReturn(vexResponse);
        downloadVexMojo.performAction();

        // Executing the Mojo
        verify(vexClient, times(1)).downloadVex(anyString()); // Verify downloadVex was called exactly once with any string argument

    }

   /*  @Test
    public void testDownloadVexFail() throws Exception {
        String projectUuid = "test-uuid";
        Project project = new Project();
        project.setUuid(projectUuid);

        when(mavenProject.getParent()).thenReturn(null);
        when(projectAction.getProject(anyString(), anyString())).thenReturn(project);
        when(vexClient.downloadVex(projectUuid)).thenReturn(new Response<>(500, "Internal Server Error", false, Optional.empty()));

        MojoExecutionException thrown = assertThrows(MojoExecutionException.class, () -> downloadVexMojo.performAction());
        assertEquals("Failed to download VEX file", thrown.getMessage());

        verify(logger).warn("Failed to download VEX content. Status: 500, Message: Internal Server Error");
    }

    @Test
    public void testDownloadVexIOException() throws Exception {
        String projectUuid = "test-uuid";
        Project project = new Project();
        project.setUuid(projectUuid);

        when(mavenProject.getParent()).thenReturn(null);
        when(projectAction.getProject(anyString(), anyString())).thenReturn(project);
        when(vexClient.downloadVex(projectUuid)).thenThrow(new IOException("Connection error"));

        MojoExecutionException thrown = assertThrows(MojoExecutionException.class, () -> downloadVexMojo.performAction());
        assertEquals("An error occurred while downloading VEX file", thrown.getMessage());

        verify(logger).error("An error occurred while downloading VEX file: Connection error");
    }

    @Test
    public void testSkippingExecutionInSubproject() throws Exception {
        when(mavenProject.getParent()).thenReturn(mock(MavenProject.class));
        downloadVexMojo.performAction();
        verify(logger).info("Skipping execution in subproject: " + mavenProject.getName());
        verifyNoInteractions(projectAction, vexClient);
    }

    @Test
    public void testSaveVexContentToFile() throws Exception {
        String vexContent = "{ \"vex\": \"content\" }";
        downloadVexMojo.saveVexContentToFile(vexContent);
        File outputFile = new File(outputDirectory, outputFileName);
        assertTrue(outputFile.exists());
    }

    @Test
    public void testSaveVexContentToFileIOException() throws Exception {
        String vexContent = "{ \"vex\": \"content\" }";
        File invalidDirectory = new File("/invalid/directory");
        downloadVexMojo.outputDirectory = invalidDirectory;
        
        MojoExecutionException thrown = assertThrows(MojoExecutionException.class, () -> downloadVexMojo.saveVexContentToFile(vexContent));
        assertEquals("Failed to create output directory: " + invalidDirectory.getAbsolutePath(), thrown.getMessage());
    }*/

}
