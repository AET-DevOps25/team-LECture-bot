package com.lecturebot.security;

import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    private final String jwtSecret = "0123456789012345678901234567890123456789012345678901234567890123"; // 64 chars
    private final int jwtExpirationInMs = 3600000; // 1 hour

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtTokenProvider = new JwtTokenProvider();
        // Set fields via reflection
        setField(jwtTokenProvider, "jwtSecret", jwtSecret);
        setField(jwtTokenProvider, "jwtExpirationInMs", jwtExpirationInMs);
        jwtTokenProvider.init();
    }

    @Test
    void generateToken_and_getUsernameFromJWT_valid() {
        when(authentication.getName()).thenReturn("testuser");
        String token = jwtTokenProvider.generateToken(authentication);
        assertNotNull(token);
        String username = jwtTokenProvider.getUsernameFromJWT(token);
        assertEquals("testuser", username);
    }

    @Test
    void validateToken_validToken_returnsTrue() {
        when(authentication.getName()).thenReturn("testuser");
        String token = jwtTokenProvider.generateToken(authentication);
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    void validateToken_invalidToken_returnsFalse() {
        assertFalse(jwtTokenProvider.validateToken("invalid.token.value"));
    }

    @Test
    void validateToken_malformedJwtException_returnsFalse() {
        // MalformedJwtException is thrown for structurally invalid JWTs
        String malformedToken = "abc.def"; // Not enough segments
        assertFalse(jwtTokenProvider.validateToken(malformedToken));
    }

    @Test
    void validateToken_unsupportedJwtException_returnsFalse() {
        // UnsupportedJwtException is thrown for unsupported JWTs (e.g., wrong alg header)
        // We'll simulate this by tampering with the header
        when(authentication.getName()).thenReturn("testuser");
        String token = jwtTokenProvider.generateToken(authentication);
        // Replace alg in header with an unsupported value
        String[] parts = token.split("\\.");
        String badHeader = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString("{\"alg\":\"none\"}".getBytes());
        String unsupportedToken = badHeader + "." + parts[1] + "." + parts[2];
        assertFalse(jwtTokenProvider.validateToken(unsupportedToken));
    }

    @Test
    void validateToken_illegalArgumentException_returnsFalse() {
        // IllegalArgumentException is thrown for null or empty token
        assertFalse(jwtTokenProvider.validateToken(""));
        assertFalse(jwtTokenProvider.validateToken(null));
    }

    @Test
    void validateToken_expiredToken_returnsFalse() throws InterruptedException {
        // Create a token with a very short expiration
        setField(jwtTokenProvider, "jwtExpirationInMs", 1); // 1 ms
        jwtTokenProvider.init();
        when(authentication.getName()).thenReturn("testuser");
        String token = jwtTokenProvider.generateToken(authentication);
        Thread.sleep(5); // Wait for token to expire
        assertFalse(jwtTokenProvider.validateToken(token));
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

    @Test
    void generateToken_withEmptyUsername_shouldCreateToken() {
        when(authentication.getName()).thenReturn("");
        String token = jwtTokenProvider.generateToken(authentication);
        assertNotNull(token);
        String username = jwtTokenProvider.getUsernameFromJWT(token);
        assertNull(username); // Implementation returns null for empty subject
    }

    @Test
    void getUsernameFromJWT_withMalformedToken_shouldThrowException() {
        String malformedToken = "abc.def";
        assertThrows(Exception.class, () -> jwtTokenProvider.getUsernameFromJWT(malformedToken));
    }

    @Test
    void getUsernameFromJWT_withNullToken_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> jwtTokenProvider.getUsernameFromJWT(null));
    }

    @Test
    void getUsernameFromJWT_withEmptyToken_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> jwtTokenProvider.getUsernameFromJWT(""));
    }

    @Test
    void getUsernameFromJWT_withNoSubject_shouldReturnNull() {
        // Create a token with no subject
        String token = io.jsonwebtoken.Jwts.builder()
                .setIssuedAt(new java.util.Date())
                .setExpiration(new java.util.Date(System.currentTimeMillis() + 10000))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), io.jsonwebtoken.SignatureAlgorithm.HS512)
                .compact();
        assertNull(jwtTokenProvider.getUsernameFromJWT(token));
    }

}
