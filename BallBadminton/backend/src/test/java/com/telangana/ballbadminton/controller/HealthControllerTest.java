package com.telangana.ballbadminton.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for the HealthController
 * 
 * Tests the health check endpoints to ensure they return proper responses
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@WebMvcTest(HealthController.class)
@ActiveProfiles("test")
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Test the health endpoint returns 200 OK with proper status
     */
    @Test
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/public/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.application").exists())
                .andExpect(jsonPath("$.version").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    /**
     * Test the status endpoint returns detailed information
     */
    @Test
    void testStatusEndpoint() throws Exception {
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

    /**
     * Test the version endpoint returns version information
     */
    @Test
    void testVersionEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/public/version"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.version").exists())
                .andExpect(jsonPath("$.apiVersion").value("v1"))
                .andExpect(jsonPath("$.buildTime").exists());
    }
}