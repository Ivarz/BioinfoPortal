package org.biovars.bioinformaticsportal.analysis.result.minio;

import jakarta.servlet.http.HttpServletResponse;
import org.biovars.bioinformaticsportal.analysis.request.AnalysisRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class AnalysisResultController {

    private static final Logger logger = LoggerFactory.getLogger(AnalysisResultController.class);

    private final AnalysisResultService analysisResultService;

    private final AnalysisRequestService analysisRequestService;

    AnalysisResultController(AnalysisResultService analysisResultService, AnalysisRequestService analysisRequestService) {
        this.analysisResultService = analysisResultService;
        this.analysisRequestService = analysisRequestService;
    }

    @GetMapping("list-results")
    public List<AnalysisResultObjectDTO> listResults(@RequestParam String analysisId, HttpServletResponse response) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userId = auth.getName();
            return analysisResultService.listObjects(userId, analysisId);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return new ArrayList<>();
        }
    }

    @GetMapping("/results-download")
    public void downloadFile(@RequestParam String analysisId, @RequestParam String fileName, HttpServletResponse response) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userId = auth.getName();
            analysisResultService.downloadFile(userId, analysisId, fileName, response);
        } catch (Exception e) {
            logger.error(e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/analysis-results")
    public void deleteResults(@RequestParam String analysisId, HttpServletResponse response) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userId = auth.getName();
            logger.info("Removing analysis files from HPC "+analysisId + " for user " + userId);
            analysisRequestService.deleteFromHpc(userId, analysisId);
            analysisResultService.deleteAnalysis(userId, analysisId, response);
            analysisRequestService.deleteByUserIdAndAnalysisId(userId, analysisId);
        } catch (Exception e) {
            logger.error(e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }
}
