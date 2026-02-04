package com.telangana.ballbadminton.dto.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * Logout request DTO for user logout
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
public class LogoutRequest {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;

    private boolean logoutFromAllDevices = false;

    // Constructors
    public LogoutRequest() {}

    public LogoutRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public LogoutRequest(String refreshToken, boolean logoutFromAllDevices) {
        this.refreshToken = refreshToken;
        this.logoutFromAllDevices = logoutFromAllDevices;
    }

    // Getters and Setters
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public boolean isLogoutFromAllDevices() {
        return logoutFromAllDevices;
    }

    public void setLogoutFromAllDevices(boolean logoutFromAllDevices) {
        this.logoutFromAllDevices = logoutFromAllDevices;
    }

    @Override
    public String toString() {
        return "LogoutRequest{" +
                "logoutFromAllDevices=" + logoutFromAllDevices +
                '}';
    }
}