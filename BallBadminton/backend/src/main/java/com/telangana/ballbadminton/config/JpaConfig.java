package com.telangana.ballbadminton.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * JPA Configuration class
 * Enables JPA repositories
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.telangana.ballbadminton.repository")
public class JpaConfig {
    // JPA auditing configuration is handled by JpaAuditingConfig
    // Additional JPA configuration can be added here if needed
}