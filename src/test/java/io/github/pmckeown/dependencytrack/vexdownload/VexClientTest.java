package io.github.pmckeown.dependencytrack.vexdownload;
import org.junit.Before;
import org.junit.Test;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;
import io.github.pmckeown.dependencytrack.AbstractDependencyTrackIntegrationTest;
import io.github.pmckeown.dependencytrack.Response;


import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;


public class VexClientTest extends AbstractDependencyTrackIntegrationTest {

    private VexClient vexClient;

    @Before
    public void setUp() {
        vexClient = new VexClient(getCommonConfig());
    }

    @Test
    public void testDownloadVexSuccess() {
        // Stubbing the wiremock server to return a success response
        stubFor(get(urlPathEqualTo("/api/v1/vex/cyclonedx/project/123"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("VEX File Content")));

        // Performing the actual download
        Response<String> response = vexClient.downloadVex("123");

        // Asserting the response
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertEquals("VEX File Content", response.getBody().get());
    }

    @Test
    public void testDownloadVexFailure() {
        // Stubbing the wiremock server to return a failure response
        stubFor(get(urlPathEqualTo("/api/v1/vex/cyclonedx/project/456"))
                .willReturn(aResponse()
                        .withStatus(404)));

        // Performing the actual download
        Response<String> response = vexClient.downloadVex("456");

        // Asserting the response
        assertFalse(response.isSuccess());
        assertEquals(404, response.getStatus());
        assertFalse(response.getBody().isPresent());
    }

    @Test
    public void testDownloadVexServerError() {
        // Stubbing the wiremock server to return a server error response
        stubFor(get(urlPathEqualTo("/api/v1/vex/cyclonedx/project/789"))
                .willReturn(aResponse()
                        .withStatus(500)));

        // Performing the actual download
        Response<String> response = vexClient.downloadVex("789");

        // Asserting the response
        assertFalse(response.isSuccess());
        assertEquals(500, response.getStatus());
        assertFalse(response.getBody().isPresent());
    }
}
