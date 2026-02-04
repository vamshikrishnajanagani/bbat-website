package com.telangana.ballbadminton;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for Telangana Ball Badminton Association Website
 * 
 * This application provides a comprehensive API-driven platform for managing
 * association members, players, tournaments, and related content.
 * 
 * Features:
 * - RESTful API architecture
 * - JWT-based authentication and authorization
 * - Dynamic content management
 * - Real-time updates and notifications
 * - Comprehensive monitoring and logging
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableScheduling
public class AssociationWebsiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssociationWebsiteApplication.class, args);
    }
}