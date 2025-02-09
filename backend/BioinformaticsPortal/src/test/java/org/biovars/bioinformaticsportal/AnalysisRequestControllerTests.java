package org.biovars.bioinformaticsportal;

import com.nimbusds.jose.shaded.gson.Gson;
import org.biovars.bioinformaticsportal.analysis.request.AnalysisRequest;
import org.biovars.bioinformaticsportal.analysis.request.AnalysisRequestService;
import org.biovars.bioinformaticsportal.analysis.request.details.AnalysisRequestDetails;
import org.biovars.bioinformaticsportal.analysis.request.statusupdate.AnalysisRequestStatusUpdateDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AnalysisRequestControllerTests {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    private AnalysisRequestService analysisRequestService;

    @Value("${frontendOrigin}")
    private String frontendOrigin;

    @Value("${apiKey}")
    private String apiKey;

    static List<AnalysisRequest> sampleAnalysisRequest() {
        return List.of(new AnalysisRequest(
                "ab-cd-ef",
                "user1",
                500,
                true,
                false,
                new AnalysisRequestDetails(
                        1,
                        "acc1",
                        "asdf",
                        "metagenomics",
                        false,
                        null,
                        List.of()
                )
        ));

    }
    @Test
    void findUserAnalyses_whenAuthenticated_shouldRespondWithAnalysisRequestInfo() throws Exception {
        when(analysisRequestService.findUserAnalyses()).thenReturn(sampleAnalysisRequest());
        this.mockMvc
                .perform(get("/analysis-requests")
                        .header("Origin", frontendOrigin)
                        .with(csrf())
                        .with(oauth2Login())
                )
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("asdf")));
    }

    @Test
    void findUserAnalyses_whenUnauthenticated_shouldRespondWithUnauthorized() throws Exception {
        when(analysisRequestService.findUserAnalyses()).thenReturn(sampleAnalysisRequest());
        this.mockMvc
                .perform(get("/analysis-requests")
                        .header("Origin", frontendOrigin)
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findUserAnalyses_whenNotAuthenticated_shouldRespondWithUnauthorized() throws Exception {
        when(analysisRequestService.findUserAnalyses()).thenReturn(sampleAnalysisRequest());
        this.mockMvc
                .perform(get("/analysis-requests")
                        .header("Origin", frontendOrigin)
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());

    }
    @Test
    void updateStatus_whenNoApiKey_shouldRespondWithUnauthorized() throws Exception {
        AnalysisRequestStatusUpdateDTO update = new AnalysisRequestStatusUpdateDTO("asdf", "user1", 500, true, false);
        this.mockMvc
                .perform(put("/api/analysis-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(update))
                )
                .andExpect(status().isUnauthorized());

    }
    @Test
    void updateStatus_whenInvalidApiKey_shouldRespondWithUnauthorized() throws Exception {
        AnalysisRequestStatusUpdateDTO update = new AnalysisRequestStatusUpdateDTO("asdf", "user1", 500, true, false);
        this.mockMvc
                .perform(put("/api/analysis-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(update))
                        .header("X-API-KEY", "XXXXX")
                )
                .andExpect(status().isUnauthorized());

    }

    @Test
    void updateStatus_whenValidApiKey_shouldRespondWithOk() throws Exception {
        AnalysisRequestStatusUpdateDTO update = new AnalysisRequestStatusUpdateDTO("asdf", "user1", 500, true, false);
        this.mockMvc
                .perform(put("/api/analysis-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(update))
                        .header("X-API-KEY", apiKey)
                        .header("Origin", frontendOrigin)
                )
                .andExpect(status().isOk());

    }
}
