package org.biovars.bioinformaticsportal.analysis.request;

import org.biovars.bioinformaticsportal.analysis.request.details.AnalysisRequestDetails;
import org.biovars.bioinformaticsportal.analysis.request.statusupdate.AnalysisRequestStatusUpdateDTO;
import org.biovars.bioinformaticsportal.hpc.resourceaccount.rolemapping.RoleMappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AnalysisRequestController {
    private AnalysisRequestService analysisRequestService;
    private RoleMappingService roleMappingService;
    private static final Logger logger = LoggerFactory.getLogger(AnalysisRequestController.class);

    AnalysisRequestController(AnalysisRequestService analysisRequestService,
                              RoleMappingService roleMappingService) {
        this.analysisRequestService = analysisRequestService;
        this.roleMappingService = roleMappingService;
    }

    @GetMapping("/analysis-requests")
    public List<AnalysisRequest> findUserAnalyses() {
        return analysisRequestService.findUserAnalyses();
    }

    @PostMapping("/analysis-requests")
    public void submitNGSAnalysis(@RequestBody AnalysisRequestDetails details) {
        if (roleMappingService.userCanAccessAccount(details.hpcAccount())) {
            analysisRequestService.save(details);
        }
    }

    @PutMapping("/analysis-requests")
    public void resumeAnalysis(@RequestBody AnalysisRequest analysisRequest) {
        if (roleMappingService.userCanAccessAccount(analysisRequest.details().hpcAccount())) {
            analysisRequestService.resume(analysisRequest);
        }
    }

    @PutMapping("/api/analysis-requests")
    public void updateAnalysisStatus(@RequestBody AnalysisRequestStatusUpdateDTO statusUpdateDTO) {
        logger.info("updating analysis status");
        logger.info(statusUpdateDTO.toString());
        analysisRequestService.updateStatus(statusUpdateDTO);
    }

}

