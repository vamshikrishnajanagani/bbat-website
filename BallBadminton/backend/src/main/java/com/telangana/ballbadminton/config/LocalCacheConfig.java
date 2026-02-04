package com.telangana.ballbadminton.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Local cache configuration for development without Redis
 * Uses simple in-memory caching for local development
 */
@Configuration
@EnableCaching
@Profile("local")
public class LocalCacheConfig {

    /**
     * Simple in-memory cache manager for local development
     */
    @Bean
    @Primary
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        
        // Pre-configure cache names
        cacheManager.setCacheNames(java.util.Arrays.asList(
            "members",
            "players", 
            "tournaments",
            "districts",
            "news",
            "media",
            "statistics",
            "rankings"
        ));
        
        // Allow dynamic cache creation
        cacheManager.setAllowNullValues(false);
        
        return cacheManager;
    }
}