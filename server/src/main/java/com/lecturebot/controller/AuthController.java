package com.lecturebot.controller;

import com.lecturebot.dto.LoginRequest;
import com.lecturebot.dto.LoginResponse;
// Import registration DTO and User entity later
// import com.lecturebot.dto.RegisterRequest;
// import com.lecturebot.entity.User;
import com.lecturebot.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth") // Common base path for authentication endpoints
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse loginResponse = userService.loginUser(loginRequest);
            return ResponseEntity.ok(loginResponse);
        } catch (RuntimeException e) {
            // Basic error handling, improve later with global exception handler and specific exceptions
            return ResponseEntity.badRequest().body(new LoginResponse(e.getMessage()));
        }
    }

    // Placeholder for registration endpoint - you'll need this soon
    /*
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            // Create User object from RegisterRequest
            User newUser = new User(registerRequest.getEmail(), null, registerRequest.getName());
            // The password will be hashed and set by the service
            User registeredUser = userService.registerUser(newUser, registerRequest.getPassword());
            return ResponseEntity.ok(new LoginResponse("User registered successfully!")); // Or return UserDto
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new LoginResponse(e.getMessage()));
        }
    }
    */
}
