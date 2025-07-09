package com.lecturebot.controller;

import com.lecturebot.generated.api.ProfileApi;
import com.lecturebot.generated.model.ChangePasswordRequest;
import com.lecturebot.generated.model.UpdateUserProfileRequest;
import com.lecturebot.generated.model.UserProfile;
import com.lecturebot.generated.model.UpdateUserProfileResponse;
import com.lecturebot.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder; // Added import
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ProfileController implements ProfileApi {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseEntity<UserProfile> getUserProfile() { // Removed UserDetails parameter
        // Access user details from SecurityContextHolder
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserProfile userProfile = userService.getUserProfile(username);
        return ResponseEntity.ok(userProfile);
    }

    @Override
    public ResponseEntity<UpdateUserProfileResponse> updateUserProfile(
            @Valid UpdateUserProfileRequest updateUserProfileRequest) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
        boolean emailChanged = !username.equals(updateUserProfileRequest.getEmail());
        UserProfile updatedProfile = userService.updateUserProfile(username, updateUserProfileRequest);

        UpdateUserProfileResponse response = new UpdateUserProfileResponse()
            .userProfile(updatedProfile)
            .requireReauth(emailChanged)
            .message(emailChanged ? "Email changed. Please log in again." : "Profile updated successfully.");

        return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // Or return a more specific error response
        }
    }

    @Override
    public ResponseEntity<Void> changePassword(
            @Valid ChangePasswordRequest changePasswordRequest) {

        // Access username from SecurityContextHolder
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            userService.changePassword(
                    username,
                    changePasswordRequest.getOldPassword(),
                    changePasswordRequest.getNewPassword()
            );
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // Or return a more specific error response
        }
    }
}
