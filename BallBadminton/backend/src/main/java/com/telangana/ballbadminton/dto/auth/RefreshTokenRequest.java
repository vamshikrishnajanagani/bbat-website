package com.telangana.ballbadminton.dto.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * Refresh token request DTO for token renewal
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;

    // Constructors
    public RefreshTokenRequest() {}

    public RefreshTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    // Getters and Setters
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public String toString() {
        return "RefreshTokenRequest{refreshToken='[PROTECTED]'}";
    }
}