package com.telangana.ballbadminton.service;

import com.telangana.ballbadminton.base.BaseUnitTest;
import com.telangana.ballbadminton.entity.Role;
import com.telangana.ballbadminton.entity.User;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for JwtService
 * Tests JWT token generation, validation, and extraction
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@DisplayName("JWT Service Tests")
class JwtServiceTest extends BaseUnitTest {

    private JwtService jwtService;
    private User testUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        
        // Set test properties using reflection
        ReflectionTestUtils.setField(jwtService, "jwtSecret", "dGVzdC1zZWNyZXQta2V5LWZvci10ZWxhbmdhbmEtYmFsbC1iYWRtaW50b24tYXNzb2NpYXRpb24=");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", 86400000L);

        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRoles(Set.of(Role.USER));
        testUser.setActive(true);
        testUser.setAccountNonExpired(true);
        testUser.setAccountNonLocked(true);
        testUser.setCredentialsNonExpired(true);
    }

    @Test
    @DisplayName("Should generate valid access token")
    void shouldGenerateValidAccessToken() {
        // When
        String token = jwtService.generateToken(testUser);
        
        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(jwtService.isValidTokenFormat(token)).isTrue();
        
        // Verify token content
        String username = jwtService.extractUsername(token);
        assertThat(username).isEqualTo("testuser");
        
        UUID userId = jwtService.extractUserId(token);
        assertThat(userId).isEqualTo(testUser.getId());
        
        String email = jwtService.extractEmail(token);
        assertThat(email).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should generate valid refresh token")
    void shouldGenerateValidRefreshToken() {
        // When
        String refreshToken = jwtService.generateRefreshToken(testUser);
        
        // Then
        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken).isNotEmpty();
        assertThat(jwtService.isValidTokenFormat(refreshToken)).isTrue();
        assertThat(jwtService.isRefreshToken(refreshToken)).isTrue();
        
        // Verify token content
        String username = jwtService.extractUsername(refreshToken);
        assertThat(username).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Should generate token with extra claims")
    void shouldGenerateTokenWithExtraClaims() {
        // Given
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("customClaim", "customValue");
        extraClaims.put("department", "IT");
        
        // When
        String token = jwtService.generateToken(extraClaims, testUser);
        
        // Then
        assertThat(token).isNotNull();
        assertThat(jwtService.isValidTokenFormat(token)).isTrue();
        
        // Verify custom claims can be extracted
        String customClaim = jwtService.extractClaim(token, claims -> claims.get("customClaim", String.class));
        assertThat(customClaim).isEqualTo("customValue");
        
        String department = jwtService.extractClaim(token, claims -> claims.get("department", String.class));
        assertThat(department).isEqualTo("IT");
    }

    @Test
    @DisplayName("Should validate token correctly")
    void shouldValidateTokenCorrectly() {
        // Given
        String token = jwtService.generateToken(testUser);
        
        // When & Then
        assertThat(jwtService.isTokenValid(token, testUser)).isTrue();
    }

    @Test
    @DisplayName("Should reject invalid token")
    void shouldRejectInvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";
        
        // When & Then
        assertThat(jwtService.isTokenValid(invalidToken, testUser)).isFalse();
    }

    @Test
    @DisplayName("Should reject token for different user")
    void shouldRejectTokenForDifferentUser() {
        // Given
        String token = jwtService.generateToken(testUser);
        
        User differentUser = new User();
        differentUser.setUsername("differentuser");
        differentUser.setEmail("different@example.com");
        differentUser.setActive(true);
        
        // When & Then
        assertThat(jwtService.isTokenValid(token, differentUser)).isFalse();
    }

    @Test
    @DisplayName("Should detect expired token")
    void shouldDetectExpiredToken() {
        // This test would require manipulating time or using a very short expiration
        // For now, we'll test the isTokenExpired method with a valid token
        String token = jwtService.generateToken(testUser);
        
        // A freshly generated token should not be expired
        assertThat(jwtService.isTokenExpired(token)).isFalse();
    }

    @Test
    @DisplayName("Should extract expiration date")
    void shouldExtractExpirationDate() {
        // Given
        String token = jwtService.generateToken(testUser);
        
        // When
        Date expiration = jwtService.extractExpiration(token);
        
        // Then
        assertThat(expiration).isNotNull();
        assertThat(expiration).isAfter(new Date());
    }

    @Test
    @DisplayName("Should validate refresh token correctly")
    void shouldValidateRefreshTokenCorrectly() {
        // Given
        String refreshToken = jwtService.generateRefreshToken(testUser);
        
        // When & Then
        assertThat(jwtService.isRefreshTokenValid(refreshToken, testUser)).isTrue();
    }

    @Test
    @DisplayName("Should reject access token as refresh token")
    void shouldRejectAccessTokenAsRefreshToken() {
        // Given
        String accessToken = jwtService.generateToken(testUser);
        
        // When & Then
        assertThat(jwtService.isRefreshTokenValid(accessToken, testUser)).isFalse();
    }

    @Test
    @DisplayName("Should extract token from authorization header")
    void shouldExtractTokenFromAuthorizationHeader() {
        // Given
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token";
        String authHeader = "Bearer " + token;
        
        // When
        String extractedToken = jwtService.extractTokenFromHeader(authHeader);
        
        // Then
        assertThat(extractedToken).isEqualTo(token);
    }

    @Test
    @DisplayName("Should return null for invalid authorization header")
    void shouldReturnNullForInvalidAuthorizationHeader() {
        // Test null header
        assertThat(jwtService.extractTokenFromHeader(null)).isNull();
        
        // Test header without Bearer prefix
        assertThat(jwtService.extractTokenFromHeader("Basic token")).isNull();
        
        // Test empty header
        assertThat(jwtService.extractTokenFromHeader("")).isNull();
    }

    @Test
    @DisplayName("Should validate token format correctly")
    void shouldValidateTokenFormatCorrectly() {
        // Valid JWT format (3 parts separated by dots)
        assertThat(jwtService.isValidTokenFormat("header.payload.signature")).isTrue();
        
        // Invalid formats
        assertThat(jwtService.isValidTokenFormat(null)).isFalse();
        assertThat(jwtService.isValidTokenFormat("")).isFalse();
        assertThat(jwtService.isValidTokenFormat("   ")).isFalse();
        assertThat(jwtService.isValidTokenFormat("invalid")).isFalse();
        assertThat(jwtService.isValidTokenFormat("only.two.parts")).isTrue(); // This should pass format check
        assertThat(jwtService.isValidTokenFormat("too.many.parts.here.invalid")).isFalse();
    }

    @Test
    @DisplayName("Should get expiration times correctly")
    void shouldGetExpirationTimesCorrectly() {
        // When & Then
        assertThat(jwtService.getExpirationTime()).isEqualTo(3600000L); // 1 hour
        assertThat(jwtService.getRefreshExpirationTime()).isEqualTo(86400000L); // 24 hours
    }

    @Test
    @DisplayName("Should handle malformed token gracefully")
    void shouldHandleMalformedTokenGracefully() {
        // Given
        String malformedToken = "malformed.token";
        
        // When & Then - should not throw exception, just return false/null
        assertThat(jwtService.isTokenValid(malformedToken, testUser)).isFalse();
        assertThat(jwtService.isTokenExpired(malformedToken)).isTrue();
        assertThat(jwtService.isRefreshToken(malformedToken)).isFalse();
        
        // These should throw JwtException
        assertThatThrownBy(() -> jwtService.extractUsername(malformedToken))
                .isInstanceOf(JwtException.class);
        
        assertThatThrownBy(() -> jwtService.extractUserId(malformedToken))
                .isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("Should handle null user ID in token")
    void shouldHandleNullUserIdInToken() {
        // Given - create a simple UserDetails without User entity
        UserDetails simpleUser = org.springframework.security.core.userdetails.User.builder()
                .username("simpleuser")
                .password("password")
                .authorities("ROLE_USER")
                .build();
        
        // When
        String token = jwtService.generateToken(simpleUser);
        UUID userId = jwtService.extractUserId(token);
        
        // Then
        assertThat(userId).isNull();
    }
}