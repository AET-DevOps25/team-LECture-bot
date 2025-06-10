package com.lecturebot.service;

import com.lecturebot.dto.RegisterRequest;
import com.lecturebot.dto.UpdateUserProfileRequest;
import com.lecturebot.entity.User;
import com.lecturebot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
                // Passwords match, successful login
                // TODO: Generate and return a JWT token (can be a separate sub-task or part of full login feature)
                String token = "dummy-jwt-token-replace-me"; // Replace with actual token generation logic
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

    // Update the authenticated user's profile (name, email)
    @Transactional
    public User updateUserProfile(String email, com.lecturebot.dto.UpdateUserProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // If email is being changed, check for uniqueness
        if (!user.getEmail().equals(request.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(request.getEmail());
        }
        user.setName(request.getName());
        return userRepository.save(user);
    }
}