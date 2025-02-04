package org.biovars.bioinformaticsportal.scheduledjobs;

import com.jcraft.jsch.JSchException;
import org.biovars.bioinformaticsportal.hpc.resourceaccount.ResourceAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ScheduledResourceAccountUpdate {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledResourceAccountUpdate.class);

    private final ResourceAccountService resourceAccountService;

    public ScheduledResourceAccountUpdate(ResourceAccountService resourceAccountService) {
        this.resourceAccountService = resourceAccountService;
    }


    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.HOURS)
    void updateResourceAccounts() throws JSchException, InterruptedException {
        try {
            logger.info("Fetching HPC account info");
            resourceAccountService.updateResourceAccounts();
        } catch (Exception e) {
            logger.error("Error updating HPC account info", e);
        }
    }
}
