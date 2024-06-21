package io.github.pmckeown.dependencytrack.vexdownload;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.dependencytrack.project.ProjectAction;
import io.github.pmckeown.util.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.junit.runner.RunWith;
import org.mockito.*;
import java.util.Optional;
import java.nio.file.Path;

import java.nio.file.Files;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static kong.unirest.Unirest.options;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class DownloadVexMojoTest {

    private static final String PROJECT_NAME = "test";

    private static final String PROJECT_VERSION = "1.0";

    private String projectUuid = "12345678-90ab-cdef-1234-567890abcdef";

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

    @TempDir
    Path tempDir;

    private File outputDirectory;
    private String outputFileName;

    @Before
    public void setup() {
        /*downloadVexMojo.setMavenProject(mavenProject);
        outputDirectory = new File("target/test-classes");
        outputFileName = "vex_test.json";
        downloadVexMojo.setOutputDirectory(outputDirectory);
        downloadVexMojo.setOutputFileName(outputFileName);*/
        MockitoAnnotations.openMocks(this);
        downloadVexMojo = new DownloadVexMojo(commonConfig, logger, vexClient, projectAction);
        downloadVexMojo.setMavenProject(mavenProject);
        downloadVexMojo.setProjectName(PROJECT_NAME);
        downloadVexMojo.setProjectVersion(PROJECT_VERSION);
        downloadVexMojo.setOutputDirectory(new File("target/test-classes/test"));
        downloadVexMojo.setOutputFileName("vex.json");
        
    }

    @Test
    public void testPerformAction() throws DependencyTrackException, MojoExecutionException, MojoFailureException  {
         // Mock the Maven project
    when(mavenProject.getParent()).thenReturn(null);
   // when(mavenProject.getName()).thenReturn("test-project");

    // Mock the ProjectAction to return a valid project
    Project mockProject = mock(Project.class);
    when(mockProject.getUuid()).thenReturn("project-uuid");
    when(projectAction.getProject(anyString(), anyString())).thenReturn(mockProject);

    // Mock the VexClient to return a successful response
    Response<String> mockResponse = mock(Response.class);
        when(mockResponse.isSuccess()).thenReturn(true);
        when(mockResponse.getBody()).thenReturn(Optional.of("VEX Content"));
        when(vexClient.downloadVex("project-uuid")).thenReturn(mockResponse);
    

    // Execute the action
    downloadVexMojo.performAction();
    verify(logger).debug("Attempting to get project with name: " + PROJECT_NAME + " and version: " + PROJECT_VERSION);
    verify(logger).debug("Project found with UUID: project-uuid");
    verify(logger).info("VEX Content downloaded successfully.");
    }

     @Test
public void testPerformActionProjectNotFound() throws Exception {
    // Mock the Maven project
    when(mavenProject.getParent()).thenReturn(null);
     //when(mavenProject.getName()).thenReturn("test-project");

    // Mock the ProjectAction to return null (project not found)
    when(projectAction.getProject(anyString(), anyString())).thenReturn(null);

    // Execute the action
    MojoExecutionException exception = assertThrows(MojoExecutionException.class, () -> {
        downloadVexMojo.performAction();
    });

    // Verify the interactions and behavior
    verify(logger).debug("Attempting to get project with name: " + PROJECT_NAME + " and version: " + PROJECT_VERSION);
    verify(logger).error(eq("An error occurred while downloading VEX file: Project not found"));
    
}

@Test
public void testPerformActionDownloadFailure() throws Exception {
    // Mock the Maven project
    when(mavenProject.getParent()).thenReturn(null);
    // when(mavenProject.getName()).thenReturn("test-project");

    // Mock the ProjectAction to return a valid project
    Project mockProject = mock(Project.class);
    when(mockProject.getUuid()).thenReturn("project-uuid");
    when(projectAction.getProject(anyString(), anyString())).thenReturn(mockProject);

    // Mock the VexClient to return a failure response
    Response<String> mockResponse = mock(Response.class);
    when(mockResponse.isSuccess()).thenReturn(false);
    when(mockResponse.getStatus()).thenReturn(500); // Simulate a failure status code
    when(mockResponse.getStatusText()).thenReturn("Internal Server Error");
    when(vexClient.downloadVex("project-uuid")).thenReturn(mockResponse);

    // Execute the action
    MojoExecutionException exception = assertThrows(MojoExecutionException.class, () -> {
        downloadVexMojo.performAction();
    });

    // Verify the interactions and behavior
    verify(logger).debug("Attempting to get project with name: " + PROJECT_NAME + " and version: " + PROJECT_VERSION);
    verify(logger).debug("Project found with UUID: project-uuid");
    verify(logger).warn("Failed to download VEX content. Status: 500, Message: Internal Server Error");
   // assertTrue(exception.getMessage().contains("Failed to download VEX file"));
}

    
    

    

    

    
        
 }

    

  


