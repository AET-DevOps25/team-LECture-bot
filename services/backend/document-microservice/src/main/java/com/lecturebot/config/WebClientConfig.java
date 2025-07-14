package com.lecturebot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean(name = "genaiWebClient")
    public WebClient genaiWebClient() {
        return WebClient.builder()
                .baseUrl("http://genai-backend-microservice") // Use the Docker Compose service name and port
                .build();
    }
}