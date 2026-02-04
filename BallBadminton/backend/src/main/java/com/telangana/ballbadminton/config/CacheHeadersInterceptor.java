package com.telangana.ballbadminton.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor to add cache control headers to API responses
 * 
 * This interceptor adds appropriate cache headers based on the request path:
 * - Static resources: Long cache duration
 * - Public data (districts, news): Medium cache duration
 * - Dynamic data (tournaments, registrations): Short cache duration
 * - Private data: No cache
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Component
public class CacheHeadersInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // Only add cache headers for GET requests
        if (!"GET".equalsIgnoreCase(method)) {
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
            return true;
        }
        
        // Static resources - long cache (1 year)
        if (path.matches(".+\\.(js|css|png|jpg|jpeg|gif|svg|woff|woff2|ttf|eot|ico)$")) {
            response.setHeader("Cache-Control", "public, max-age=31536000, immutable");
            return true;
        }
        
        // Districts - very long cache (1 day) as geographic data rarely changes
        if (path.contains("/districts")) {
            response.setHeader("Cache-Control", "public, max-age=86400, s-maxage=86400");
            response.setHeader("Vary", "Accept-Encoding");
            return true;
        }
        
        // Members - long cache (6 hours) as member data changes infrequently
        if (path.contains("/members")) {
            response.setHeader("Cache-Control", "public, max-age=21600, s-maxage=21600");
            response.setHeader("Vary", "Accept-Encoding");
            return true;
        }
        
        // Players - medium cache (3 hours)
        if (path.contains("/players")) {
            response.setHeader("Cache-Control", "public, max-age=10800, s-maxage=10800");
            response.setHeader("Vary", "Accept-Encoding");
            return true;
        }
        
        // Media galleries - medium cache (2 hours)
        if (path.contains("/media")) {
            response.setHeader("Cache-Control", "public, max-age=7200, s-maxage=7200");
            response.setHeader("Vary", "Accept-Encoding");
            return true;
        }
        
        // News - short cache (15 minutes) as news is frequently updated
        if (path.contains("/news")) {
            response.setHeader("Cache-Control", "public, max-age=900, s-maxage=900");
            response.setHeader("Vary", "Accept-Encoding");
            return true;
        }
        
        // Tournaments - short cache (30 minutes) as tournament data changes frequently
        if (path.contains("/tournaments")) {
            response.setHeader("Cache-Control", "public, max-age=1800, s-maxage=1800");
            response.setHeader("Vary", "Accept-Encoding");
            return true;
        }
        
        // Statistics and rankings - short cache (10 minutes)
        if (path.contains("/statistics") || path.contains("/rankings")) {
            response.setHeader("Cache-Control", "public, max-age=600, s-maxage=600");
            response.setHeader("Vary", "Accept-Encoding");
            return true;
        }
        
        // Authentication and user-specific endpoints - no cache
        if (path.contains("/auth") || path.contains("/user") || path.contains("/admin")) {
            response.setHeader("Cache-Control", "private, no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
            return true;
        }
        
        // Default - moderate cache (1 hour)
        response.setHeader("Cache-Control", "public, max-age=3600, s-maxage=3600");
        response.setHeader("Vary", "Accept-Encoding");
        
        return true;
    }
}
