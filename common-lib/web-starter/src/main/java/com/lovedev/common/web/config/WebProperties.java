package com.lovedev.common.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for web starter
 */
@Data
@ConfigurationProperties(prefix = "app.web")
public class WebProperties {

    /**
     * Enable/disable web starter auto-configuration
     * Default: true
     */
    private boolean enabled = true;

    /**
     * Logging configuration
     */
    private LoggingProperties logging = new LoggingProperties();

    /**
     * Pagination configuration
     */
    private PaginationProperties pagination = new PaginationProperties();

    @Data
    public static class LoggingProperties {
        /**
         * Enable/disable request/response logging
         * Default: false (disabled for performance)
         */
        private boolean enabled = false;

        /**
         * Log request body
         */
        private boolean logRequestBody = true;

        /**
         * Log response body
         */
        private boolean logResponseBody = false;
    }

    @Data
    public static class PaginationProperties {
        /**
         * Default page size
         */
        private int defaultPageSize = 10;

        /**
         * Maximum page size
         */
        private int maxPageSize = 100;

        /**
         * Default sort field
         */
        private String defaultSortBy = "createdAt";

        /**
         * Default sort direction
         */
        private String defaultSortDirection = "desc";
    }
}