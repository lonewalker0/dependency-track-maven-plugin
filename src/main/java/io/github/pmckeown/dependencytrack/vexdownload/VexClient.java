package io.github.pmckeown.dependencytrack.vexdownload;

import io.github.pmckeown.dependencytrack.CommonConfig;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Optional;
import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_VEX;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;





import io.github.pmckeown.dependencytrack.Response;


/**
 * Client for downloading VEX files from Dependency-Track
 */
@Singleton
public class VexClient {

    private final CommonConfig commonConfig;

    @Inject
    public VexClient(CommonConfig commonConfig) {
        this.commonConfig = commonConfig;
    }

    /**
     * Download the VEX file for a given project UUID from the Dependency-Track server.
     *
     * @param projectUuid the UUID of the project
     * @return a response containing the VEX file content if successful
     */
    public Response<String> downloadVex(String projectUuid) {
        String url = commonConfig.getDependencyTrackBaseUrl() + V1_VEX + projectUuid;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            request.addHeader("X-Api-Key", commonConfig.getApiKey());
            request.addHeader("Content-Type", "application/json");

            HttpResponse httpResponse = httpClient.execute(request);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(httpResponse.getEntity());

            Optional<String> body = statusCode >= 200 && statusCode < 300 ? Optional.of(responseBody) : Optional.empty();
            return new Response<>(statusCode, httpResponse.getStatusLine().getReasonPhrase(), body.isPresent(), body);

        } catch (IOException e) {
            e.printStackTrace();
            return new Response<>(500, "Internal Server Error", false, Optional.empty());
        }
    }
    }
