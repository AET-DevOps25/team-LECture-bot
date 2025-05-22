package com.lecturebot.service;

import com.lecturebot.dto.LoginRequest;
import com.lecturebot.dto.LoginResponse;
import com.lecturebot.dto.UserDto;
import com.lecturebot.entity.User;
import com.lecturebot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
// Import Spring Security's PasswordEncoder and AuthenticationManager later
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    // private final PasswordEncoder passwordEncoder; // Inject later for hashing
    // private final AuthenticationManager authenticationManager; // Inject later for Spring Security auth flow
    // private final JwtTokenProvider jwtTokenProvider; // Inject later for JWT

    @Autowired
    public UserService(UserRepository userRepository /*, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider */) {
        this.userRepository = userRepository;
        // this.passwordEncoder = passwordEncoder;
        // this.authenticationManager = authenticationManager;
        // this.jwtTokenProvider = jwtTokenProvider;
    }

    // Placeholder for registration - you'll need this soon
    public User registerUser(User userToRegister, String rawPassword) {
        if (userRepository.existsByEmail(userToRegister.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!"); // Create custom exceptions later
        }
        // **IMPORTANT: Hash the password before saving!**
        // userToRegister.setPasswordHash(passwordEncoder.encode(rawPassword));
        userToRegister.setPasswordHash(rawPassword); // TEMPORARY: Plain text password
        return userRepository.save(userToRegister);
    }


    public LoginResponse loginUser(LoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());

        if (userOptional.isEmpty()) {
            // Consider a more generic error message for security ("Invalid credentials")
            throw new RuntimeException("Error: User not found with email: " + loginRequest.getEmail());
        }

        User user = userOptional.get();

        // **IMPORTANT: This is a PLAIN TEXT password comparison - NOT SECURE!**
        // **Replace with passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash()) when hashing is implemented.**
        if (!user.getPasswordHash().equals(loginRequest.getPassword())) {
            // Consider a more generic error message
            throw new RuntimeException("Error: Invalid password.");
        }

        // If using Spring Security's AuthenticationManager flow (recommended):
        // Authentication authentication = authenticationManager.authenticate(
        //         new UsernamePasswordAuthenticationToken(
        //                 loginRequest.getEmail(),
        //                 loginRequest.getPassword()
        //         )
        // );
        // SecurityContextHolder.getContext().setAuthentication(authentication);
        // String jwt = jwtTokenProvider.generateToken(authentication);
        // UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        // UserDto userDto = new UserDto(userDetails.getId(), userDetails.getEmail(), userDetails.getName());
        // return new LoginResponse("User logged in successfully!", jwt, userDto);


        // For this simplified version:
        UserDto userDto = new UserDto(user.getId(), user.getEmail(), user.getName());
        // Placeholder for token
        String placeholderToken = "placeholder-jwt-token-for-" + user.getEmail();
        return new LoginResponse("Login successful (SIMPLIFIED - NO JWT, NO HASHING!)", placeholderToken, userDto);
    }
}
