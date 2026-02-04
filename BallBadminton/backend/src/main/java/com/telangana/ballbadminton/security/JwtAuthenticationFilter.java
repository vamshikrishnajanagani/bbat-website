package com.telangana.ballbadminton.security;

import com.telangana.ballbadminton.service.JwtService;
import com.telangana.ballbadminton.service.UserDetailsServiceImpl;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter for processing JWT tokens in requests
 * Validates JWT tokens and sets up Spring Security authentication context
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsServiceImpl userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        
        // Skip processing if no authorization header or not a Bearer token
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(BEARER_PREFIX.length());
            
            // Validate token format
            if (!jwtService.isValidTokenFormat(jwt)) {
                logger.warn("Invalid JWT token format from IP: {}", getClientIpAddress(request));
                filterChain.doFilter(request, response);
                return;
            }

            final String username = jwtService.extractUsername(jwt);

            // Process authentication if username exists and no authentication is set
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                authenticateUser(request, jwt, username);
            }

        } catch (JwtException e) {
            logger.warn("JWT token validation failed from IP {}: {}", getClientIpAddress(request), e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during JWT authentication from IP {}: {}", getClientIpAddress(request), e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Authenticate user based on JWT token
     */
    private void authenticateUser(HttpServletRequest request, String jwt, String username) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                
                logger.debug("Successfully authenticated user: {} from IP: {}", username, getClientIpAddress(request));
            } else {
                logger.warn("Invalid JWT token for user: {} from IP: {}", username, getClientIpAddress(request));
            }
        } catch (UsernameNotFoundException e) {
            logger.warn("User not found during JWT authentication: {} from IP: {}", username, getClientIpAddress(request));
        } catch (Exception e) {
            logger.error("Error loading user details for JWT authentication: {} from IP: {}", username, getClientIpAddress(request));
        }
    }

    /**
     * Get client IP address from request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    /**
     * Skip JWT processing for certain endpoints
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        
        // Skip JWT processing for public endpoints
        return path.startsWith("/api/v1/auth/login") ||
               path.startsWith("/api/v1/auth/register") ||
               path.startsWith("/api/v1/public/") ||
               path.startsWith("/actuator/health") ||
               path.startsWith("/actuator/info") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/api-docs/") ||
               path.startsWith("/v3/api-docs/");
    }
}