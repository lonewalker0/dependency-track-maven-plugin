package io.github.pmckeown.dependencytrack.vexupload;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.metrics.MetricsAction;
import io.github.pmckeown.dependencytrack.project.ProjectAction;
import io.github.pmckeown.util.Logger;
import kong.unirest.Unirest;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;

import static io.github.pmckeown.dependencytrack.project.ProjectBuilder.aProject;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@RunWith(MockitoJUnitRunner.class)
public class UploadVexMojoTest {

    private static final String PROJECT_NAME = "test";

    private static final String PROJECT_VERSION = "1.0";

    @InjectMocks
    private UploadVexMojo uploadVexMojo;

    @Mock
    private MavenProject project;

    @Mock
    private UploadVexAction uploadVexAction;

    @Mock
    private MetricsAction metricsAction;

    @Mock
    private ProjectAction projectAction;

    @Mock
    private Logger logger;

    @Mock
    private CommonConfig commonConfig;

    @Before
    public void setup() {
        uploadVexMojo.setMavenProject(project);
    }

    @Test
    public void thatTheVexLocationIsDefaultedWhenNotSupplied() throws Exception {
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        doReturn(new File(".")).when(project).getBasedir();
       // doReturn(aProject().build()).when(projectAction).getProject(PROJECT_NAME, PROJECT_VERSION);
        doReturn(true).when(uploadVexAction).upload(anyString());

        uploadVexMojo.setProjectName(PROJECT_NAME);
        uploadVexMojo.setProjectVersion(PROJECT_VERSION);
        uploadVexMojo.execute();

        verify(uploadVexAction).upload(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue(), is(equalTo("./target/vex.json")));
    }

    @Test
    public void thatTheUploadVexIsSkippedWhenSkipIsTrue() throws Exception {
        uploadVexMojo.setSkip("true");
        uploadVexMojo.setProjectName(PROJECT_NAME);
        uploadVexMojo.setProjectVersion(PROJECT_VERSION);

        uploadVexMojo.execute();

        verify(commonConfig).setProjectName(PROJECT_NAME);
        verify(commonConfig).setProjectVersion(PROJECT_VERSION);
        verifyNoInteractions(uploadVexAction);
        verifyNoInteractions(metricsAction);
        verifyNoInteractions(projectAction);
    }

    @Test
    public void thatTheUploadVexIsSkippedWhenSkipIsReleases() throws Exception {
        uploadVexMojo.setSkip("releases");
        uploadVexMojo.setProjectName(PROJECT_NAME);
        uploadVexMojo.setProjectVersion(PROJECT_VERSION);

        uploadVexMojo.execute();

        verify(commonConfig).setProjectName(PROJECT_NAME);
        verify(commonConfig).setProjectVersion(PROJECT_VERSION);
        verifyNoInteractions(uploadVexAction);
        verifyNoInteractions(metricsAction);
        verifyNoInteractions(projectAction);
    }

    @Test
    public void thatTheUploadVexIsSkippedWhenSkipIsSnapshots() throws Exception {
        String snapshotVersion = "1.0-SNAPSHOT";
        uploadVexMojo.setSkip("snapshots");
        uploadVexMojo.setProjectName(PROJECT_NAME);
        uploadVexMojo.setProjectVersion(snapshotVersion);

        uploadVexMojo.execute();

        verify(commonConfig).setProjectName(PROJECT_NAME);
        verify(commonConfig).setProjectVersion(snapshotVersion);
        verifyNoInteractions(uploadVexAction);
        verifyNoInteractions(metricsAction);
        verifyNoInteractions(projectAction);
    }

    @Test
    public void thatUnirestConfiguredWithSslVerifyOnWhenAsked() throws Exception {
        uploadVexMojo.setVerifySsl(true);
        uploadVexMojo.execute();
        assertThat(Unirest.config().isVerifySsl(), is(equalTo(true)));
    }

    @Test
    public void thatUnirestIsConfiguredWithSslVerifyOffWhenAsked() throws Exception {
        uploadVexMojo.setVerifySsl(false);
        uploadVexMojo.execute();
        assertThat(Unirest.config().isVerifySsl(), is(equalTo(false)));
    }

    

    
}
