package com.lecturebot.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;

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
        // Inject tokenProvider via reflection
        setField(filter, "tokenProvider", tokenProvider);
        SecurityContextHolder.clearContext();
    }

    @Test
    void filter_withValidJwt_callsChainAndDoesNotSetAuthentication() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
        when(tokenProvider.validateToken("valid.jwt.token")).thenReturn(true);
        when(tokenProvider.getUsernameFromJWT("valid.jwt.token")).thenReturn("testuser");
        filter.doFilterInternal(request, response, filterChain);
        assertNull(SecurityContextHolder.getContext().getAuthentication()); // No UserDetailsService, so should remain null
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void filter_withInvalidJwt_callsChainAndDoesNotSetAuthentication() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid.jwt.token");
        when(tokenProvider.validateToken("invalid.jwt.token")).thenReturn(false);
        filter.doFilterInternal(request, response, filterChain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void filter_withNoJwtHeader_callsChainAndDoesNotSetAuthentication() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);
        filter.doFilterInternal(request, response, filterChain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void filter_withExceptionDuringValidation_callsChainAndDoesNotSetAuthentication() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
        when(tokenProvider.validateToken("valid.jwt.token")).thenThrow(new RuntimeException("fail"));
        filter.doFilterInternal(request, response, filterChain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

        @Test
    void getJwtFromRequest_withBearerToken_returnsToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer abc.def.ghi");
        java.lang.reflect.Method method = JwtAuthenticationFilter.class.getDeclaredMethod("getJwtFromRequest", javax.servlet.http.HttpServletRequest.class);
        method.setAccessible(true);
        String jwt = (String) method.invoke(filter, request);
        assertEquals("abc.def.ghi", jwt);
    }

    @Test
    void getJwtFromRequest_withNoHeader_returnsNull() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);
        java.lang.reflect.Method method = JwtAuthenticationFilter.class.getDeclaredMethod("getJwtFromRequest", javax.servlet.http.HttpServletRequest.class);
        method.setAccessible(true);
        String jwt = (String) method.invoke(filter, request);
        assertNull(jwt);
    }

    @Test
    void getJwtFromRequest_withEmptyHeader_returnsNull() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("");
        java.lang.reflect.Method method = JwtAuthenticationFilter.class.getDeclaredMethod("getJwtFromRequest", javax.servlet.http.HttpServletRequest.class);
        method.setAccessible(true);
        String jwt = (String) method.invoke(filter, request);
        assertNull(jwt);
    }

    @Test
    void getJwtFromRequest_withWrongPrefix_returnsNull() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Token abc.def.ghi");
        java.lang.reflect.Method method = JwtAuthenticationFilter.class.getDeclaredMethod("getJwtFromRequest", javax.servlet.http.HttpServletRequest.class);
        method.setAccessible(true);
        String jwt = (String) method.invoke(filter, request);
        assertNull(jwt);
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
