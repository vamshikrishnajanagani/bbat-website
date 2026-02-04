package com.telangana.ballbadminton.config;

import com.telangana.ballbadminton.security.JwtAuthenticationFilter;
import com.telangana.ballbadminton.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Security configuration for the Telangana Ball Badminton Association Website
 * 
 * Implements JWT-based authentication and authorization with:
 * - JWT token authentication
 * - Role-based access control with hierarchy
 * - Permission-based authorization
 * - CORS support
 * - Security headers
 * - Stateless session management
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(
            CorsConfigurationSource corsConfigurationSource,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            UserDetailsServiceImpl userDetailsService
    ) {
        this.corsConfigurationSource = corsConfigurationSource;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Password encoder bean for secure password hashing
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Role hierarchy configuration
     * Defines the hierarchy of roles where higher roles inherit permissions from lower roles
     */
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String hierarchy = "ROLE_SUPER_ADMIN > ROLE_ADMIN " +
                          "ROLE_ADMIN > ROLE_EDITOR " +
                          "ROLE_EDITOR > ROLE_MODERATOR " +
                          "ROLE_MODERATOR > ROLE_USER";
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }

    /**
     * Authentication provider configuration
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Authentication manager bean
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Security filter chain configuration with JWT authentication and detailed authorization
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CORS configuration
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            
            // CSRF configuration - disabled for API
            .csrf(csrf -> csrf.disable())
            
            // Session management - stateless for API
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Authentication provider
            .authenticationProvider(authenticationProvider())
            
            // JWT filter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            // Authorization rules
            .authorizeHttpRequests(authz -> authz
                // Public endpoints - no authentication required
                .requestMatchers("/auth/login").permitAll()
                .requestMatchers("/auth/refresh").permitAll()
                .requestMatchers("/public/**").permitAll()
                .requestMatchers("/privacy/policy").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/actuator/info").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/api-docs/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                
                // Authentication endpoints - require authentication
                .requestMatchers("/auth/status").authenticated()
                .requestMatchers("/auth/logout").authenticated()
                
                // User management endpoints - require specific permissions
                .requestMatchers("/api/v1/users/create").hasAuthority("PERMISSION_USER_CREATE")
                .requestMatchers("/api/v1/users/*/roles").hasAuthority("PERMISSION_USER_MANAGE_ROLES")
                .requestMatchers("/api/v1/users/*/delete").hasAuthority("PERMISSION_USER_DELETE")
                .requestMatchers("/api/v1/users/**").hasAnyAuthority("PERMISSION_USER_READ", "PERMISSION_USER_UPDATE")
                
                // Member management endpoints
                .requestMatchers("/api/v1/members/create").hasAuthority("PERMISSION_MEMBER_CREATE")
                .requestMatchers("/api/v1/members/*/delete").hasAuthority("PERMISSION_MEMBER_DELETE")
                .requestMatchers("/api/v1/members/*/hierarchy").hasAuthority("PERMISSION_MEMBER_MANAGE_HIERARCHY")
                .requestMatchers("/api/v1/members/*/update").hasAuthority("PERMISSION_MEMBER_UPDATE")
                .requestMatchers("/api/v1/members/**").hasAuthority("PERMISSION_MEMBER_READ")
                
                // Player management endpoints
                .requestMatchers("/api/v1/players/create").hasAuthority("PERMISSION_PLAYER_CREATE")
                .requestMatchers("/api/v1/players/*/delete").hasAuthority("PERMISSION_PLAYER_DELETE")
                .requestMatchers("/api/v1/players/*/statistics").hasAuthority("PERMISSION_PLAYER_MANAGE_STATISTICS")
                .requestMatchers("/api/v1/players/*/achievements").hasAuthority("PERMISSION_PLAYER_MANAGE_ACHIEVEMENTS")
                .requestMatchers("/api/v1/players/*/update").hasAuthority("PERMISSION_PLAYER_UPDATE")
                .requestMatchers("/api/v1/players/**").hasAuthority("PERMISSION_PLAYER_READ")
                
                // Tournament management endpoints
                .requestMatchers("/api/v1/tournaments/create").hasAuthority("PERMISSION_TOURNAMENT_CREATE")
                .requestMatchers("/api/v1/tournaments/*/delete").hasAuthority("PERMISSION_TOURNAMENT_DELETE")
                .requestMatchers("/api/v1/tournaments/*/registration").hasAuthority("PERMISSION_TOURNAMENT_MANAGE_REGISTRATION")
                .requestMatchers("/api/v1/tournaments/*/results").hasAuthority("PERMISSION_TOURNAMENT_MANAGE_RESULTS")
                .requestMatchers("/api/v1/tournaments/*/update").hasAuthority("PERMISSION_TOURNAMENT_UPDATE")
                .requestMatchers("/api/v1/tournaments/**").hasAuthority("PERMISSION_TOURNAMENT_READ")
                
                // News management endpoints
                .requestMatchers("/api/v1/news/create").hasAuthority("PERMISSION_NEWS_CREATE")
                .requestMatchers("/api/v1/news/*/delete").hasAuthority("PERMISSION_NEWS_DELETE")
                .requestMatchers("/api/v1/news/*/publish").hasAuthority("PERMISSION_NEWS_PUBLISH")
                .requestMatchers("/api/v1/news/*/moderate").hasAuthority("PERMISSION_NEWS_MODERATE")
                .requestMatchers("/api/v1/news/*/update").hasAuthority("PERMISSION_NEWS_UPDATE")
                .requestMatchers("/api/v1/news/**").hasAuthority("PERMISSION_NEWS_READ")
                
                // Media management endpoints
                .requestMatchers("/api/v1/media/upload").hasAuthority("PERMISSION_MEDIA_CREATE")
                .requestMatchers("/api/v1/media/*/delete").hasAuthority("PERMISSION_MEDIA_DELETE")
                .requestMatchers("/api/v1/media/galleries/**").hasAuthority("PERMISSION_MEDIA_MANAGE_GALLERIES")
                .requestMatchers("/api/v1/media/*/update").hasAuthority("PERMISSION_MEDIA_UPDATE")
                .requestMatchers("/api/v1/media/**").hasAuthority("PERMISSION_MEDIA_READ")
                
                // District management endpoints
                .requestMatchers("/api/v1/districts/create").hasAuthority("PERMISSION_DISTRICT_CREATE")
                .requestMatchers("/api/v1/districts/*/delete").hasAuthority("PERMISSION_DISTRICT_DELETE")
                .requestMatchers("/api/v1/districts/*/statistics").hasAuthority("PERMISSION_DISTRICT_MANAGE_STATISTICS")
                .requestMatchers("/api/v1/districts/*/update").hasAuthority("PERMISSION_DISTRICT_UPDATE")
                .requestMatchers("/api/v1/districts/**").hasAuthority("PERMISSION_DISTRICT_READ")
                
                // System administration endpoints
                .requestMatchers("/api/v1/admin/system/**").hasAuthority("PERMISSION_SYSTEM_ADMIN")
                .requestMatchers("/api/v1/admin/backup/**").hasAuthority("PERMISSION_SYSTEM_BACKUP")
                .requestMatchers("/api/v1/admin/restore/**").hasAuthority("PERMISSION_SYSTEM_RESTORE")
                .requestMatchers("/api/v1/admin/monitor/**").hasAuthority("PERMISSION_SYSTEM_MONITOR")
                .requestMatchers("/api/v1/admin/audit/**").hasAuthority("PERMISSION_SYSTEM_AUDIT")
                
                // Content moderation endpoints
                .requestMatchers("/api/v1/moderation/**").hasAnyAuthority(
                    "PERMISSION_CONTENT_MODERATE", 
                    "PERMISSION_CONTENT_APPROVE", 
                    "PERMISSION_CONTENT_REJECT"
                )
                
                // File management endpoints
                .requestMatchers("/api/v1/files/upload").hasAuthority("PERMISSION_FILE_UPLOAD")
                .requestMatchers("/api/v1/files/*/delete").hasAuthority("PERMISSION_FILE_DELETE")
                .requestMatchers("/api/v1/files/manage/**").hasAuthority("PERMISSION_FILE_MANAGE")
                .requestMatchers("/api/v1/files/**").hasAuthority("PERMISSION_FILE_DOWNLOAD")
                
                // Legacy role-based endpoints for backward compatibility
                .requestMatchers("/admin/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                
                // All other API endpoints require authentication
                .requestMatchers("/api/**").authenticated()
                
                // All other endpoints require authentication
                .anyRequest().authenticated()
            );

        return http.build();
    }
}