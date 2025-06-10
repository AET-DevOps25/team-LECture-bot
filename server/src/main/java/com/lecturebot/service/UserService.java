package com.lecturebot.service;

import com.lecturebot.dto.RegisterRequest;
import com.lecturebot.entity.User;
import com.lecturebot.repository.UserRepository;
import com.lecturebot.security.JwtTokenProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import Transactional

// Assuming LoginRequest and LoginResponse DTOs exist
import com.lecturebot.dto.LoginRequest;
import com.lecturebot.dto.LoginResponse;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional // Add Transactional annotation for operations that modify data
    public User registerUser(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        // Save the user to the database
        return userRepository.save(user); // This line is now active
    }

    @Transactional(readOnly = true) // Good practice for read operations
    public LoginResponse loginUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);

        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
                // Passwords match, successful login
                // TODO: Generate and return a JWT token (can be a separate sub-task or part of
                // full login feature)
                return new LoginResponse(token, "Login successful for " + user.getEmail());
            } else {
                // Invalid password
                throw new RuntimeException("Invalid email or password for user " + loginRequest.getEmail());
            }
        } else {
            // User not found
            throw new RuntimeException("User not found with email: " + loginRequest.getEmail());
        }
    }
}
