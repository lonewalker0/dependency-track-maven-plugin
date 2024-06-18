package io.github.pmckeown.dependencytrack.vexupload;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.util.BomEncoder;
import io.github.pmckeown.util.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UploadVexActionTest {

    @Mock
    private UploadVexClient vexClient;
    @Mock
    private BomEncoder bomEncoder;
    @Mock
    private CommonConfig commonConfig;
    @Mock
    private Logger logger;

    @InjectMocks
    private UploadVexAction uploadVexAction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpload_Success() throws DependencyTrackException {
        String vexLocation = "target/vex.json";
        String encodedVex = "encodedVex";

        when(commonConfig.getProjectName()).thenReturn("projectName");
        when(commonConfig.getProjectVersion()).thenReturn("1.0");
        when(bomEncoder.encodeBom(vexLocation, logger)).thenReturn(Optional.of(encodedVex));
        when(vexClient.uploadVex(any(UploadVexRequest.class))).thenReturn(new Response<>(200, "OK", true, Optional.of(new UploadVexResponse("token"))));

        assertTrue(uploadVexAction.upload(vexLocation));

        verify(logger).info("Project Name: %s", "projectName");
        verify(logger).info("Project Version: %s", "1.0");
        verify(logger).info("VEX uploaded to Dependency Track server");
    }

    @Test
    void testUpload_EncodingFailure() throws DependencyTrackException {
        String vexLocation = "target/vex.json";

        when(bomEncoder.encodeBom(vexLocation, logger)).thenReturn(Optional.empty());

        assertFalse(uploadVexAction.upload(vexLocation));

        verify(logger).error("No vex.json could be located at: %s", vexLocation);
    }

    
}

