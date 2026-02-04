package com.telangana.ballbadminton.service;

import com.telangana.ballbadminton.dto.auth.LoginRequest;
import com.telangana.ballbadminton.dto.auth.LoginResponse;
import com.telangana.ballbadminton.dto.auth.RefreshTokenRequest;
import com.telangana.ballbadminton.entity.User;
import com.telangana.ballbadminton.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Authentication service for handling user login, logout, and token refresh
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Service
@Transactional
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthenticationService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserRepository userRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    /**
     * Authenticate user and generate JWT tokens
     */
    public LoginResponse login(LoginRequest loginRequest) {
        logger.info("Login attempt for user: {}", loginRequest.getUsernameOrEmail());

        try {
            // Authenticate user credentials
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsernameOrEmail(),
                            loginRequest.getPassword()
                    )
            );

            User user = (User) authentication.getPrincipal();
            
            // Update last login time
            updateLastLogin(user);

            // Generate tokens
            String accessToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            // Create user info for response
            LoginResponse.UserInfo userInfo = createUserInfo(user);

            logger.info("Successful login for user: {} (ID: {})", user.getUsername(), user.getId());

            return new LoginResponse(
                    accessToken,
                    refreshToken,
                    jwtService.getExpirationTime(),
                    userInfo
            );

        } catch (BadCredentialsException e) {
            logger.warn("Invalid credentials for user: {}", loginRequest.getUsernameOrEmail());
            throw new BadCredentialsException("Invalid username/email or password");
        } catch (DisabledException e) {
            logger.warn("Disabled account login attempt for user: {}", loginRequest.getUsernameOrEmail());
            throw new DisabledException("Account is disabled");
        } catch (AuthenticationException e) {
            logger.warn("Authentication failed for user: {} - {}", loginRequest.getUsernameOrEmail(), e.getMessage());
            throw new BadCredentialsException("Authentication failed");
        }
    }

    /**
     * Refresh access token using refresh token
     */
    public LoginResponse refreshToken(RefreshTokenRequest refreshRequest) {
        logger.debug("Token refresh attempt");

        try {
            String refreshToken = refreshRequest.getRefreshToken();
            
            // Validate refresh token format
            if (!jwtService.isValidTokenFormat(refreshToken)) {
                throw new BadCredentialsException("Invalid refresh token format");
            }

            // Extract username from refresh token
            String username = jwtService.extractUsername(refreshToken);
            if (username == null) {
                throw new BadCredentialsException("Invalid refresh token");
            }

            // Load user details
            User user = userRepository.findByUsernameOrEmail(username, username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Validate refresh token
            if (!jwtService.isRefreshTokenValid(refreshToken, user)) {
                throw new BadCredentialsException("Invalid or expired refresh token");
            }

            // Check if user is still active
            if (!user.getActive()) {
                throw new DisabledException("User account is disabled");
            }

            // Generate new tokens
            String newAccessToken = jwtService.generateToken(user);
            String newRefreshToken = jwtService.generateRefreshToken(user);

            // Create user info for response
            LoginResponse.UserInfo userInfo = createUserInfo(user);

            logger.debug("Token refresh successful for user: {}", user.getUsername());

            return new LoginResponse(
                    newAccessToken,
                    newRefreshToken,
                    jwtService.getExpirationTime(),
                    userInfo
            );

        } catch (Exception e) {
            logger.warn("Token refresh failed: {}", e.getMessage());
            throw new BadCredentialsException("Token refresh failed");
        }
    }

    /**
     * Logout user (currently just validates the token)
     * In a production system, you might want to blacklist the token
     */
    public void logout(String refreshToken) {
        logger.debug("Logout attempt");

        try {
            if (refreshToken != null && jwtService.isValidTokenFormat(refreshToken)) {
                String username = jwtService.extractUsername(refreshToken);
                if (username != null) {
                    logger.info("User logged out: {}", username);
                    // In a production system, you might want to:
                    // 1. Add the token to a blacklist
                    // 2. Store logout event in audit log
                    // 3. Invalidate all sessions for the user if requested
                }
            }
        } catch (Exception e) {
            logger.warn("Error during logout: {}", e.getMessage());
            // Don't throw exception for logout - it should always succeed
        }
    }

    /**
     * Update user's last login timestamp
     */
    private void updateLastLogin(User user) {
        try {
            LocalDateTime now = LocalDateTime.now();
            userRepository.updateLastLogin(user.getId(), now);
            user.setLastLogin(now); // Update the entity for response
        } catch (Exception e) {
            logger.warn("Failed to update last login for user {}: {}", user.getUsername(), e.getMessage());
            // Don't fail authentication if we can't update last login
        }
    }

    /**
     * Create user info object for response
     */
    private LoginResponse.UserInfo createUserInfo(User user) {
        return new LoginResponse.UserInfo(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles(),
                user.getLastLogin(),
                user.getEmailVerified()
        );
    }

    /**
     * Validate if a token is valid for a user
     */
    public boolean isTokenValid(String token, String username) {
        try {
            User user = userRepository.findByUsernameOrEmail(username, username)
                    .orElse(null);
            
            if (user == null || !user.getActive()) {
                return false;
            }

            return jwtService.isTokenValid(token, user);
        } catch (Exception e) {
            logger.warn("Token validation failed for user {}: {}", username, e.getMessage());
            return false;
        }
    }
}