package io.github.pmckeown.dependencytrack.vexupload;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.Response;
import kong.unirest.GenericType;
import kong.unirest.RequestBodyEntity;
import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import static kong.unirest.HeaderNames.CONTENT_TYPE;

import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_VEX_UPLOAD;




@Singleton
class UploadVexClient {

    private CommonConfig commonConfig;

    @Inject
    UploadVexClient(CommonConfig commonConfig) {
        this.commonConfig = commonConfig;
    }

    /**
     * Upload a BOM to the Dependency-Track server.  The BOM is processed asynchronously after the upload is completed
     * and the response returned.  The response contains a token that can be used later to query if the bom that the
     * token relates to has been completely processed.
     *
     * @param bom the request object containing the project details and the Base64 encoded bom.xml
     * @return a response containing a token to later determine if processing the supplied BOM is completed
     */
    /*public Response<String> uploadVex(UploadVexRequest vexrequest) {
        String url = commonConfig.getDependencyTrackBaseUrl() + V1_VEX_UPLOAD;
        File vexFile = new File("target/vex.json");
        if (!vexFile.exists()) {
            return new Response<>(404, "VEX file not found", false, Optional.empty());
        }
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("X-Api-Key", commonConfig.getApiKey());

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addPart("file", new FileBody(vexFile));
            builder.addPart("project", new StringBody(vexrequest.getProjectUuid(),ContentType.TEXT_PLAIN ));

            // Aggiungi nome del progetto e versione solo se sono specificati
            if (vexrequest.getProjectName() != null && !vexrequest.getProjectName().isEmpty()) {
                builder.addPart("projectName", new StringBody(vexrequest.getProjectName(),ContentType.TEXT_PLAIN ));
            }
            if (vexrequest.getProjectVersion() != null && !vexrequest.getProjectVersion().isEmpty()) {
                builder.addPart("projectVersion", new StringBody(vexrequest.getProjectVersion(), ContentType.TEXT_PLAIN));
            }

            httpPost.setEntity(builder.build());

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

                Optional<String> body = statusCode >= 200 && statusCode < 300 ? Optional.of(responseBody) : Optional.empty();
                return new Response<>(statusCode, response.getStatusLine().getReasonPhrase(), body.isPresent(), body);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new Response<>(500, "Internal Server Error", false, Optional.empty());
        }
    }*/

    Response<UploadVexResponse> uploadVex(UploadVexRequest vex) {
        RequestBodyEntity requestBodyEntity = Unirest.put(commonConfig.getDependencyTrackBaseUrl() + V1_VEX_UPLOAD)
                .header(CONTENT_TYPE, "application/json")
                .header("X-Api-Key", commonConfig.getApiKey())
                .body(vex);
        HttpResponse<UploadVexResponse> httpResponse = requestBodyEntity.asObject(
                new GenericType<UploadVexResponse>() {});

        Optional<UploadVexResponse> body;
        if (httpResponse.isSuccess()) {
            body = Optional.of(httpResponse.getBody());
        } else {
            body = Optional.empty();
        }

        return new Response<>(httpResponse.getStatus(), httpResponse.getStatusText(), httpResponse.isSuccess(), body);
    }

    
}


