package com.telangana.ballbadminton.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Monitoring and observability configuration for the Telangana Ball Badminton Association Website
 * 
 * This configuration provides:
 * - Custom health indicators for database and Redis
 * - Application metrics and monitoring
 * - Performance timing aspects
 * - Custom info contributors
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Configuration
public class MonitoringConfig {

    /**
     * Enables @Timed annotation for method-level performance monitoring
     */
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    /**
     * Custom health indicator for database connectivity
     */
    @Bean
    public HealthIndicator databaseHealthIndicator(DataSource dataSource) {
        return new DatabaseHealthIndicator(dataSource);
    }

    /**
     * Custom health indicator for Redis connectivity
     */
    @Bean
    public HealthIndicator redisHealthIndicator(RedisConnectionFactory redisConnectionFactory) {
        return new RedisHealthIndicator(redisConnectionFactory);
    }

    /**
     * Custom info contributor for application metadata
     */
    @Bean
    public InfoContributor applicationInfoContributor() {
        return new ApplicationInfoContributor();
    }

    /**
     * Database health indicator implementation
     */
    public static class DatabaseHealthIndicator implements HealthIndicator {
        
        private final DataSource dataSource;
        
        public DatabaseHealthIndicator(DataSource dataSource) {
            this.dataSource = dataSource;
        }
        
        @Override
        public Health health() {
            try (Connection connection = dataSource.getConnection()) {
                if (connection.isValid(1)) {
                    return Health.up()
                            .withDetail("database", "PostgreSQL")
                            .withDetail("status", "Connected")
                            .withDetail("validationQuery", "SELECT 1")
                            .build();
                } else {
                    return Health.down()
                            .withDetail("database", "PostgreSQL")
                            .withDetail("status", "Connection invalid")
                            .build();
                }
            } catch (Exception e) {
                return Health.down()
                        .withDetail("database", "PostgreSQL")
                        .withDetail("status", "Connection failed")
                        .withDetail("error", e.getMessage())
                        .build();
            }
        }
    }

    /**
     * Redis health indicator implementation
     */
    public static class RedisHealthIndicator implements HealthIndicator {
        
        private final RedisConnectionFactory redisConnectionFactory;
        
        public RedisHealthIndicator(RedisConnectionFactory redisConnectionFactory) {
            this.redisConnectionFactory = redisConnectionFactory;
        }
        
        @Override
        public Health health() {
            try {
                RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
                redisTemplate.setConnectionFactory(redisConnectionFactory);
                redisTemplate.afterPropertiesSet();
                
                // Test Redis connectivity
                redisTemplate.opsForValue().set("health-check", "ok");
                String result = redisTemplate.opsForValue().get("health-check");
                redisTemplate.delete("health-check");
                
                if ("ok".equals(result)) {
                    return Health.up()
                            .withDetail("redis", "Connected")
                            .withDetail("status", "Operational")
                            .build();
                } else {
                    return Health.down()
                            .withDetail("redis", "Connection test failed")
                            .build();
                }
            } catch (Exception e) {
                return Health.down()
                        .withDetail("redis", "Connection failed")
                        .withDetail("error", e.getMessage())
                        .build();
            }
        }
    }

    /**
     * Application info contributor implementation
     */
    public static class ApplicationInfoContributor implements InfoContributor {
        
        @Override
        public void contribute(org.springframework.boot.actuate.info.Info.Builder builder) {
            Map<String, Object> applicationInfo = new HashMap<>();
            applicationInfo.put("name", "Telangana Ball Badminton Association Website");
            applicationInfo.put("description", "Official API for managing association members, players, tournaments, and content");
            applicationInfo.put("version", "1.0.0");
            applicationInfo.put("organization", "Telangana Ball Badminton Association");
            applicationInfo.put("contact", "admin@telanganaballbadminton.org");
            applicationInfo.put("startup-time", LocalDateTime.now().toString());
            
            Map<String, Object> features = new HashMap<>();
            features.put("member-management", "enabled");
            features.put("player-profiles", "enabled");
            features.put("tournament-management", "enabled");
            features.put("geographic-info", "enabled");
            features.put("news-media", "enabled");
            features.put("multilingual-support", "enabled");
            features.put("api-documentation", "enabled");
            features.put("caching", "enabled");
            features.put("monitoring", "enabled");
            
            applicationInfo.put("features", features);
            
            builder.withDetail("application", applicationInfo);
        }
    }
}