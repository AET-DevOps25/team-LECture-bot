package com.lecturebot.service;

import com.lecturebot.dto.RegisterRequest; //
import com.lecturebot.entity.User; //
import com.lecturebot.repository.UserRepository; //
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // Import PasswordEncoder
import org.springframework.stereotype.Service;

// Assuming LoginRequest and LoginResponse DTOs exist for later use
import com.lecturebot.dto.LoginRequest;
import com.lecturebot.dto.LoginResponse;


import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository; //
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(RegisterRequest request) { //
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        User user = new User(); //
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        // Hash the password before saving
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        
        // return userRepository.save(user); 
        System.out.println("Hashed password for " + request.getEmail() + ": " + user.getPasswordHash());
        return user; // Returning the user object with hashed password (not yet saved)
    }

    public LoginResponse loginUser(LoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Use passwordEncoder.matches() for comparison
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
                // Passwords match, successful login
                System.out.println("Login successful for (conceptual): " + user.getEmail());
                return new LoginResponse("dummy-token-replace-in-sub-issue-5", "Login successful");
            } else {
                // Invalid password
                throw new RuntimeException("Invalid email or password");
            }
        } else {
            // User not found
            throw new RuntimeException("Invalid email or password");
        }
    }
}