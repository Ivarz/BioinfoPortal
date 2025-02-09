package org.biovars.bioinformaticsportal;

import com.nimbusds.jose.shaded.gson.Gson;
import org.biovars.bioinformaticsportal.sequence.BatchSequenceSearchFormat;
import org.biovars.bioinformaticsportal.sequence.Sequence;
import org.biovars.bioinformaticsportal.sequence.SequenceController;
import org.biovars.bioinformaticsportal.sequence.SequenceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SequenceController.class)
@ActiveProfiles("test")
public class SequenceControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SequenceService sequenceService;

    @Value("${frontendOrigin}")
    private String frontendOrigin;

    @Test
    void batchFindSequenceFull_whenPostsJson_shouldRespondWithJson() throws Exception {
        BatchSequenceSearchFormat dummyReq = new BatchSequenceSearchFormat(
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                false
        );
        when(sequenceService.batchFindSequenceFull(dummyReq)).thenReturn(List.of(new Sequence(
                1,
                "SM1234",
                "V123",
                "1",
                "1",
                "",
                "file1.fq.gz",
                "file2.fq.gz"
        )));
        this.mockMvc
                .perform(post("/sequences/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(dummyReq))
                        .header("Origin", frontendOrigin)
                        .with(csrf())
                        .with(oauth2Login())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("SM1234")));
    }



}
