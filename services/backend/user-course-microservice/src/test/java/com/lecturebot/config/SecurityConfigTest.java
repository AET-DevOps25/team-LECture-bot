package com.lecturebot.config;

import com.lecturebot.entity.User;
import com.lecturebot.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityConfigTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        securityConfig = new SecurityConfig(userRepository);
    }

    @Test
    void userDetailsService_returnsUserDetails_whenUserExists() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("hash");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        UserDetailsService uds = securityConfig.userDetailsService();
        UserDetails details = uds.loadUserByUsername("test@example.com");
        assertEquals("test@example.com", details.getUsername());
        assertEquals("hash", details.getPassword());
    }

    @Test
    void userDetailsService_throwsException_whenUserNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());
        UserDetailsService uds = securityConfig.userDetailsService();
        assertThrows(UsernameNotFoundException.class, () -> uds.loadUserByUsername("notfound@example.com"));
    }
}
