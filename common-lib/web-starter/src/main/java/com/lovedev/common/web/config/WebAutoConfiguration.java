package com.lovedev.common.web.config;

import com.lovedev.common.web.exception.GlobalExceptionHandler;
import com.lovedev.common.web.filter.RequestResponseLoggingFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

/**
 * Auto-configuration for LoveDev Web Starter
 * Automatically configures common web components for microservices
 */
@AutoConfiguration
@EnableConfigurationProperties(WebProperties.class)
@ConditionalOnProperty(
        prefix = "app.web",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@Slf4j
public class WebAutoConfiguration {

    private final WebProperties webProperties;

    public WebAutoConfiguration(WebProperties webProperties) {
        this.webProperties = webProperties;
        log.info("🌐 LoveDev Web Starter Auto-Configuration Enabled");
    }

    /**
     * Register GlobalExceptionHandler
     * Provides consistent error handling across all services
     */
    @Bean
    @ConditionalOnMissingBean
    public GlobalExceptionHandler globalExceptionHandler() {
        log.info("📋 Configuring Global Exception Handler");
        return new GlobalExceptionHandler();
    }

    /**
     * Register RequestResponseLoggingFilter
     * Logs HTTP requests and responses for debugging
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
            prefix = "app.web.logging",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = false
    )
    public FilterRegistrationBean<RequestResponseLoggingFilter> loggingFilter() {
        log.info("📝 Configuring Request/Response Logging Filter");

        FilterRegistrationBean<RequestResponseLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestResponseLoggingFilter());
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return registrationBean;
    }
}