package com.telangana.ballbadminton.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * CORS (Cross-Origin Resource Sharing) configuration for the Telangana Ball Badminton Association Website
 * 
 * This configuration handles:
 * - Cross-origin requests from frontend applications
 * - Security headers for CORS requests
 * - Environment-specific CORS policies
 * - Preflight request handling
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${app.cors.allowed-methods}")
    private String allowedMethods;

    @Value("${app.cors.allowed-headers}")
    private String allowedHeaders;

    @Value("${app.cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Value("${app.cors.max-age:3600}")
    private long maxAge;

    /**
     * Configure CORS mappings for all endpoints
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        List<String> methods = Arrays.asList(allowedMethods.split(","));
        List<String> headers = Arrays.asList(allowedHeaders.split(","));

        registry.addMapping("/api/**")
                .allowedOrigins(origins.toArray(new String[0]))
                .allowedMethods(methods.toArray(new String[0]))
                .allowedHeaders(headers.toArray(new String[0]))
                .allowCredentials(allowCredentials)
                .maxAge(maxAge);

        // Additional mapping for actuator endpoints
        registry.addMapping("/actuator/**")
                .allowedOrigins(origins.toArray(new String[0]))
                .allowedMethods("GET", "POST")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(maxAge);

        // Additional mapping for API documentation
        registry.addMapping("/swagger-ui/**")
                .allowedOrigins(origins.toArray(new String[0]))
                .allowedMethods("GET")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(maxAge);

        registry.addMapping("/api-docs/**")
                .allowedOrigins(origins.toArray(new String[0]))
                .allowedMethods("GET")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(maxAge);
    }

    /**
     * CORS configuration source for Spring Security
     * This is used by Spring Security to handle CORS for secured endpoints
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Set allowed origins
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        configuration.setAllowedOrigins(origins);
        
        // Set allowed methods
        List<String> methods = Arrays.asList(allowedMethods.split(","));
        configuration.setAllowedMethods(methods);
        
        // Set allowed headers
        if ("*".equals(allowedHeaders)) {
            configuration.addAllowedHeader("*");
        } else {
            List<String> headers = Arrays.asList(allowedHeaders.split(","));
            configuration.setAllowedHeaders(headers);
        }
        
        // Set credentials and max age
        configuration.setAllowCredentials(allowCredentials);
        configuration.setMaxAge(maxAge);
        
        // Expose headers that the frontend might need
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Total-Count",
                "X-Page-Number",
                "X-Page-Size",
                "X-Has-Next-Page",
                "X-Has-Previous-Page"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}