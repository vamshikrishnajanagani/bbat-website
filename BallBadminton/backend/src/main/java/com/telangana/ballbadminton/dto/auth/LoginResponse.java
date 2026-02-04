package com.telangana.ballbadminton.dto.auth;

import com.telangana.ballbadminton.entity.Role;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Login response DTO containing authentication tokens and user information
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private long expiresIn;
    private UserInfo user;

    // Constructors
    public LoginResponse() {}

    public LoginResponse(String accessToken, String refreshToken, long expiresIn, UserInfo user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.user = user;
    }

    // Getters and Setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    /**
     * Nested class for user information in login response
     */
    public static class UserInfo {
        private UUID id;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private Set<Role> roles;
        private LocalDateTime lastLogin;
        private boolean emailVerified;

        // Constructors
        public UserInfo() {}

        public UserInfo(UUID id, String username, String email, String firstName, String lastName, Set<Role> roles, LocalDateTime lastLogin, boolean emailVerified) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.roles = roles;
            this.lastLogin = lastLogin;
            this.emailVerified = emailVerified;
        }

        // Getters and Setters
        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public Set<Role> getRoles() {
            return roles;
        }

        public void setRoles(Set<Role> roles) {
            this.roles = roles;
        }

        public LocalDateTime getLastLogin() {
            return lastLogin;
        }

        public void setLastLogin(LocalDateTime lastLogin) {
            this.lastLogin = lastLogin;
        }

        public boolean isEmailVerified() {
            return emailVerified;
        }

        public void setEmailVerified(boolean emailVerified) {
            this.emailVerified = emailVerified;
        }

        public String getFullName() {
            return firstName + " " + lastName;
        }
    }
}