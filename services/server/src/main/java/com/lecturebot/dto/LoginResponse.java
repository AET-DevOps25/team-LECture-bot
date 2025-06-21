package com.lecturebot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String message;
    private String token; // For JWT later
    private UserDto user; // Optional: send some user details back

    // Constructor for simple message
    public LoginResponse(String message) {
        this.message = message;
    }

    // Constructor for message and token
    public LoginResponse(String token, String message) {
        this.message = message;
        this.token = token;
    }
}
