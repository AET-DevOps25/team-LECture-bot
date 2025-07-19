package com.lecturebot.service;

import com.lecturebot.entity.User;
import com.lecturebot.generated.model.LoginRequest;
import com.lecturebot.generated.model.LoginResponse;
import com.lecturebot.generated.model.RegisterRequest;
import com.lecturebot.generated.model.UpdateUserProfileRequest;
import com.lecturebot.generated.model.UserProfile;
import com.lecturebot.repository.UserRepository;
import com.lecturebot.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private Authentication authentication;
    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_success() {
        RegisterRequest request = new RegisterRequest().name("Test").email("test@example.com").password("pass");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("hashed");
        User savedUser = new User();
        savedUser.setName("Test");
        savedUser.setEmail("test@example.com");
        savedUser.setPasswordHash("hashed");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        User result = userService.registerUser(request);
        assertEquals("Test", result.getName());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("hashed", result.getPasswordHash());
    }

    @Test
    void registerUser_emailExists_throwsException() {
        RegisterRequest request = new RegisterRequest().name("Test").email("test@example.com").password("pass");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(request));
    }

    @Test
    void loginUser_success() {
        LoginRequest loginRequest = new LoginRequest().email("test@example.com").password("pass");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("jwt-token");
        LoginResponse response = userService.loginUser(loginRequest);
        assertEquals("jwt-token", response.getToken());
    }

    @Test
    void loginUser_invalidCredentials_throwsException() {
        LoginRequest loginRequest = new LoginRequest().email("test@example.com").password("wrong");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new RuntimeException("Invalid credentials"));
        assertThrows(RuntimeException.class, () -> userService.loginUser(loginRequest));
    }

    @Test
    void getUserProfile_success() {
        User user = new User();
        user.setId(1L);
        user.setName("Test");
        user.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        UserProfile profile = userService.getUserProfile("test@example.com");
        assertEquals(1L, profile.getId());
        assertEquals("Test", profile.getName());
        assertEquals("test@example.com", profile.getEmail());
    }

    @Test
    void getUserProfile_notFound_throwsException() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.getUserProfile("notfound@example.com"));
    }

    @Test
    void updateUserProfile_success() {
        User user = new User();
        user.setId(1L);
        user.setName("Old");
        user.setEmail("old@example.com");
        UpdateUserProfileRequest req = new UpdateUserProfileRequest().name("New").email("new@example.com");
        when(userRepository.findByEmail("old@example.com")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserProfile profile = userService.updateUserProfile("old@example.com", req);
        assertEquals("New", profile.getName());
        assertEquals("new@example.com", profile.getEmail());
    }

    @Test
    void updateUserProfile_newEmailExists_throwsException() {
        User user = new User();
        user.setEmail("old@example.com");
        UpdateUserProfileRequest req = new UpdateUserProfileRequest().name("New").email("new@example.com");
        when(userRepository.findByEmail("old@example.com")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.of(new User()));
        assertThrows(IllegalArgumentException.class, () -> userService.updateUserProfile("old@example.com", req));
    }

    @Test
    void updateUserProfile_userNotFound_throwsException() {
        UpdateUserProfileRequest req = new UpdateUserProfileRequest().name("New").email("new@example.com");
        when(userRepository.findByEmail("old@example.com")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.updateUserProfile("old@example.com", req));
    }

    @Test
    void changePassword_success() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("oldhash");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldpass", "oldhash")).thenReturn(true);
        when(passwordEncoder.encode("newpass")).thenReturn("newhash");
        userService.changePassword("test@example.com", "oldpass", "newpass");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void changePassword_wrongOldPassword_throwsException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("oldhash");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "oldhash")).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> userService.changePassword("test@example.com", "wrong", "newpass"));
    }

    @Test
    void changePassword_userNotFound_throwsException() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.changePassword("notfound@example.com", "old", "new"));
    }

    @Test
    void getCurrentAuthenticatedUser_success() {
        User user = new User();
        user.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userDetails.getUsername()).thenReturn("test@example.com");
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);
        org.springframework.security.core.context.SecurityContext securityContext = mock(org.springframework.security.core.context.SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        User result = userService.getCurrentAuthenticatedUser();
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void getCurrentAuthenticatedUser_userNotFound_throwsException() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userDetails.getUsername()).thenReturn("test@example.com");
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);
        org.springframework.security.core.context.SecurityContext securityContext = mock(org.springframework.security.core.context.SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        assertThrows(UsernameNotFoundException.class, () -> userService.getCurrentAuthenticatedUser());
    }

    @Test
    void getUserById_success() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Optional<User> result = userService.getUserById(1L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void getUserById_notFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        Optional<User> result = userService.getUserById(2L);
        assertFalse(result.isPresent());
    }
}
