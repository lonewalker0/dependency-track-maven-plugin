package io.github.pmckeown.dependencytrack.modifyvex;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.apache.maven.plugin.logging.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;

import static org.mockito.Mockito.*;
import java.io.File;

import java.lang.reflect.Field;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

@RunWith(MockitoJUnitRunner.class)
public class SuppressionXmlVexJsonMojoTest {

    @Mock
    private Log logMock;

    @Mock
    private MavenProject projectMock;

    @Test
    public void testPerformSuppressionMatchingFilesExist() throws MojoExecutionException {
        File suppressionXml = new File("target/test-classes/suppression.xml");
        File vexJson = new File("target/test-classes/vex.json");

        
        

        SuppressionXmlVexJsonMojo mojo = new SuppressionXmlVexJsonMojo();
        mojo.setSuppressionXml(suppressionXml);
        mojo.setVexJson(vexJson);
        mojo.setProject(projectMock);

        mojo.performSuppressionMatching(logMock);
        

        

        verify(logMock, times(1)).info("Suppression matching completed successfully.");
    }

    @Test(expected = MojoExecutionException.class)
    public void testPerformSuppressionMatchingSuppressionXmlNotExists() throws MojoExecutionException {
        File suppressionXml = mock(File.class);
        File vexJson = mock(File.class);

        when(suppressionXml.exists()).thenReturn(false);

        SuppressionXmlVexJsonMojo mojo = new SuppressionXmlVexJsonMojo();
        mojo.setSuppressionXml(suppressionXml);
        mojo.setVexJson(vexJson);
        
        mojo.performSuppressionMatching(logMock);

        verify(logMock, times(1)).error("Suppression XML file not found: " + suppressionXml.getAbsolutePath());
    }

    @Test(expected = MojoExecutionException.class)
    public void testPerformSuppressionMatchingVexJsonNotExists() throws MojoExecutionException {
        File suppressionXml = mock(File.class);
        File vexJson = mock(File.class);

        when(suppressionXml.exists()).thenReturn(true);
        when(vexJson.exists()).thenReturn(false);

        SuppressionXmlVexJsonMojo mojo = new SuppressionXmlVexJsonMojo();
        mojo.setSuppressionXml(suppressionXml);
        mojo.setVexJson(vexJson);
        
        mojo.performSuppressionMatching(logMock);

        verify(logMock, times(1)).error("VEX JSON file not found: " + vexJson.getAbsolutePath());
    }
}