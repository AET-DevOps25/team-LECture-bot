package com.lecturebot.config;

import com.lecturebot.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WebClientConfigTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private WebClientConfig webClientConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        webClientConfig = new WebClientConfig();
        // Inject mock manually since field is package-private
        try {
            java.lang.reflect.Field field = WebClientConfig.class.getDeclaredField("jwtTokenProvider");
            field.setAccessible(true);
            field.set(webClientConfig, jwtTokenProvider);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void genaiWebClient_addsAuthorizationHeader() {
        when(jwtTokenProvider.generateServiceToken("document-microservice")).thenReturn("test-token");

        WebClient webClient = webClientConfig.genaiWebClient();

        // Mock ExchangeFunction to capture the outgoing request
        ExchangeFunction exchangeFunction = mock(ExchangeFunction.class);
        when(exchangeFunction.exchange(any(ClientRequest.class))).thenAnswer(invocation -> {
            ClientRequest req = invocation.getArgument(0);
            String authHeader = req.headers().getFirst("Authorization");
            assertNotNull(authHeader);
            assertEquals("Bearer test-token", authHeader);
            // Return a dummy response
            return Mono.empty();
        });

        // Build a request and trigger the filter
        webClient.mutate().exchangeFunction(exchangeFunction).build()
                .get().uri(URI.create("/test"))
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();

        verify(jwtTokenProvider).generateServiceToken("document-microservice");
    }

        @Test
    void genaiWebClient_throwsIfTokenProviderFails() {
        when(jwtTokenProvider.generateServiceToken("document-microservice")).thenThrow(new RuntimeException("token error"));
        WebClient webClient = webClientConfig.genaiWebClient();
        ExchangeFunction exchangeFunction = mock(ExchangeFunction.class);
        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.empty());

        Exception ex = assertThrows(RuntimeException.class, () ->
            webClient.mutate().exchangeFunction(exchangeFunction).build()
                .get().uri(URI.create("/test"))
                .retrieve()
                .bodyToMono(Void.class)
                .block()
        );
        assertEquals("token error", ex.getMessage());
        verify(jwtTokenProvider).generateServiceToken("document-microservice");
    }
}
