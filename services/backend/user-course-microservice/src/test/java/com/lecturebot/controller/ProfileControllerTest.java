package com.lecturebot.controller;

import com.lecturebot.generated.model.ChangePasswordRequest;
import com.lecturebot.generated.model.UpdateUserProfileRequest;
import com.lecturebot.generated.model.UserProfile;
import com.lecturebot.generated.model.UpdateUserProfileResponse;
import com.lecturebot.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private ProfileController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser@example.com");
    }

    @Test
    void getUserProfile_success() {
        UserProfile profile = new UserProfile().id(1L).email("testuser@example.com").name("Test User");
        when(userService.getUserProfile("testuser@example.com")).thenReturn(profile);
        ResponseEntity<UserProfile> response = controller.getUserProfile();
        assertEquals(org.springframework.http.HttpStatus.OK, response.getStatusCode());
        assertEquals(profile, response.getBody());
    }

    @Test
    void getUserProfile_userServiceThrows_exceptionPropagated() {
        when(userService.getUserProfile("testuser@example.com")).thenThrow(new RuntimeException("User not found"));
        assertThrows(RuntimeException.class, () -> controller.getUserProfile());
    }

    @Test
    void updateUserProfile_success_noEmailChange() {
        UpdateUserProfileRequest req = new UpdateUserProfileRequest().email("testuser@example.com").name("Test User");
        UserProfile updated = new UserProfile().id(1L).email("testuser@example.com").name("Test User");
        when(userService.updateUserProfile(eq("testuser@example.com"), eq(req))).thenReturn(updated);
        ResponseEntity<UpdateUserProfileResponse> response = controller.updateUserProfile(req);
        assertEquals(org.springframework.http.HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(updated, response.getBody().getUserProfile());
        assertFalse(response.getBody().getRequireReauth());
    }

    @Test
    void updateUserProfile_success_emailChanged() {
        UpdateUserProfileRequest req = new UpdateUserProfileRequest().email("new@example.com").name("Test User");
        UserProfile updated = new UserProfile().id(1L).email("new@example.com").name("Test User");
        when(userService.updateUserProfile(eq("testuser@example.com"), eq(req))).thenReturn(updated);
        ResponseEntity<UpdateUserProfileResponse> response = controller.updateUserProfile(req);
        assertEquals(org.springframework.http.HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(updated, response.getBody().getUserProfile());
        assertTrue(response.getBody().getRequireReauth());
    }

    @Test
    void updateUserProfile_illegalArgument_returnsBadRequest() {
        UpdateUserProfileRequest req = new UpdateUserProfileRequest().email("bad@example.com");
        when(userService.updateUserProfile(anyString(), any())).thenThrow(new IllegalArgumentException());
        ResponseEntity<UpdateUserProfileResponse> response = controller.updateUserProfile(req);
        assertEquals(org.springframework.http.HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void changePassword_success() {
        ChangePasswordRequest req = new ChangePasswordRequest().oldPassword("old").newPassword("new");
        doNothing().when(userService).changePassword(eq("testuser@example.com"), eq("old"), eq("new"));
        ResponseEntity<Void> response = controller.changePassword(req);
        assertEquals(org.springframework.http.HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void changePassword_illegalArgument_returnsBadRequest() {
        ChangePasswordRequest req = new ChangePasswordRequest().oldPassword("old").newPassword("new");
        doThrow(new IllegalArgumentException()).when(userService).changePassword(anyString(), anyString(), anyString());
        ResponseEntity<Void> response = controller.changePassword(req);
        assertEquals(org.springframework.http.HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
