package com.telangana.ballbadminton.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Database configuration for the Telangana Ball Badminton Association Website
 * 
 * This configuration class handles:
 * - Transaction management
 * - Database connection pooling
 * 
 * Note: JPA repository configuration is handled in JpaConfig
 * Note: JPA auditing is configured in JpaAuditingConfig
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Configuration
@EnableTransactionManagement
public class DatabaseConfig {

    /**
     * Database properties configuration
     * Allows external configuration of database connection parameters
     */
    @ConfigurationProperties(prefix = "spring.datasource")
    public static class DatabaseProperties {
        private String url;
        private String username;
        private String password;
        private String driverClassName;
        
        // Getters and setters
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getDriverClassName() { return driverClassName; }
        public void setDriverClassName(String driverClassName) { this.driverClassName = driverClassName; }
    }
}