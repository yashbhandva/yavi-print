package com.web_app.yaviPrint.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@Profile("prod")
public class ProdCorsConfig {

    @Value("${app.frontend.url:https://yaviprint.com}")
    private String frontendUrl;

    @Value("${app.admin.url:https://admin.yaviprint.com}")
    private String adminUrl;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Production-specific strict CORS settings
        configuration.setAllowedOriginPatterns(Arrays.asList(
                frontendUrl,
                adminUrl,
                "https://*.yaviprint.com",
                "https://yaviprint.vercel.app"
        ));

        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers",
                "X-API-Version"
        ));

        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Disposition",
                "X-Total-Count",
                "X-API-Version"
        ));

        configuration.setAllowCredentials(true);
        configuration.setMaxAge(1800L); // 30 minutes for production

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}