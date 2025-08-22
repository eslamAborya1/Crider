package com.NTG.Cridir.Config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Bean
    public Queue emailQueue() {
        return new Queue("emailQueue", true);
    }
}
