package com.telangana.ballbadminton.security;

import com.telangana.ballbadminton.base.BaseIntegrationTest;
import com.telangana.ballbadminton.dto.auth.LoginRequest;
import com.telangana.ballbadminton.dto.auth.LoginResponse;
import com.telangana.ballbadminton.entity.Permission;
import com.telangana.ballbadminton.entity.Role;
import com.telangana.ballbadminton.entity.User;
import com.telangana.ballbadminton.repository.UserRepository;
import com.telangana.ballbadminton.service.AuthenticationService;
import com.telangana.ballbadminton.service.JwtService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.quicktheories.core.Gen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.quicktheories.QuickTheory.qt;
import static org.quicktheories.generators.SourceDSL.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Property-based tests for authentication and authorization
 * Tests universal security properties that should hold for all valid inputs
 * 
 * Feature: telangana-ball-badminton-website
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthenticationAuthorizationPropertyTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Generators for random test data
    private Gen<String> usernameGen() {
        return strings().betweenCodePoints(97, 122).ofLengthBetween(5, 20);
    }

    private Gen<String> emailGen() {
        return strings().betweenCodePoints(97, 122).ofLengthBetween(5, 20)
                .map(s -> s + "@example.com");
    }

    private Gen<String> passwordGen() {
        return strings().betweenCodePoints(33, 126).ofLengthBetween(8, 30);
    }

    private Gen<String> nameGen() {
        return strings().betweenCodePoints(65, 90).ofLengthBetween(3, 30);
    }

    private Gen<Role> roleGen() {
        return arbitrary().pick(Role.USER, Role.EDITOR, Role.MODERATOR, Role.ADMIN);
    }

    private Gen<Role> nonAdminRoleGen() {
        return arbitrary().pick(Role.USER, Role.EDITOR, Role.MODERATOR);
    }

    /**
     * Property 15: Authorization Enforcement
     * For any protected API endpoint, requests without valid authentication tokens 
     * or with tokens lacking required permissions should be rejected with 401 or 403 status codes.
     * 
     * Validates: Requirements 6.1, 8.3, 9.3
     */
    @Test
    @Tag("Feature: telangana-ball-badminton-website, Property 15: Authorization Enforcement")
    public void testAuthorizationEnforcementWithoutToken() throws Exception {
        qt()
            .withExamples(100)
            .forAll(
                arbitrary().pick(
                    "/api/v1/admin/health",
                    "/api/v1/members",
                    "/api/v1/players",
                    "/api/v1/tournaments",
                    "/api/v1/news",
                    "/api/v1/media",
                    "/api/v1/districts"
                )
            )
            .checkAssert(endpoint -> {
                // Test POST request without authentication token
                if (endpoint.contains("admin") || endpoint.contains("members") || 
                    endpoint.contains("players") || endpoint.contains("tournaments")) {
                    mockMvc.perform(post(endpoint)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                        .andExpect(status().isUnauthorized());
                }
                
                // Test GET request without authentication token for admin endpoints
                if (endpoint.contains("admin")) {
                    mockMvc.perform(get(endpoint))
                        .andExpect(status().isUnauthorized());
                }
            });
    }

    @Test
    @Tag("Feature: telangana-ball-badminton-website, Property 15: Authorization Enforcement")
    public void testAuthorizationEnforcementWithInvalidToken() throws Exception {
        qt()
            .withExamples(100)
            .forAll(
                strings().betweenCodePoints(65, 90).ofLengthBetween(10, 50),
                arbitrary().pick(
                    "/api/v1/admin/health",
                    "/api/v1/members",
                    "/api/v1/players"
                )
            )
            .checkAssert((invalidToken, endpoint) -> {
                // Test with invalid token format
                mockMvc.perform(get(endpoint)
                        .header("Authorization", "Bearer " + invalidToken))
                    .andExpect(status().isUnauthorized());
            });
    }


    @Test
    @Tag("Feature: telangana-ball-badminton-website, Property 15: Authorization Enforcement")
    public void testAuthorizationEnforcementWithInsufficientPermissions() {
        qt()
            .withExamples(100)
            .forAll(
                usernameGen(),
                emailGen(),
                passwordGen(),
                nameGen(),
                nonAdminRoleGen()
            )
            .checkAssert((username, email, password, firstName, role) -> {
                // Create a user with non-admin role
                User user = new User();
                user.setUsername(username);
                user.setEmail(email);
                user.setPassword(passwordEncoder.encode(password));
                user.setFirstName(firstName);
                user.setLastName("Test");
                user.setRole(role);
                user.setIsActive(true);
                user.setIsEmailVerified(true);
                userRepository.save(user);

                // Generate token for the user
                String token = jwtService.generateToken(user);

                // Try to access admin endpoint with non-admin token
                try {
                    mockMvc.perform(get("/api/v1/admin/health")
                            .header("Authorization", "Bearer " + token))
                        .andExpect(status().isForbidden());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
    }
}