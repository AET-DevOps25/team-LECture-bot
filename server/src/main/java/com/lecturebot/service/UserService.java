package com.lecturebot.service;

import com.lecturebot.dto.RegisterRequest; // Import RegisterRequest
import com.lecturebot.entity.User;
import com.lecturebot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User registerUser(RegisterRequest request) { // RegisterRequest is now recognized
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        User user = new User(); // This relies on @NoArgsConstructor from Lombok in User.java
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash("temporary_plain_password"); // Placeholder for now

        System.out.println("UserService.registerUser called for: " + request.getEmail() + ". DB save is conceptual for this scope.");
        return user; // Return the user object (not yet saved for this scope)
    }
}