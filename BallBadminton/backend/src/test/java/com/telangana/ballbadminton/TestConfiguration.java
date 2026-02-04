package com.telangana.ballbadminton;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Test configuration for the Telangana Ball Badminton Association Website
 * 
 * This configuration provides:
 * - Test-specific beans and configurations
 * - Testcontainers setup for integration tests
 * - Mock services for unit tests
 * - Test data factories and utilities
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@org.springframework.boot.test.context.TestConfiguration
@Profile("test")
public class TestConfiguration {

    /**
     * Password encoder for test environment
     * Uses a lower strength for faster test execution
     */
    @Bean
    @Primary
    public PasswordEncoder testPasswordEncoder() {
        return new BCryptPasswordEncoder(4); // Lower strength for faster tests
    }

    /**
     * PostgreSQL container for integration tests
     * This will be used when running tests that require a real database
     */
    @Bean
    public PostgreSQLContainer<?> postgresContainer() {
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:15-alpine")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")
                .withReuse(true);
        
        container.start();
        return container;
    }

    /**
     * Redis container for integration tests
     * This will be used when testing caching functionality
     */
    @Bean
    public GenericContainer<?> redisContainer() {
        GenericContainer<?> container = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
                .withExposedPorts(6379)
                .withReuse(true);
        
        container.start();
        return container;
    }
}