package com.telangana.ballbadminton.integration;

import com.telangana.ballbadminton.base.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests to verify the development environment setup
 * 
 * These tests ensure that:
 * - The application starts correctly
 * - Database connectivity works
 * - Security headers are properly configured
 * - CORS configuration is working
 * - API endpoints are accessible
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Development Environment Integration Tests")
class DevelopmentEnvironmentIntegrationTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Should start application context successfully")
    void contextLoads() {
        // This test passes if the application context loads without errors
        // It verifies that all beans are properly configured and dependencies are resolved
    }

    @Test
    @DisplayName("Should return health status from health endpoint")
    void shouldReturnHealthStatus() throws Exception {
        mockMvc.perform(get("/api/v1/public/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.application").exists())
                .andExpect(jsonPath("$.version").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Should return application status with system information")
    void shouldReturnApplicationStatus() throws Exception {
        mockMvc.perform(get("/api/v1/public/status"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.application").exists())
                .andExpect(jsonPath("$.version").exists())
                .andExpect(jsonPath("$.status").value("RUNNING"))
                .andExpect(jsonPath("$.runtime").exists())
                .andExpect(jsonPath("$.system").exists())
                .andExpect(jsonPath("$.features").exists())
                .andExpect(jsonPath("$.features.memberManagement").value("enabled"))
                .andExpect(jsonPath("$.features.playerProfiles").value("enabled"))
                .andExpect(jsonPath("$.features.tournamentManagement").value("enabled"));
    }

    @Test
    @DisplayName("Should return version information")
    void shouldReturnVersionInformation() throws Exception {
        mockMvc.perform(get("/api/v1/public/version"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.version").exists())
                .andExpect(jsonPath("$.apiVersion").value("v1"))
                .andExpect(jsonPath("$.buildTime").exists());
    }

    @Test
    @DisplayName("Should include security headers in responses")
    void shouldIncludeSecurityHeaders() throws Exception {
        mockMvc.perform(get("/api/v1/public/health"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Frame-Options"))
                .andExpect(header().exists("X-Content-Type-Options"))
                .andExpect(header().exists("X-XSS-Protection"))
                .andExpect(header().exists("Referrer-Policy"))
                .andExpect(header().exists("Content-Security-Policy"))
                .andExpect(header().exists("Permissions-Policy"))
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("X-XSS-Protection", "1; mode=block"));
    }

    @Test
    @DisplayName("Should handle CORS preflight requests")
    void shouldHandleCorsPreflightRequests() throws Exception {
        mockMvc.perform(
                get("/api/v1/public/health")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "Content-Type")
        )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 404 for non-existent endpoints")
    void shouldReturn404ForNonExistentEndpoints() throws Exception {
        mockMvc.perform(get("/api/v1/non-existent-endpoint"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should handle API documentation endpoints")
    void shouldHandleApiDocumentationEndpoints() throws Exception {
        // Test Swagger UI endpoint
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().isFound()); // Redirect to swagger-ui/index.html

        // Test API docs endpoint
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("Should set appropriate cache headers for different endpoint types")
    void shouldSetAppropiateCacheHeaders() throws Exception {
        // Public endpoints should have cache headers
        mockMvc.perform(get("/api/v1/public/health"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Cache-Control"));

        // API documentation should be cacheable
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should validate application properties are loaded correctly")
    void shouldValidateApplicationProperties() throws Exception {
        // This test verifies that the application properties are loaded
        // and the application is configured correctly for the test environment
        
        mockMvc.perform(get("/api/v1/public/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.application").value("telangana-ball-badminton-website"));
    }

    @Test
    @DisplayName("Should handle content negotiation correctly")
    void shouldHandleContentNegotiation() throws Exception {
        // Test JSON response (default)
        mockMvc.perform(get("/api/v1/public/health")
                .header("Accept", "application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));

        // Test that XML is not supported (should default to JSON)
        mockMvc.perform(get("/api/v1/public/health")
                .header("Accept", "application/xml"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }
}