package com.telangana.ballbadminton.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

/**
 * Base class for unit tests
 * 
 * This class provides:
 * - Mockito extension for mock management
 * - Common test utilities and helpers
 * - ObjectMapper for JSON operations
 * - Test profile activation
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public abstract class BaseUnitTest {

    protected ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        // Configure ObjectMapper for tests
        objectMapper.findAndRegisterModules();
        
        // Additional setup can be added here
        setupTest();
    }

    /**
     * Template method for test-specific setup
     * Override this method in subclasses for custom setup
     */
    protected void setupTest() {
        // Default implementation - can be overridden
    }

    /**
     * Helper method to convert object to JSON string
     */
    protected String asJsonString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert object to JSON", e);
        }
    }

    /**
     * Helper method to convert JSON string to object
     */
    protected <T> T fromJsonString(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert JSON to object", e);
        }
    }

    /**
     * Helper method to create a mock object with default behavior
     */
    protected <T> T createMockWithDefaults(Class<T> clazz) {
        try {
            return org.mockito.Mockito.mock(clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create mock for class: " + clazz.getName(), e);
        }
    }
}