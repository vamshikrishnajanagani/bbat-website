package com.telangana.ballbadminton.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC Configuration
 * 
 * Configures:
 * - Cache headers interceptor for API responses
 * - Static resource handling
 * - Custom interceptors
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final CacheHeadersInterceptor cacheHeadersInterceptor;

    @Autowired
    public WebMvcConfig(CacheHeadersInterceptor cacheHeadersInterceptor) {
        this.cacheHeadersInterceptor = cacheHeadersInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(cacheHeadersInterceptor)
                .addPathPatterns("/api/v1/**")
                .excludePathPatterns("/api/v1/auth/**", "/api/v1/admin/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configure static resource handling with cache headers
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/")
                .setCachePeriod(31536000); // 1 year cache for uploaded files
    }
}
