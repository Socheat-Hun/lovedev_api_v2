package com.lovedev.common.security.config;

import com.lovedev.common.security.jwt.JwtAuthenticationFilter;
import com.lovedev.common.security.jwt.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Auto-configuration for LoveDev Security Starter
 * Automatically configures JWT token provider and authentication filter
 */
@AutoConfiguration
@EnableConfigurationProperties(SecurityProperties.class)
@ConditionalOnProperty(
        prefix = "app.security",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@Slf4j
public class SecurityAutoConfiguration {

    public SecurityAutoConfiguration() {
        log.info("🔐 LoveDev Security Starter Auto-Configuration Enabled");
    }

    /**
     * JWT Token Provider Bean
     */
    @Bean
    @ConditionalOnMissingBean
    public JwtTokenProvider jwtTokenProvider(SecurityProperties properties) {
        log.info("📝 Configuring JWT Token Provider");

        SecurityProperties.JwtProperties jwt = properties.getJwt();

        if (jwt.getSecret() == null || jwt.getSecret().isEmpty()) {
            throw new IllegalStateException(
                    "JWT secret must be configured in application.yml: app.security.jwt.secret"
            );
        }

        return new JwtTokenProvider(
                jwt.getSecret(),
                jwt.getExpiration(),
                jwt.getRefreshExpiration(),
                jwt.getIssuer()
        );
    }

    /**
     * JWT Authentication Filter Bean
     */
    @Bean
    @ConditionalOnMissingBean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        log.info("🔒 Configuring JWT Authentication Filter");
        return new JwtAuthenticationFilter(jwtTokenProvider);
    }

    /**
     * CORS Configuration Source Bean
     */
    @Bean
    @ConditionalOnMissingBean(name = "corsConfigurationSource")
    public CorsConfigurationSource corsConfigurationSource(SecurityProperties properties) {
        log.info("🌐 Configuring CORS");

        SecurityProperties.CorsProperties cors = properties.getCors();

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(cors.getAllowedOrigins()));
        configuration.setAllowedMethods(Arrays.asList(cors.getAllowedMethods()));
        configuration.addAllowedHeader(cors.getAllowedHeaders());
        configuration.setAllowCredentials(cors.getAllowCredentials());
        configuration.setMaxAge(cors.getMaxAge());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}