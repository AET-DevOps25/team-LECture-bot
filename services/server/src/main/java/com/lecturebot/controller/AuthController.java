package com.lecturebot.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.lecturebot.generated.api.AuthenticationApi;
import com.lecturebot.generated.model.LoginResponse;
import com.lecturebot.generated.model.RegisterRequest;
import com.lecturebot.service.UserService;

@RestController
public class AuthController implements AuthenticationApi { // RequestMapping is now handled by the global context-path

    private final UserService userService; 

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody com.lecturebot.generated.model.LoginRequest loginRequest) {
        try {
            LoginResponse loginResponse = userService.loginUser(loginRequest);
            return ResponseEntity.ok(loginResponse);
        } catch (RuntimeException e) {
            // Return a more structured error if LoginResponse can accommodate it
            return ResponseEntity.badRequest().body(new LoginResponse().token(e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequest request) { 
        try {
            userService.registerUser(request);
            return ResponseEntity.ok("Registration successful for " + request.getEmail());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
