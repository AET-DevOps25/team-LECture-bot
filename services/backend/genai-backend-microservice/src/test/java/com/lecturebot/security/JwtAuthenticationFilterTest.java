package com.lecturebot.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {
    private JwtAuthenticationFilter filter;
    @Mock
    private JwtTokenProvider tokenProvider;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filter = new JwtAuthenticationFilter();
        try {
            java.lang.reflect.Field tokenProviderField = JwtAuthenticationFilter.class.getDeclaredField("tokenProvider");
            tokenProviderField.setAccessible(true);
            tokenProviderField.set(filter, tokenProvider);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_validToken_setsAuthentication() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
        when(tokenProvider.validateToken("valid.jwt.token")).thenReturn(true);
        when(tokenProvider.getUsernameFromJWT("valid.jwt.token")).thenReturn("testuser");

        filter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("testuser", SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_invalidToken_doesNotSetAuthentication() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid.jwt.token");
        when(tokenProvider.validateToken("invalid.jwt.token")).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_noToken_doesNotSetAuthentication() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_exception_doesNotSetAuthentication() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
        when(tokenProvider.validateToken("valid.jwt.token")).thenThrow(new RuntimeException("fail"));

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void getJwtFromRequest_withBearerToken_returnsToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer abc.def.ghi");
        java.lang.reflect.Method method = JwtAuthenticationFilter.class.getDeclaredMethod("getJwtFromRequest", jakarta.servlet.http.HttpServletRequest.class);
        method.setAccessible(true);
        String jwt = (String) method.invoke(filter, request);
        assertEquals("abc.def.ghi", jwt);
    }

    @Test
    void getJwtFromRequest_withNoHeader_returnsNull() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);
        java.lang.reflect.Method method = JwtAuthenticationFilter.class.getDeclaredMethod("getJwtFromRequest", jakarta.servlet.http.HttpServletRequest.class);
        method.setAccessible(true);
        String jwt = (String) method.invoke(filter, request);
        assertNull(jwt);
    }

    @Test
    void getJwtFromRequest_withEmptyHeader_returnsNull() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("");
        java.lang.reflect.Method method = JwtAuthenticationFilter.class.getDeclaredMethod("getJwtFromRequest", jakarta.servlet.http.HttpServletRequest.class);
        method.setAccessible(true);
        String jwt = (String) method.invoke(filter, request);
        assertNull(jwt);
    }

    @Test
    void getJwtFromRequest_withWrongPrefix_returnsNull() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Token abc.def.ghi");
        java.lang.reflect.Method method = JwtAuthenticationFilter.class.getDeclaredMethod("getJwtFromRequest", jakarta.servlet.http.HttpServletRequest.class);
        method.setAccessible(true);
        String jwt = (String) method.invoke(filter, request);
        assertNull(jwt);
    }
}
