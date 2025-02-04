package org.biovars.bioinformaticsportal;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class RabbitQueue {

    @Value("rabbit.analysisQueue")
    private String analysisQueue;

    @Bean
    public Queue myQueue() {
        return new Queue("analysisQueue", false);
    }

}
