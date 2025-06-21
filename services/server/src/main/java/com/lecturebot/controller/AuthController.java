package com.lecturebot.controller;

import com.lecturebot.dto.LoginRequest; //
import com.lecturebot.dto.LoginResponse; //
import com.lecturebot.dto.RegisterRequest; //
import com.lecturebot.service.UserService; //
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService; //

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) { //
        try {
            LoginResponse loginResponse = userService.loginUser(loginRequest); //
            return ResponseEntity.ok(loginResponse);
        } catch (RuntimeException e) {
            // Return a more structured error if LoginResponse can accommodate it
            return ResponseEntity.badRequest().body(new LoginResponse(null, e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request, BindingResult result) { //
        if (result.hasErrors()) {
            String errorMessage = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(errorMessage);
        }
        try {
            userService.registerUser(request);
            return ResponseEntity.ok("Registration successful for " + request.getEmail());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
