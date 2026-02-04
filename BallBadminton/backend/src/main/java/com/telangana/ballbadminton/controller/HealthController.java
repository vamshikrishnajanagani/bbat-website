package com.telangana.ballbadminton.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health check controller for the Telangana Ball Badminton Association Website
 * 
 * Provides basic health and status endpoints for monitoring and verification
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@RestController
@RequestMapping("/public")
@Tag(name = "Health", description = "Health check and status endpoints")
public class HealthController {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.application.version:1.0.0}")
    private String applicationVersion;

    /**
     * Basic health check endpoint
     */
    @GetMapping("/health")
    @Operation(
        summary = "Health Check",
        description = "Returns the health status of the application"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Application is healthy")
    })
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("application", applicationName);
        response.put("version", applicationVersion);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Application status endpoint with detailed information
     */
    @GetMapping("/status")
    @Operation(
        summary = "Application Status",
        description = "Returns detailed status information about the application"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status information retrieved successfully")
    })
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> response = new HashMap<>();
        
        // Basic information
        response.put("application", applicationName);
        response.put("version", applicationVersion);
        response.put("status", "RUNNING");
        response.put("timestamp", LocalDateTime.now());
        
        // Runtime information
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> runtimeInfo = new HashMap<>();
        runtimeInfo.put("processors", runtime.availableProcessors());
        runtimeInfo.put("totalMemory", runtime.totalMemory());
        runtimeInfo.put("freeMemory", runtime.freeMemory());
        runtimeInfo.put("maxMemory", runtime.maxMemory());
        response.put("runtime", runtimeInfo);
        
        // System properties
        Map<String, Object> systemInfo = new HashMap<>();
        systemInfo.put("javaVersion", System.getProperty("java.version"));
        systemInfo.put("javaVendor", System.getProperty("java.vendor"));
        systemInfo.put("osName", System.getProperty("os.name"));
        systemInfo.put("osVersion", System.getProperty("os.version"));
        response.put("system", systemInfo);
        
        // Features
        Map<String, Object> features = new HashMap<>();
        features.put("memberManagement", "enabled");
        features.put("playerProfiles", "enabled");
        features.put("tournamentManagement", "enabled");
        features.put("geographicInfo", "enabled");
        features.put("newsMedia", "enabled");
        features.put("multilingualSupport", "enabled");
        features.put("apiDocumentation", "enabled");
        features.put("caching", "enabled");
        features.put("monitoring", "enabled");
        response.put("features", features);
        
        return ResponseEntity.ok(response);
    }

    /**
     * API version endpoint
     */
    @GetMapping("/version")
    @Operation(
        summary = "API Version",
        description = "Returns the current API version"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Version information retrieved successfully")
    })
    public ResponseEntity<Map<String, String>> version() {
        Map<String, String> response = new HashMap<>();
        response.put("version", applicationVersion);
        response.put("apiVersion", "v1");
        response.put("buildTime", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(response);
    }
}