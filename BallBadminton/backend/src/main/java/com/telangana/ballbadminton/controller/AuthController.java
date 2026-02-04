package com.telangana.ballbadminton.controller;

import com.telangana.ballbadminton.dto.auth.LoginRequest;
import com.telangana.ballbadminton.dto.auth.LoginResponse;
import com.telangana.ballbadminton.dto.auth.LogoutRequest;
import com.telangana.ballbadminton.dto.auth.RefreshTokenRequest;
import com.telangana.ballbadminton.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Authentication controller for handling login, logout, and token refresh
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * User login endpoint
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "403", description = "Account disabled"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request
    ) {
        try {
            logger.info("Login request from IP: {} for user: {}", 
                    getClientIpAddress(request), loginRequest.getUsernameOrEmail());

            LoginResponse response = authenticationService.login(loginRequest);
            
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            logger.warn("Login failed - invalid credentials for user: {} from IP: {}", 
                    loginRequest.getUsernameOrEmail(), getClientIpAddress(request));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Invalid username/email or password"));

        } catch (DisabledException e) {
            logger.warn("Login failed - account disabled for user: {} from IP: {}", 
                    loginRequest.getUsernameOrEmail(), getClientIpAddress(request));
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse("Account is disabled"));

        } catch (Exception e) {
            logger.error("Login failed - unexpected error for user: {} from IP: {}: {}", 
                    loginRequest.getUsernameOrEmail(), getClientIpAddress(request), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Login failed. Please try again."));
        }
    }

    /**
     * Token refresh endpoint
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Generate new access token using refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refresh successful",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<?> refreshToken(
            @Valid @RequestBody RefreshTokenRequest refreshRequest,
            HttpServletRequest request
    ) {
        try {
            logger.debug("Token refresh request from IP: {}", getClientIpAddress(request));

            LoginResponse response = authenticationService.refreshToken(refreshRequest);
            
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            logger.warn("Token refresh failed - invalid token from IP: {}: {}", 
                    getClientIpAddress(request), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Invalid or expired refresh token"));

        } catch (DisabledException e) {
            logger.warn("Token refresh failed - account disabled from IP: {}", getClientIpAddress(request));
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse("Account is disabled"));

        } catch (Exception e) {
            logger.error("Token refresh failed - unexpected error from IP: {}: {}", 
                    getClientIpAddress(request), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Token refresh failed. Please try again."));
        }
    }

    /**
     * User logout endpoint
     */
    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout user and invalidate tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<?> logout(
            @Valid @RequestBody LogoutRequest logoutRequest,
            HttpServletRequest request
    ) {
        try {
            logger.debug("Logout request from IP: {}", getClientIpAddress(request));

            authenticationService.logout(logoutRequest.getRefreshToken());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Logout successful");
            response.put("timestamp", java.time.LocalDateTime.now());
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Logout failed from IP: {}: {}", getClientIpAddress(request), e.getMessage());
            // Always return success for logout to prevent information leakage
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Logout successful");
            response.put("timestamp", java.time.LocalDateTime.now());
            
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Check authentication status
     */
    @GetMapping("/status")
    @Operation(summary = "Check authentication status", description = "Verify if user is authenticated")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication status retrieved"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<?> getAuthStatus(HttpServletRequest request) {
        try {
            // This endpoint requires authentication, so if we reach here, user is authenticated
            Map<String, Object> response = new HashMap<>();
            response.put("authenticated", true);
            response.put("timestamp", java.time.LocalDateTime.now());
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error checking auth status from IP: {}: {}", getClientIpAddress(request), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Not authenticated"));
        }
    }

    /**
     * Create standardized error response
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", true);
        error.put("message", message);
        error.put("timestamp", java.time.LocalDateTime.now());
        return error;
    }

    /**
     * Get client IP address from request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}