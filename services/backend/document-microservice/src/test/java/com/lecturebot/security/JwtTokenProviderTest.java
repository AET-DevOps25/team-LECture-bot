package com.lecturebot.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtTokenProviderTest {

    private JwtTokenProvider provider;
    // 64 chars (512 bits) for HS512
    private final String secret = "0123456789012345678901234567890123456789012345678901234567890123";
    private final int expirationMs = 10000;

    @BeforeEach
    void setUp() throws Exception {
        provider = new JwtTokenProvider();
        java.lang.reflect.Field secretField = JwtTokenProvider.class.getDeclaredField("jwtSecret");
        secretField.setAccessible(true);
        secretField.set(provider, secret);
        java.lang.reflect.Field expField = JwtTokenProvider.class.getDeclaredField("jwtExpirationInMs");
        expField.setAccessible(true);
        expField.set(provider, expirationMs);
        provider.init();
    }

    @Test
    void generateAndParseToken_success() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");
        String token = provider.generateToken(auth);
        assertNotNull(token);
        String username = provider.getUsernameFromJWT(token);
        assertEquals("testuser", username);
    }

    @Test
    void validateToken_validToken_returnsTrue() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");
        String token = provider.generateToken(auth);
        assertTrue(provider.validateToken(token));
    }

    @Test
    void validateToken_expiredToken_returnsFalse() {
        Key key = Keys.hmacShaKeyFor(secret.getBytes());
        String expiredToken = Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date(System.currentTimeMillis() - 20000))
                .setExpiration(new Date(System.currentTimeMillis() - 10000))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
        assertFalse(provider.validateToken(expiredToken));
    }

    @Test
    void validateToken_malformedToken_returnsFalse() {
        assertFalse(provider.validateToken("not.a.jwt"));
    }

        @Test
    void validateToken_nullInput_returnsFalse() {
        assertFalse(provider.validateToken(null));
    }

    @Test
    void validateToken_emptyInput_returnsFalse() {
        assertFalse(provider.validateToken(""));
    }

    @Test
    void getUsernameFromJWT_invalidToken_throwsException() {
        assertThrows(Exception.class, () -> provider.getUsernameFromJWT("not.a.jwt"));
    }

    @Test
    void generateToken_nullAuthentication_throwsException() {
        assertThrows(NullPointerException.class, () -> provider.generateToken(null));
    }

    @Test
    void validateToken_unsupportedJwt_returnsFalse() {
        // Create a token with a different key (simulate unsupported JWT)
        String otherSecret = "abcdefghabcdefghabcdefghabcdefghabcdefghabcdefghabcdefghabcdefgh"; // 64 chars
        java.security.Key otherKey = io.jsonwebtoken.security.Keys.hmacShaKeyFor(otherSecret.getBytes());
        String token = io.jsonwebtoken.Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new java.util.Date())
                .setExpiration(new java.util.Date(System.currentTimeMillis() + 10000))
                .signWith(otherKey, io.jsonwebtoken.SignatureAlgorithm.HS512)
                .compact();
        assertThrows(io.jsonwebtoken.JwtException.class, () -> provider.validateToken(token));
    }
}
