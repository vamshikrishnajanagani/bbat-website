package com.telangana.ballbadminton.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Security headers configuration for the Telangana Ball Badminton Association Website
 * 
 * This configuration adds essential security headers to all HTTP responses:
 * - Content Security Policy (CSP)
 * - X-Frame-Options
 * - X-Content-Type-Options
 * - X-XSS-Protection
 * - Referrer-Policy
 * - Permissions-Policy
 * 
 * These headers help protect against various security vulnerabilities including:
 * - Cross-Site Scripting (XSS)
 * - Clickjacking
 * - MIME type sniffing
 * - Content injection attacks
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Configuration
public class SecurityHeadersConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SecurityHeadersInterceptor());
    }

    /**
     * Interceptor that adds security headers to all responses
     */
    public static class SecurityHeadersInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            // Content Security Policy - Helps prevent XSS attacks
            response.setHeader("Content-Security-Policy", 
                "default-src 'self'; " +
                "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://cdn.jsdelivr.net https://unpkg.com; " +
                "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com https://cdn.jsdelivr.net; " +
                "font-src 'self' https://fonts.gstatic.com https://cdn.jsdelivr.net; " +
                "img-src 'self' data: https: blob:; " +
                "connect-src 'self' https://api.telanganaballbadminton.org; " +
                "media-src 'self' https: blob:; " +
                "object-src 'none'; " +
                "base-uri 'self'; " +
                "form-action 'self'; " +
                "frame-ancestors 'none'; " +
                "upgrade-insecure-requests"
            );

            // X-Frame-Options - Prevents clickjacking attacks
            response.setHeader("X-Frame-Options", "DENY");

            // X-Content-Type-Options - Prevents MIME type sniffing
            response.setHeader("X-Content-Type-Options", "nosniff");

            // X-XSS-Protection - Enables XSS filtering (legacy browsers)
            response.setHeader("X-XSS-Protection", "1; mode=block");

            // Referrer-Policy - Controls referrer information
            response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

            // Permissions-Policy - Controls browser features
            response.setHeader("Permissions-Policy", 
                "camera=(), " +
                "microphone=(), " +
                "geolocation=(), " +
                "payment=(), " +
                "usb=(), " +
                "magnetometer=(), " +
                "gyroscope=(), " +
                "accelerometer=()"
            );

            // Strict-Transport-Security - Enforces HTTPS (only in production)
            String profile = System.getProperty("spring.profiles.active", "development");
            if ("production".equals(profile)) {
                response.setHeader("Strict-Transport-Security", 
                    "max-age=31536000; includeSubDomains; preload");
            }

            // Cache-Control for API responses
            String requestURI = request.getRequestURI();
            if (requestURI.startsWith("/api/")) {
                if (requestURI.contains("/public/")) {
                    // Public endpoints can be cached for a short time
                    response.setHeader("Cache-Control", "public, max-age=300"); // 5 minutes
                } else {
                    // Private endpoints should not be cached
                    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                    response.setHeader("Pragma", "no-cache");
                    response.setHeader("Expires", "0");
                }
            }

            // Server header removal (security through obscurity)
            response.setHeader("Server", "TBBA-API");

            return true;
        }
    }
}