package com.lovedev.common.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Security configuration properties
 */
@ConfigurationProperties(prefix = "app.security")
@Data
public class SecurityProperties {

    private JwtProperties jwt = new JwtProperties();
    private CorsProperties cors = new CorsProperties();

    @Data
    public static class JwtProperties {
        /**
         * JWT secret key (must be at least 256 bits)
         */
        private String secret;

        /**
         * Access token expiration time in milliseconds (default: 15 minutes)
         */
        private Long expiration = 900000L;

        /**
         * Refresh token expiration time in milliseconds (default: 7 days)
         */
        private Long refreshExpiration = 604800000L;

        /**
         * Token issuer
         */
        private String issuer = "lovedev-api";
    }

    @Data
    public static class CorsProperties {
        /**
         * Allowed origins for CORS
         */
        private String[] allowedOrigins = {"http://localhost:3000"};

        /**
         * Allowed methods for CORS
         */
        private String[] allowedMethods = {"GET", "POST", "PUT", "DELETE", "OPTIONS"};

        /**
         * Allowed headers for CORS
         */
        private String allowedHeaders = "*";

        /**
         * Allow credentials
         */
        private Boolean allowCredentials = true;

        /**
         * Max age for CORS preflight requests
         */
        private Long maxAge = 3600L;
    }
}