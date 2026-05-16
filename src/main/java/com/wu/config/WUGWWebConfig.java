package com.wu.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
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

    /**
     * Removes X-Frame-Options so the app can be embedded in an iframe (e.g. Replit Preview).
     */
    @Bean
    public Filter removeXFrameOptionsFilter() {
        return (ServletRequest req, ServletResponse res, FilterChain chain) -> {
            HttpServletResponse response = (HttpServletResponse) res;
            chain.doFilter(req, res);
            response.setHeader("X-Frame-Options", null);
        };
    }
}
