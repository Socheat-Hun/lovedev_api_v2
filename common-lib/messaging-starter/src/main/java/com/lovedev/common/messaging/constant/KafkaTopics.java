package com.lovedev.common.messaging.constant;

/**
 * Kafka topic names used across all microservices
 * Centralized to prevent typos and ensure consistency
 */
public final class KafkaTopics {

    private KafkaTopics() {
        throw new UnsupportedOperationException("Utility class");
    }

    // Email topics
    public static final String EMAIL_VERIFY = "email.verify";
    public static final String EMAIL_WELCOME = "email.welcome";
    public static final String EMAIL_RESET_PASSWORD = "email.reset.password";

    // User domain events
    public static final String USER_EVENTS = "user-events";

    // Notification domain events
    public static final String NOTIFICATION_EVENTS = "notification-events";

    // Dead letter topics for error handling
    public static final String DLT_EMAIL_VERIFY = "email.verify-dlt";
    public static final String DLT_EMAIL_WELCOME = "email.welcome-dlt";
    public static final String DLT_EMAIL_RESET_PASSWORD = "email.reset.password-dlt";
}