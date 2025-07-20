package com.lecturebot.controller;

import com.lecturebot.generated.model.LoginRequest;
import com.lecturebot.generated.model.LoginResponse;
import com.lecturebot.generated.model.RegisterRequest;
import com.lecturebot.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {
    @Mock
    private UserService userService;
    @InjectMocks
    private AuthController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loginUser_success() {
        LoginRequest req = new LoginRequest().email("test@example.com").password("pass");
        LoginResponse resp = new LoginResponse().token("token123");
        when(userService.loginUser(req)).thenReturn(resp);
        ResponseEntity<LoginResponse> response = controller.loginUser(req);
        assertEquals(org.springframework.http.HttpStatus.OK, response.getStatusCode());
        assertEquals(resp, response.getBody());
    }

    @Test
    void loginUser_failure_returnsBadRequest() {
        LoginRequest req = new LoginRequest().email("test@example.com").password("bad");
        when(userService.loginUser(req)).thenThrow(new RuntimeException("Invalid credentials"));
        ResponseEntity<LoginResponse> response = controller.loginUser(req);
        assertEquals(org.springframework.http.HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody().getToken());
    }

    @Test
    void registerUser_success() {
        RegisterRequest req = new RegisterRequest().email("test@example.com").password("pass").name("Test");
        ResponseEntity<String> response = controller.registerUser(req);
        assertEquals(org.springframework.http.HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Registration successful"));
    }

    @Test
    void registerUser_failure_returnsBadRequest() {
        RegisterRequest req = new RegisterRequest().email("test@example.com").password("pass").name("Test");
        doThrow(new RuntimeException("User already exists")).when(userService).registerUser(req);
        ResponseEntity<String> response = controller.registerUser(req);
        assertEquals(org.springframework.http.HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User already exists", response.getBody());
    }
}
