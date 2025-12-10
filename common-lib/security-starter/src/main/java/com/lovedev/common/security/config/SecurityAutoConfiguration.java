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
        prefix = "app",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@Slf4j
public class SecurityAutoConfiguration {
    private final SecurityProperties securityProperties;

    public SecurityAutoConfiguration(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
        log.info("🔐 LoveDev Security Starter Auto-Configuration Enabled");
        // Validate JWT secret is provided
        if (securityProperties.getJwt().getSecret() == null ||
                securityProperties.getJwt().getSecret().isEmpty()) {
            throw new IllegalStateException(
                    "JWT secret is required. Please set 'app.security.jwt.secret' in your application.yml"
            );
        }
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
     * Create CORS configuration source
     * Configures Cross-Origin Resource Sharing settings
     *
     * @return CorsConfigurationSource instance
     */
    @Bean
    @ConditionalOnMissingBean
    public CorsConfigurationSource corsConfigurationSource() {
        SecurityProperties.CorsProperties cors = securityProperties.getCors();

        if (cors.getAllowedOrigins() == null || Arrays.asList(cors.getAllowedOrigins()).isEmpty()) {
            log.warn("No CORS allowed origins configured. CORS will be disabled.");
            return request -> null;
        }

        System.out.printf("::::: >> "+cors.getAllowedOrigins());
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(cors.getAllowedOrigins()));
        configuration.setAllowedMethods(Arrays.asList(cors.getAllowedMethods()));
        configuration.setAllowedHeaders(Arrays.asList(cors.getAllowedHeaders()));
        configuration.setAllowCredentials(cors.getAllowCredentials());
        configuration.setMaxAge(cors.getMaxAge());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        log.info("CORS configuration created with allowed origins: {}", cors.getAllowedOrigins());

        return source;
    }
}