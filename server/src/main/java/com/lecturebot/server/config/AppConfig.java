package com.lecturebot.server.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // You can customize the RestTemplate here, e.g., set timeouts
        return builder
                .setConnectTimeout(Duration.ofSeconds(5)) // Example: 5 seconds connection timeout
                .setReadTimeout(Duration.ofSeconds(30))   // Example: 30 seconds read timeout
                .build();
    }
}
