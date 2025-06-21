package com.lecturebot.controller;

import com.lecturebot.dto.UpdateUserProfileRequest;
import com.lecturebot.dto.ChangePasswordRequest;
import com.lecturebot.entity.User;
import com.lecturebot.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class ProfileController {
    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        User user = userService.findByEmail(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        // Optionally, return a DTO instead of the full User entity
        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(
            @Valid @RequestBody UpdateUserProfileRequest request,
            Principal principal
    ) {
        String oldEmail = principal.getName();
        User updatedUser = userService.updateUserProfile(oldEmail, request);
        boolean emailChanged = !oldEmail.equals(request.getEmail());

        if (emailChanged) {
            return ResponseEntity.ok(Map.of(
                "message", "Email changed. Please log in again.",
                "requireReauth", true
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                "message", "Profile updated successfully",
                "requireReauth", false,
                "user", updatedUser
            ));
        }
    }

    @PostMapping("/me/change-password")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        userService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.ok().build();
    }
}
