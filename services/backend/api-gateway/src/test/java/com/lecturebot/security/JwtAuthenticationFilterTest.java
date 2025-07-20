package com.lecturebot.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtAuthenticationFilter filter;
    @Mock
    private JwtTokenProvider tokenProvider;
    @Mock
    private ServerWebExchange exchange;
    @Mock
    private WebFilterChain chain;
    @Mock
    private ServerHttpRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filter = new JwtAuthenticationFilter();
        setField(filter, "tokenProvider", tokenProvider);
        when(exchange.getRequest()).thenReturn(request);
    }

    @Test
    void filter_withValidJwt_callsChainAndSetsAuthentication() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer valid.jwt.token");
        when(request.getHeaders()).thenReturn(headers);
        when(tokenProvider.validateToken("valid.jwt.token")).thenReturn(true);
        when(tokenProvider.getUsernameFromJWT("valid.jwt.token")).thenReturn("testuser");
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        Mono<Void> result = filter.filter(exchange, chain);
        assertDoesNotThrow(() -> { result.block(); return null; });
        verify(chain).filter(exchange);
        verify(tokenProvider).validateToken("valid.jwt.token");
        verify(tokenProvider).getUsernameFromJWT("valid.jwt.token");
    }

    @Test
    void filter_withInvalidJwt_callsChain() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer invalid.jwt.token");
        when(request.getHeaders()).thenReturn(headers);
        when(tokenProvider.validateToken("invalid.jwt.token")).thenReturn(false);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        Mono<Void> result = filter.filter(exchange, chain);
        assertDoesNotThrow(() -> { result.block(); return null; });
        verify(chain).filter(exchange);
        verify(tokenProvider).validateToken("invalid.jwt.token");
    }

    @Test
    void filter_withNoJwtHeader_callsChain() {
        HttpHeaders headers = new HttpHeaders();
        when(request.getHeaders()).thenReturn(headers);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        Mono<Void> result = filter.filter(exchange, chain);
        assertDoesNotThrow(() -> { result.block(); return null; });
        verify(chain).filter(exchange);
    }


    // Helper to set private fields
    private static void setField(Object target, String field, Object value) {
        try {
            java.lang.reflect.Field f = target.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
