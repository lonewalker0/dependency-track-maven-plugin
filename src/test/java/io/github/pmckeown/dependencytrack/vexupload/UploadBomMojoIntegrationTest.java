package io.github.pmckeown.dependencytrack.vexupload;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojoTest;
import io.github.pmckeown.dependencytrack.PollingConfig;
import io.github.pmckeown.dependencytrack.ResourceConstants;
import io.github.pmckeown.dependencytrack.TestResourceConstants;
import io.github.pmckeown.util.BomEncoder;
import io.github.pmckeown.util.Logger;
import kong.unirest.Unirest;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.github.pmckeown.TestMojoLoader.loadUploadVexMojo;
import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_VEX_UPLOAD;
import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_PROJECT;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

public class UploadBomMojoIntegrationTest extends AbstractDependencyTrackMojoTest {

    private static final String BOM_LOCATION = "target/test-classes/projects/run/vex.json";

    @Mock
    private BomEncoder bomEncoder;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        doReturn(Optional.of("encoded-bom")).when(bomEncoder).encodeBom(anyString(), any(Logger.class));
    }

    @Test
    public void thatVexCanBeUploadedSuccessfully() throws Exception {
        stubFor(get(urlPathMatching(V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));
        stubFor(put(urlEqualTo(ResourceConstants.V1_VEX_UPLOAD)).willReturn(ok()));

        uploadVexMojo(BOM_LOCATION).execute();

        verify(exactly(1), putRequestedFor(urlEqualTo(ResourceConstants.V1_VEX_UPLOAD)));
    }

    @Test
    public void thatWhenFailOnErrorIsFalseAFailureFromToDependencyTrackDoesNotFailTheBuild() {
        stubFor(get(urlPathMatching(V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));
        stubFor(put(urlEqualTo(ResourceConstants.V1_VEX_UPLOAD)).willReturn(notFound()));

        try {
            UploadVexMojo UploadVexMojo = uploadVexMojo(BOM_LOCATION);
            UploadVexMojo.setFailOnError(false);
            UploadVexMojo.execute();
        } catch (Exception ex) {
            fail("No exception expected");
        }

        verify(exactly(1), putRequestedFor(urlEqualTo(ResourceConstants.V1_VEX_UPLOAD)));
    }

    @Test
    public void thatWhenFailOnErrorIsTrueAFailureFromToDependencyTrackDoesFailTheBuild() throws Exception {
        stubFor(put(urlEqualTo(ResourceConstants.V1_VEX_UPLOAD)).willReturn(notFound()));

        UploadVexMojo UploadVexMojo = null;
        try {
            UploadVexMojo = uploadVexMojo(BOM_LOCATION);
            UploadVexMojo.setDependencyTrackBaseUrl("http://localghost:80");
            UploadVexMojo.setFailOnError(true);
        } catch (Exception ex) {
            fail("Exception not expected yet");
        }

        try {
            UploadVexMojo.execute();
            fail("Exception expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(MojoExecutionException.class)));
        }
    }

    @Test
    public void thatWhenFailOnErrorIsFalseAFailureToConnectToDependencyTrackDoesNotFailTheBuild() {
        // No Wiremock Stubbing

        try {
            UploadVexMojo UploadVexMojo = uploadVexMojo(BOM_LOCATION);
            UploadVexMojo.setDependencyTrackBaseUrl("http://localghost:80");
            UploadVexMojo.setFailOnError(false);
            UploadVexMojo.execute();
        } catch (Exception ex) {
            fail("No exception expected");
        }
    }

    @Test
    public void thatWhenFailOnErrorIsTrueAFailureToConnectToDependencyTrackDoesFailTheBuild() throws Exception {
        // No Wiremock Stubbing

        UploadVexMojo UploadVexMojo = null;

        try {
            UploadVexMojo = uploadVexMojo(BOM_LOCATION);
            UploadVexMojo.setDependencyTrackBaseUrl("http://localghost:80");
            UploadVexMojo.setFailOnError(true);
        } catch (Exception ex) {
            fail("Exception not expected yet");
        }

        try {
            UploadVexMojo.execute();
            fail("Exception expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(MojoExecutionException.class)));
        }
    }

    @Test
    public void thatProjectNameCanBeProvided() throws Exception {
        stubFor(get(urlPathMatching(V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));
        stubFor(put(urlEqualTo(V1_VEX_UPLOAD)).willReturn(ok()));

        UploadVexMojo UploadVexMojo = uploadVexMojo(BOM_LOCATION);
        UploadVexMojo.setProjectName("test-project");
        UploadVexMojo.execute();

        verify(exactly(1), putRequestedFor(urlEqualTo(V1_VEX_UPLOAD))
                .withRequestBody(
                        matchingJsonPath("$.projectName", equalTo("test-project"))));
    }

    @Test
    public void thatProjectNameDefaultsToArtifactId() throws Exception {
        stubFor(get(urlPathMatching(V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));
        stubFor(put(urlEqualTo(V1_VEX_UPLOAD)).willReturn(ok()));

        UploadVexMojo UploadVexMojo = uploadVexMojo(BOM_LOCATION);
        UploadVexMojo.execute();

        verify(exactly(1), putRequestedFor(urlEqualTo(V1_VEX_UPLOAD))
                .withRequestBody(
                        matchingJsonPath("$.projectName", equalTo("dependency-track-maven-plugin-test-project"))));
    }

    @Test
    public void thatProjectVersionCanBeProvided() throws Exception {
        stubFor(get(urlPathMatching(V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));
        stubFor(put(urlEqualTo(V1_VEX_UPLOAD)).willReturn(ok()));


        UploadVexMojo UploadVexMojo = uploadVexMojo(BOM_LOCATION);
        UploadVexMojo.setProjectVersion("99.99.99-RELEASE");
        UploadVexMojo.execute();

        verify(exactly(1), putRequestedFor(urlEqualTo(V1_VEX_UPLOAD))
                .withRequestBody(
                        matchingJsonPath("$.projectVersion", equalTo("99.99.99-RELEASE"))));
    }

    @Test
    public void thatProjectVersionDefaultsToPomVersion() throws Exception {
        stubFor(get(urlPathMatching(V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));
        stubFor(put(urlEqualTo(V1_VEX_UPLOAD)).willReturn(ok()));

        UploadVexMojo UploadVexMojo = uploadVexMojo(BOM_LOCATION);
        UploadVexMojo.execute();

        verify(exactly(1), putRequestedFor(urlEqualTo(V1_VEX_UPLOAD))
                .withRequestBody(
                        matchingJsonPath("$.projectVersion", equalTo("0.0.1-SNAPSHOT"))));
    }

    @Test
    public void thatTheUploadIsSkippedWhenSkipIsTrue() throws Exception {
        stubFor(get(urlPathMatching(V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));
        stubFor(put(urlEqualTo(ResourceConstants.V1_VEX_UPLOAD)).willReturn(ok()));

        UploadVexMojo UploadVexMojo = uploadVexMojo("target/test-classes/projects/skip/vex.json");
        UploadVexMojo.setSkip("true");

        UploadVexMojo.execute();

        verify(exactly(0), putRequestedFor(urlEqualTo(V1_VEX_UPLOAD)));
    }

    @Test
    public void thatSslVerifyDefaultsToTrue() throws Exception {
        UploadVexMojo UploadVexMojo = uploadVexMojo(BOM_LOCATION);
        UploadVexMojo.setSkip("true");
        UploadVexMojo.execute();
        assertThat(Unirest.config().isVerifySsl(), is(true));
    }

    

    

    /*
     * Helper methods
     */

    private UploadVexMojo uploadVexMojo(String bomLocation) throws Exception {
        UploadVexMojo UploadVexMojo = loadUploadVexMojo(mojoRule);
        UploadVexMojo.setDependencyTrackBaseUrl("http://localhost:" + wireMockRule.port());
        
        if (bomLocation != null) {
            UploadVexMojo.setVexLocation(bomLocation);
        }
        UploadVexMojo.setApiKey("ABC123");
        return UploadVexMojo;
    }
}
