package com.lecturebot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import com.lecturebot.security.JwtTokenProvider;

@Configuration
public class WebClientConfig {
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Bean(name = "genaiWebClient")
    public WebClient genaiWebClient() {
        return WebClient.builder()
                .baseUrl("http://api-gateway:8080") // Route through API Gateway
                .filter((request, next) -> {
                    // Add service-to-service JWT authentication
                    String serviceToken = jwtTokenProvider.generateServiceToken("document-microservice");
                    return next.exchange(
                        org.springframework.web.reactive.function.client.ClientRequest.from(request)
                            .header("Authorization", "Bearer " + serviceToken)
                            .build()
                    );
                })
                .build();
    }
}