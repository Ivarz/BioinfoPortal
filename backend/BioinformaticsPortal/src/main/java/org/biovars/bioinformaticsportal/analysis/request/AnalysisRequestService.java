package org.biovars.bioinformaticsportal.analysis.request;

import com.nimbusds.jose.shaded.gson.Gson;
import org.biovars.bioinformaticsportal.analysis.request.details.AnalysisRequestDetails;
import org.biovars.bioinformaticsportal.analysis.request.statusupdate.AnalysisRequestStatusUpdateDTO;
import org.biovars.bioinformaticsportal.analysis.result.CleanupMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service class for managing analysis requests.
 * This service provides methods for creating, retrieving, updating,
 * and deleting analysis requests within the system.
 * It integrates with a repository for data persistence and uses RabbitMQ
 * for message queuing.
 */
@Service
public class AnalysisRequestService {

    private AnalysisRequestRepository analysisRequestRepository;
    private final RabbitTemplate rabbitTemplate;
    private static final Logger logger = LoggerFactory.getLogger(AnalysisRequestService.class);

    @Value("${rabbit.analysisQueue}")
    private String analysisQueue;

    AnalysisRequestService(AnalysisRequestRepository analysisRequestRepository,
                           RabbitTemplate rabbitTemplate) {
        this.analysisRequestRepository = analysisRequestRepository;
        this.rabbitTemplate = rabbitTemplate;
    }


    public List<AnalysisRequest> findUserAnalyses() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = auth.getName();
        return this.analysisRequestRepository.findByUser(currentUserName);
    }

    void save(AnalysisRequestDetails analysisRequestDetails) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            String analysisId = UUID.randomUUID().toString();

            AnalysisRequest analysisRequest = new AnalysisRequest(analysisId, currentUserName, 100, false, false, analysisRequestDetails);
            this.analysisRequestRepository.save(analysisRequest);
            logger.debug(analysisRequest.toString());
            logger.debug("Rabbit queue name: " + analysisQueue);
            rabbitTemplate.convertAndSend(analysisQueue, new Gson().toJson(analysisRequest));
        } else {
            logger.warn("Unauthenticated user submitted analysis request");
        }
    }

    void resume(AnalysisRequest analysisRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();

            AnalysisRequest resumedAnalysisRequest = new AnalysisRequest(
                analysisRequest.id(),
                currentUserName,
                    (analysisRequest.statusId() / 100) * 100,
                false,
                false,
                analysisRequest.details());

            this.analysisRequestRepository.update(resumedAnalysisRequest);
            logger.info(analysisRequest.toString());
            rabbitTemplate.convertAndSend(analysisQueue, new Gson().toJson(resumedAnalysisRequest));
        } else {
            logger.warn("Unauthenticated user resumed analysis request");
        }
    }

    public void deleteByUserIdAndAnalysisId(String userId, String analysisId) {
        this.analysisRequestRepository.deleteByUserIdAndAnalysisId(userId, analysisId);
    }

    public void deleteFromHpc(String userId, String analysisId) {
        List<AnalysisRequest> analysisRequests = this.analysisRequestRepository.findByUserIdAndAnlysisId(userId, analysisId);
        logger.info("Sending cleanup message to HPC cleanup queue");
        logger.info("found analysis requests: "+analysisRequests.size());
        rabbitTemplate.convertAndSend("hpcCleanupQueue", new Gson().toJson(analysisRequests.get(0)));
    }

    List<AnalysisRequest> findAll() {
        return this.analysisRequestRepository.findAll();
    }
    public void updateStatus(AnalysisRequestStatusUpdateDTO statusUpdateDTO) {
        analysisRequestRepository.updateStatus(statusUpdateDTO);
    }
}
