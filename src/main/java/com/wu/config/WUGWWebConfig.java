package com.wu.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WUGWWebConfig implements WebMvcConfigurer {

    /**
     * Global CORS configuration — allows all origins on all API paths.
     * Tighten allowedOrigins before moving to production.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedHeaders("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .maxAge(1800)
                .allowedOrigins("*");
    }
}
