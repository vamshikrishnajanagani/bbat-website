package com.telangana.ballbadminton.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI (Swagger) documentation configuration for the Telangana Ball Badminton Association Website
 * 
 * This configuration provides:
 * - Comprehensive API documentation with detailed descriptions
 * - Interactive API testing interface (Swagger UI)
 * - Security scheme documentation for JWT authentication
 * - Server and contact information
 * - API versioning and deprecation notices
 * - Code examples and integration guides
 * 
 * Access the documentation at:
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - OpenAPI JSON: http://localhost:8080/v3/api-docs
 * - OpenAPI YAML: http://localhost:8080/v3/api-docs.yaml
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 * @since 2023-12-01
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${api.version:v1}")
    private String apiVersion;

    /**
     * OpenAPI configuration bean with comprehensive documentation
     * 
     * Configures:
     * - API metadata (title, description, version, contact, license)
     * - Multiple server environments (development, staging, production)
     * - Security schemes (JWT Bearer authentication)
     * - API tags for endpoint organization
     * - External documentation links
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(serverList())
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", createJWTSecurityScheme()))
                .tags(apiTags())
                .externalDocs(externalDocumentation());
    }

    /**
     * API information configuration with comprehensive description
     */
    private Info apiInfo() {
        return new Info()
                .title("Telangana Ball Badminton Association API")
                .description(buildApiDescription())
                .version("1.0.0")
                .contact(new Contact()
                        .name("Telangana Ball Badminton Association - API Support")
                        .email("api-support@telanganaballbadminton.org")
                        .url("https://www.telanganaballbadminton.org/contact"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"))
                .termsOfService("https://www.telanganaballbadminton.org/terms");
    }

    /**
     * Build comprehensive API description with markdown formatting
     */
    private String buildApiDescription() {
        return """
                # Telangana Ball Badminton Association REST API
                
                Official REST API for the Telangana Ball Badminton Association Website. This API provides comprehensive 
                functionality for managing association operations, player data, tournaments, and content.
                
                ## Features
                
                ### Content Management
                - **Association Members**: Manage organizational hierarchy, member profiles, and contact information
                - **Player Profiles**: Track player achievements, statistics, rankings, and career highlights
                - **Tournament Management**: Create tournaments, handle registrations, generate brackets, and track results
                - **Geographic Information**: Manage Telangana state and district data with location-based features
                - **News & Media**: Publish articles, announcements, and manage photo/video galleries
                - **Downloads**: Provide rules, regulations, forms, and other resources
                
                ### Key Capabilities
                - ✅ RESTful API design with standard HTTP methods
                - ✅ JWT-based authentication and role-based authorization
                - ✅ Comprehensive pagination, filtering, and search
                - ✅ Real-time data updates without code deployments
                - ✅ Multilingual support (English and Telugu)
                - ✅ File upload and media management
                - ✅ Email notifications and announcements
                - ✅ Audit logging and data security
                - ✅ Caching for optimal performance
                - ✅ Rate limiting for fair usage
                
                ## Getting Started
                
                ### 1. Authentication
                Most endpoints require JWT authentication. Start by obtaining a token:
                
                ```bash
                POST /auth/login
                {
                  "usernameOrEmail": "your-email@example.com",
                  "password": "your-password"
                }
                ```
                
                ### 2. Using the Token
                Include the JWT token in the Authorization header for protected endpoints:
                
                ```
                Authorization: Bearer <your-jwt-token>
                ```
                
                ### 3. Making Requests
                All requests and responses use JSON format. Example:
                
                ```bash
                GET /members
                Authorization: Bearer <your-jwt-token>
                ```
                
                ## API Versioning
                
                **Current Version**: v1 (Stable)
                
                The API uses URL-based versioning. All endpoints are prefixed with `/api/v1/`.
                
                ### Version Policy
                - **Major versions** (v1, v2): Breaking changes, new major features
                - **Minor updates**: Backward-compatible changes, bug fixes, new optional fields
                - **Deprecation notice**: 6 months before removal of deprecated endpoints
                
                ### Checking API Version
                The API version is included in:
                - URL path: `/api/v1/...`
                - Response header: `X-API-Version: 1.0.0`
                - OpenAPI specification: `info.version`
                
                ## Rate Limiting
                
                API requests are rate-limited to ensure fair usage and system stability:
                
                | User Type | Requests per Hour | Burst Limit |
                |-----------|-------------------|-------------|
                | Anonymous | 100 | 20 |
                | Authenticated | 1,000 | 100 |
                | Admin | 5,000 | 500 |
                
                Rate limit information is included in response headers:
                - `X-RateLimit-Limit`: Maximum requests allowed
                - `X-RateLimit-Remaining`: Remaining requests in current window
                - `X-RateLimit-Reset`: Unix timestamp when the limit resets
                
                ## Caching
                
                Many endpoints support HTTP caching for improved performance:
                - `Cache-Control`: Caching directives
                - `ETag`: Entity tag for conditional requests
                - `Last-Modified`: Last modification timestamp
                
                Use conditional requests with `If-None-Match` or `If-Modified-Since` headers to reduce bandwidth.
                
                ## Error Handling
                
                The API uses standard HTTP status codes and provides detailed error messages:
                
                | Status Code | Description |
                |-------------|-------------|
                | 200 | OK - Request successful |
                | 201 | Created - Resource created successfully |
                | 204 | No Content - Request successful, no content to return |
                | 400 | Bad Request - Invalid request data |
                | 401 | Unauthorized - Authentication required |
                | 403 | Forbidden - Insufficient permissions |
                | 404 | Not Found - Resource not found |
                | 409 | Conflict - Resource already exists |
                | 422 | Unprocessable Entity - Validation error |
                | 429 | Too Many Requests - Rate limit exceeded |
                | 500 | Internal Server Error - Server error |
                
                Error responses include detailed information:
                ```json
                {
                  "error": true,
                  "message": "Validation failed",
                  "timestamp": "2023-12-01T10:00:00Z",
                  "path": "/api/v1/members",
                  "status": 400,
                  "errors": [
                    {
                      "field": "email",
                      "message": "Email is required"
                    }
                  ]
                }
                ```
                
                ## Pagination
                
                List endpoints support pagination with query parameters:
                - `page`: Page number (0-based, default: 0)
                - `size`: Items per page (default: 20, max: 100)
                - `sortBy`: Field to sort by (default varies by endpoint)
                - `sortDir`: Sort direction - `asc` or `desc` (default: asc)
                
                Example: `GET /players/paginated?page=0&size=10&sortBy=name&sortDir=asc`
                
                ## Filtering and Search
                
                Many endpoints support filtering and full-text search:
                - **Filtering**: Use query parameters like `category`, `status`, `districtId`
                - **Search**: Use the `q` parameter for text search across multiple fields
                - **Date ranges**: Use `startDate` and `endDate` parameters
                
                Example: `GET /players/filter?category=MEN&isProminent=true`
                
                ## Support and Resources
                
                - **Developer Guide**: Comprehensive integration guide with code examples
                - **API Status**: https://status.telanganaballbadminton.org
                - **Support Email**: api-support@telanganaballbadminton.org
                - **GitHub Issues**: https://github.com/telangana-ball-badminton/api/issues
                - **Changelog**: https://www.telanganaballbadminton.org/api/changelog
                
                ## Deprecation Policy
                
                When endpoints or features are deprecated:
                1. **Announcement**: 6 months advance notice
                2. **Headers**: `Deprecation: true` and `Sunset: <date>` headers added
                3. **Documentation**: Migration guide published
                4. **Support**: Old version supported during transition period
                5. **Removal**: After 6-month notice period
                
                Monitor response headers for deprecation warnings and plan migrations accordingly.
                """;
    }

    /**
     * Configure multiple server environments
     */
    private List<Server> serverList() {
        return List.of(
                new Server()
                        .url("http://localhost:" + serverPort + "/api/" + apiVersion)
                        .description("Development Server - Local environment for testing"),
                new Server()
                        .url("https://api-staging.telanganaballbadminton.org/api/" + apiVersion)
                        .description("Staging Server - Pre-production testing environment"),
                new Server()
                        .url("https://api.telanganaballbadminton.org/api/" + apiVersion)
                        .description("Production Server - Live production environment")
        );
    }

    /**
     * JWT Security scheme configuration
     */
    private SecurityScheme createJWTSecurityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .description("""
                        ## JWT Authentication
                        
                        This API uses JWT (JSON Web Tokens) for authentication. To access protected endpoints, 
                        you must include a valid JWT token in the Authorization header.
                        
                        ### Obtaining a Token
                        
                        1. Call the `/auth/login` endpoint with your credentials:
                        ```json
                        POST /auth/login
                        {
                          "usernameOrEmail": "user@example.com",
                          "password": "your-password"
                        }
                        ```
                        
                        2. The response includes an access token and refresh token:
                        ```json
                        {
                          "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                          "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                          "tokenType": "Bearer",
                          "expiresIn": 3600
                        }
                        ```
                        
                        ### Using the Token
                        
                        Include the access token in the Authorization header:
                        ```
                        Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
                        ```
                        
                        ### Token Expiration
                        
                        - Access tokens expire after 1 hour
                        - Use the refresh token to obtain a new access token without re-authenticating
                        - Call `/auth/refresh` with the refresh token
                        
                        ### Token Refresh
                        
                        ```json
                        POST /auth/refresh
                        {
                          "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                        }
                        ```
                        
                        ### Security Best Practices
                        
                        - Store tokens securely (never in localStorage for sensitive apps)
                        - Always use HTTPS in production
                        - Implement token refresh logic to handle expiration
                        - Clear tokens on logout
                        - Never share tokens or commit them to version control
                        """);
    }

    /**
     * Define API tags for endpoint organization
     */
    private List<Tag> apiTags() {
        return List.of(
                new Tag()
                        .name("Authentication")
                        .description("User authentication, token management, and session handling"),
                new Tag()
                        .name("Member Management")
                        .description("Association member profiles, hierarchy, and contact management"),
                new Tag()
                        .name("Player Management")
                        .description("Player profiles, achievements, statistics, and rankings"),
                new Tag()
                        .name("Tournament Management")
                        .description("Tournament creation, registration, brackets, and results"),
                new Tag()
                        .name("Districts")
                        .description("Geographic information for Telangana state and districts"),
                new Tag()
                        .name("News")
                        .description("News articles, announcements, and content management"),
                new Tag()
                        .name("Media")
                        .description("Photo and video galleries, media uploads"),
                new Tag()
                        .name("Downloads")
                        .description("Rules, regulations, forms, and downloadable resources"),
                new Tag()
                        .name("Admin")
                        .description("Administrative functions, audit logs, and system management")
        );
    }

    /**
     * External documentation links
     */
    private ExternalDocumentation externalDocumentation() {
        return new ExternalDocumentation()
                .description("Complete Developer Guide with Code Examples and Integration Instructions")
                .url("https://docs.telanganaballbadminton.org/api/developer-guide");
    }
}