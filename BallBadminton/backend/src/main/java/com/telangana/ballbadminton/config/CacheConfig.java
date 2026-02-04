package com.telangana.ballbadminton.config;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Cache configuration for the Telangana Ball Badminton Association Website
 * 
 * This configuration provides:
 * - Redis-based caching for improved performance
 * - Custom cache configurations for different data types
 * - Cache eviction policies and TTL settings
 * - Serialization configuration for cached objects
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Configuration
@EnableCaching
@Profile("!local") // Exclude this configuration when using local profile
public class CacheConfig {

    /**
     * Cache names used throughout the application
     */
    public static final String MEMBERS_CACHE = "members";
    public static final String PLAYERS_CACHE = "players";
    public static final String TOURNAMENTS_CACHE = "tournaments";
    public static final String DISTRICTS_CACHE = "districts";
    public static final String NEWS_CACHE = "news";
    public static final String MEDIA_CACHE = "media";
    public static final String STATISTICS_CACHE = "statistics";
    public static final String RANKINGS_CACHE = "rankings";

    /**
     * Redis template configuration for custom operations
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // Use String serializer for keys
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        
        // Use JSON serializer for values
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        
        template.afterPropertiesSet();
        return template;
    }

    /**
     * Redis cache manager with custom configurations for different cache types
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1)) // Default TTL of 1 hour
                .serializeKeysWith(org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));

        // Custom cache configurations for different data types
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // Members cache - longer TTL as member data changes less frequently
        cacheConfigurations.put(MEMBERS_CACHE, defaultConfig.entryTtl(Duration.ofHours(6)));
        
        // Players cache - medium TTL as player data changes moderately
        cacheConfigurations.put(PLAYERS_CACHE, defaultConfig.entryTtl(Duration.ofHours(3)));
        
        // Tournaments cache - shorter TTL as tournament data changes frequently
        cacheConfigurations.put(TOURNAMENTS_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(30)));
        
        // Districts cache - very long TTL as geographic data rarely changes
        cacheConfigurations.put(DISTRICTS_CACHE, defaultConfig.entryTtl(Duration.ofDays(1)));
        
        // News cache - short TTL as news is frequently updated
        cacheConfigurations.put(NEWS_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(15)));
        
        // Media cache - medium TTL as media galleries change moderately
        cacheConfigurations.put(MEDIA_CACHE, defaultConfig.entryTtl(Duration.ofHours(2)));
        
        // Statistics cache - short TTL as statistics are calculated frequently
        cacheConfigurations.put(STATISTICS_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(10)));
        
        // Rankings cache - medium TTL as rankings are updated periodically
        cacheConfigurations.put(RANKINGS_CACHE, defaultConfig.entryTtl(Duration.ofHours(1)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    /**
     * Customizer for Redis cache manager builder
     * Allows for additional customization of cache behavior
     */
    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder
                .disableCreateOnMissingCache() // Don't create caches automatically
                .enableStatistics(); // Enable cache statistics for monitoring
    }
}