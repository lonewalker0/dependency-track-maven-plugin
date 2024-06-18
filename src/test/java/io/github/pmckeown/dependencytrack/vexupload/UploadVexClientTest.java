package io.github.pmckeown.dependencytrack.vexupload;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import io.github.pmckeown.dependencytrack.AbstractDependencyTrackIntegrationTest;
import io.github.pmckeown.dependencytrack.Response;
import kong.unirest.UnirestException;
import org.junit.Before;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.notFound;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.status;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_VEX_UPLOAD;
import static io.github.pmckeown.dependencytrack.TestUtils.asJson;
import static io.github.pmckeown.dependencytrack.vexupload.VexProcessingResponseBuilder.aVexProcessingResponse;
import static io.github.pmckeown.dependencytrack.vexupload.UploadVexResponseBuilder.anUploadVexResponse;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

    
public class UploadVexClientTest extends AbstractDependencyTrackIntegrationTest {

    private static final String BASE_64_ENCODED_VEX = "blah";

    private UploadVexClient client;
    
    @Before
    public void setup() {
        client = new UploadVexClient(getCommonConfig());
    }

    @Test
    public void thatVexCanBeUploaded() throws Exception {
        stubFor(put(urlEqualTo(V1_VEX_UPLOAD)).willReturn(ok()));

        Response<UploadVexResponse> response = client.uploadVex(aVex());

        assertThat(response.getStatus(), is(equalTo(200)));
        assertThat(response.getStatusText(), is(equalTo("OK")));

        verify(1, putRequestedFor(urlEqualTo(V1_VEX_UPLOAD))
                .withRequestBody(matchingJsonPath("$.projectName", WireMock.equalTo(PROJECT_NAME))));
    }

    @Test
    public void thatHttpErrorsWhenUploadingVexsAreTranslatedIntoAResponse() {
        stubFor(put(urlEqualTo(V1_VEX_UPLOAD)).willReturn(status(418)));

        Response<UploadVexResponse> response = client.uploadVex(aVex());

        assertThat(response.getStatus(), is(equalTo(418)));

        verify(1, putRequestedFor(urlEqualTo(V1_VEX_UPLOAD))
                .withRequestBody(matchingJsonPath("$.projectName", WireMock.equalTo(PROJECT_NAME))));
    }

    @Test
    public void thatConnectionErrorsWhenUploadingVexsAreTranslatedIntoAResponse() {
        stubFor(put(urlEqualTo(V1_VEX_UPLOAD)).willReturn(notFound()));

        Response<UploadVexResponse> response = client.uploadVex(aVex());

        assertThat(response.getStatus(), is(equalTo(404)));

        verify(1, putRequestedFor(urlEqualTo(V1_VEX_UPLOAD))
                .withRequestBody(matchingJsonPath("$.projectName", WireMock.equalTo(PROJECT_NAME))));
    }

    

    

    

    /*
     * Helper methods
     */

    private UploadVexRequest aVex() {
        return new UploadVexRequest(PROJECT_NAME, PROJECT_VERSION, BASE_64_ENCODED_VEX);
    }
}