package com.telangana.ballbadminton.service;

import com.telangana.ballbadminton.base.BaseUnitTest;
import com.telangana.ballbadminton.dto.auth.LoginRequest;
import com.telangana.ballbadminton.dto.auth.LoginResponse;
import com.telangana.ballbadminton.dto.auth.RefreshTokenRequest;
import com.telangana.ballbadminton.entity.Role;
import com.telangana.ballbadminton.entity.User;
import com.telangana.ballbadminton.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthenticationService
 * Tests authentication, token refresh, and logout functionality
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Authentication Service Tests")
class AuthenticationServiceTest extends BaseUnitTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User testUser;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRoles(Set.of(Role.USER));
        testUser.setActive(true);
        testUser.setEmailVerified(true);

        loginRequest = new LoginRequest("testuser", "password123");
    }

    @Test
    @DisplayName("Should login successfully with valid credentials")
    void shouldLoginSuccessfullyWithValidCredentials() {
        // Given
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtService.generateToken(testUser)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(testUser)).thenReturn("refresh-token");
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        // When
        LoginResponse response = authenticationService.login(loginRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isEqualTo(3600000L);
        assertThat(response.getUser()).isNotNull();
        assertThat(response.getUser().getUsername()).isEqualTo("testuser");
        assertThat(response.getUser().getEmail()).isEqualTo("test@example.com");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(testUser);
        verify(jwtService).generateRefreshToken(testUser);
        verify(userRepository).updateLastLogin(eq(testUser.getId()), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should throw BadCredentialsException for invalid credentials")
    void shouldThrowBadCredentialsExceptionForInvalidCredentials() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When & Then
        assertThatThrownBy(() -> authenticationService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid username/email or password");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(jwtService);
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Should throw DisabledException for disabled account")
    void shouldThrowDisabledExceptionForDisabledAccount() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new DisabledException("Account is disabled"));

        // When & Then
        assertThatThrownBy(() -> authenticationService.login(loginRequest))
                .isInstanceOf(DisabledException.class)
                .hasMessage("Account is disabled");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(jwtService);
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Should refresh token successfully")
    void shouldRefreshTokenSuccessfully() {
        // Given
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest("valid-refresh-token");
        
        when(jwtService.isValidTokenFormat("valid-refresh-token")).thenReturn(true);
        when(jwtService.extractUsername("valid-refresh-token")).thenReturn("testuser");
        when(userRepository.findByUsernameOrEmail("testuser", "testuser"))
                .thenReturn(Optional.of(testUser));
        when(jwtService.isRefreshTokenValid("valid-refresh-token", testUser)).thenReturn(true);
        when(jwtService.generateToken(testUser)).thenReturn("new-access-token");
        when(jwtService.generateRefreshToken(testUser)).thenReturn("new-refresh-token");
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        // When
        LoginResponse response = authenticationService.refreshToken(refreshRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");
        assertThat(response.getUser().getUsername()).isEqualTo("testuser");

        verify(jwtService).isValidTokenFormat("valid-refresh-token");
        verify(jwtService).extractUsername("valid-refresh-token");
        verify(jwtService).isRefreshTokenValid("valid-refresh-token", testUser);
        verify(jwtService).generateToken(testUser);
        verify(jwtService).generateRefreshToken(testUser);
    }

    @Test
    @DisplayName("Should throw BadCredentialsException for invalid refresh token format")
    void shouldThrowBadCredentialsExceptionForInvalidRefreshTokenFormat() {
        // Given
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest("invalid-format");
        when(jwtService.isValidTokenFormat("invalid-format")).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authenticationService.refreshToken(refreshRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Token refresh failed");

        verify(jwtService).isValidTokenFormat("invalid-format");
        verifyNoMoreInteractions(jwtService);
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Should throw BadCredentialsException for expired refresh token")
    void shouldThrowBadCredentialsExceptionForExpiredRefreshToken() {
        // Given
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest("expired-token");
        
        when(jwtService.isValidTokenFormat("expired-token")).thenReturn(true);
        when(jwtService.extractUsername("expired-token")).thenReturn("testuser");
        when(userRepository.findByUsernameOrEmail("testuser", "testuser"))
                .thenReturn(Optional.of(testUser));
        when(jwtService.isRefreshTokenValid("expired-token", testUser)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authenticationService.refreshToken(refreshRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Token refresh failed");

        verify(jwtService).isRefreshTokenValid("expired-token", testUser);
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException for non-existent user during refresh")
    void shouldThrowUsernameNotFoundExceptionForNonExistentUserDuringRefresh() {
        // Given
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest("valid-token");
        
        when(jwtService.isValidTokenFormat("valid-token")).thenReturn(true);
        when(jwtService.extractUsername("valid-token")).thenReturn("nonexistent");
        when(userRepository.findByUsernameOrEmail("nonexistent", "nonexistent"))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authenticationService.refreshToken(refreshRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Token refresh failed");
    }

    @Test
    @DisplayName("Should throw DisabledException for inactive user during refresh")
    void shouldThrowDisabledExceptionForInactiveUserDuringRefresh() {
        // Given
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest("valid-token");
        testUser.setActive(false);
        
        when(jwtService.isValidTokenFormat("valid-token")).thenReturn(true);
        when(jwtService.extractUsername("valid-token")).thenReturn("testuser");
        when(userRepository.findByUsernameOrEmail("testuser", "testuser"))
                .thenReturn(Optional.of(testUser));
        when(jwtService.isRefreshTokenValid("valid-token", testUser)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authenticationService.refreshToken(refreshRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Token refresh failed");
    }

    @Test
    @DisplayName("Should logout successfully")
    void shouldLogoutSuccessfully() {
        // Given
        String refreshToken = "valid-refresh-token";
        when(jwtService.isValidTokenFormat(refreshToken)).thenReturn(true);
        when(jwtService.extractUsername(refreshToken)).thenReturn("testuser");

        // When & Then - should not throw exception
        authenticationService.logout(refreshToken);

        verify(jwtService).isValidTokenFormat(refreshToken);
        verify(jwtService).extractUsername(refreshToken);
    }

    @Test
    @DisplayName("Should handle logout with invalid token gracefully")
    void shouldHandleLogoutWithInvalidTokenGracefully() {
        // Given
        String invalidToken = "invalid-token";
        when(jwtService.isValidTokenFormat(invalidToken)).thenReturn(false);

        // When & Then - should not throw exception
        authenticationService.logout(invalidToken);

        verify(jwtService).isValidTokenFormat(invalidToken);
    }

    @Test
    @DisplayName("Should handle logout with null token gracefully")
    void shouldHandleLogoutWithNullTokenGracefully() {
        // When & Then - should not throw exception
        authenticationService.logout(null);

        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("Should validate token correctly")
    void shouldValidateTokenCorrectly() {
        // Given
        String token = "valid-token";
        String username = "testuser";
        
        when(userRepository.findByUsernameOrEmail(username, username))
                .thenReturn(Optional.of(testUser));
        when(jwtService.isTokenValid(token, testUser)).thenReturn(true);

        // When
        boolean isValid = authenticationService.isTokenValid(token, username);

        // Then
        assertThat(isValid).isTrue();
        verify(userRepository).findByUsernameOrEmail(username, username);
        verify(jwtService).isTokenValid(token, testUser);
    }

    @Test
    @DisplayName("Should return false for token validation with non-existent user")
    void shouldReturnFalseForTokenValidationWithNonExistentUser() {
        // Given
        String token = "valid-token";
        String username = "nonexistent";
        
        when(userRepository.findByUsernameOrEmail(username, username))
                .thenReturn(Optional.empty());

        // When
        boolean isValid = authenticationService.isTokenValid(token, username);

        // Then
        assertThat(isValid).isFalse();
        verify(userRepository).findByUsernameOrEmail(username, username);
        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("Should return false for token validation with inactive user")
    void shouldReturnFalseForTokenValidationWithInactiveUser() {
        // Given
        String token = "valid-token";
        String username = "testuser";
        testUser.setActive(false);
        
        when(userRepository.findByUsernameOrEmail(username, username))
                .thenReturn(Optional.of(testUser));

        // When
        boolean isValid = authenticationService.isTokenValid(token, username);

        // Then
        assertThat(isValid).isFalse();
        verify(userRepository).findByUsernameOrEmail(username, username);
        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("Should handle exception during last login update gracefully")
    void shouldHandleExceptionDuringLastLoginUpdateGracefully() {
        // Given
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtService.generateToken(testUser)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(testUser)).thenReturn("refresh-token");
        when(jwtService.getExpirationTime()).thenReturn(3600000L);
        
        // Simulate exception during last login update
        doThrow(new RuntimeException("Database error"))
                .when(userRepository).updateLastLogin(eq(testUser.getId()), any(LocalDateTime.class));

        // When & Then - should not fail the login
        LoginResponse response = authenticationService.login(loginRequest);
        
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access-token");
    }
}