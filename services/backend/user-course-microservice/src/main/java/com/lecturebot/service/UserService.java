package com.lecturebot.service;

import com.lecturebot.entity.User;
import com.lecturebot.generated.model.LoginRequest;
import com.lecturebot.generated.model.LoginResponse;
import com.lecturebot.generated.model.RegisterRequest;
import com.lecturebot.generated.model.UpdateUserProfileRequest;
import com.lecturebot.generated.model.UserProfile;
import com.lecturebot.repository.UserRepository;
import com.lecturebot.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Added import
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public UserService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Registers a new user in the system.
     *
     * @param request The registration request containing user details.
     * @return The saved User entity.
     * @throws IllegalArgumentException if the email already exists.
     */
    public User registerUser(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Authenticates a user and returns a JWT.
     *
     * @param loginRequest The login request containing user credentials.
     * @return A LoginResponse containing the JWT.
     * @throws RuntimeException if authentication fails.
     */
    public LoginResponse loginUser(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtTokenProvider.generateToken(authentication);
            return new LoginResponse().token(jwt);
        } catch (AuthenticationException e) {
            // The controller will catch this and return an appropriate HTTP status
            throw new RuntimeException("Invalid credentials", e);
        }
    }

    /**
     * Retrieves the profile of a user by email.
     *
     * @param email The email of the user.
     * @return UserProfile DTO.
     * @throws UsernameNotFoundException if the user is not found.
     */
    public UserProfile getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return new UserProfile()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail());
    }

    /**
     * Updates the user's profile (name and/or email).
     *
     * @param oldEmail The current email of the user (used to find the user).
     * @param request  The request containing new name and email.
     * @return Updated UserProfile DTO.
     * @throws IllegalArgumentException  if the new email already exists for another
     *                                   user.
     * @throws UsernameNotFoundException if the user is not found.
     */
    public UserProfile updateUserProfile(String oldEmail, UpdateUserProfileRequest request) {
        User user = userRepository.findByEmail(oldEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + oldEmail));

        if (!oldEmail.equals(request.getEmail()) && userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("New email already exists");
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        User updatedUser = userRepository.save(user);
        return new UserProfile().id(updatedUser.getId()).name(updatedUser.getName()).email(updatedUser.getEmail());
    }

    public void changePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Old password does not match");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public User getCurrentAuthenticatedUser() {
        // Get the authentication object from the security context
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        // Look up the user in the database and return it
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
}
