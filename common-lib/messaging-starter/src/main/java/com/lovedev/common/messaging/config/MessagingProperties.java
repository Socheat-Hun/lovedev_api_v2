package com.lovedev.common.messaging.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for messaging starter
 */
@Data
@ConfigurationProperties(prefix = "app.messaging")
public class MessagingProperties {

    /**
     * Enable/disable messaging auto-configuration
     */
    private boolean enabled = true;

    /**
     * Kafka bootstrap servers
     */
    private String bootstrapServers = "localhost:9092";

    /**
     * Producer configuration
     */
    private ProducerProperties producer = new ProducerProperties();

    /**
     * Consumer configuration
     */
    private ConsumerProperties consumer = new ConsumerProperties();

    @Data
    public static class ProducerProperties {
        /**
         * Number of acknowledgments the producer requires
         * Options: 0, 1, all
         */
        private String acks = "all";

        /**
         * Number of retries
         */
        private int retries = 3;

        /**
         * Batch size in bytes
         */
        private int batchSize = 16384;

        /**
         * Linger time in milliseconds
         */
        private int lingerMs = 1;

        /**
         * Buffer memory in bytes
         */
        private long bufferMemory = 33554432L;

        /**
         * Compression type: none, gzip, snappy, lz4, zstd
         */
        private String compressionType = "snappy";

        /**
         * Enable idempotence
         */
        private boolean enableIdempotence = true;

        /**
         * Max in-flight requests per connection
         */
        private int maxInFlightRequestsPerConnection = 5;
    }

    @Data
    public static class ConsumerProperties {
        /**
         * Consumer group ID
         */
        private String groupId;

        /**
         * Auto offset reset: earliest, latest, none
         */
        private String autoOffsetReset = "earliest";

        /**
         * Enable auto commit
         */
        private boolean enableAutoCommit = false;

        /**
         * Max poll records
         */
        private int maxPollRecords = 500;

        /**
         * Max poll interval in milliseconds
         */
        private int maxPollIntervalMs = 300000;

        /**
         * Session timeout in milliseconds
         */
        private int sessionTimeoutMs = 10000;

        /**
         * Heartbeat interval in milliseconds
         */
        private int heartbeatIntervalMs = 3000;
    }
}