package com.hduong25.javalearn.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author: hduong25
 */
@Configuration
public class CustomCorsConfig implements CorsConfigurationSource {

    @SuppressWarnings("null")
    @Override
    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("*")); // Cho phép tất cả các origin
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE")); // Các HTTP method được phép
        configuration.setAllowedHeaders(List.of("*")); // Cho phép tất cả các header
        configuration.setAllowCredentials(true); // Hỗ trợ cookie hoặc Authorization header

        return configuration;
    }

}
