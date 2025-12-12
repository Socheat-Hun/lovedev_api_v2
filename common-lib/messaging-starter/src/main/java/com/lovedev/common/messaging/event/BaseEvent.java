package com.lovedev.common.messaging.event;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base class for all events in the system
 * Provides common fields for event tracking and correlation
 *
 * NOTE: This is optional to extend - services can use their own event DTOs
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "eventType",
        defaultImpl = BaseEvent.class
)
public class BaseEvent {

    /**
     * Unique event ID
     */
    private String eventId = UUID.randomUUID().toString();

    /**
     * Event type identifier (e.g., "USER_VERIFY_EMAIL", "USER_WELCOME_EMAIL")
     */
    private String eventType;

    /**
     * Timestamp when event was created
     */
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Source service that produced this event
     */
    private String source;

    /**
     * Event version for backward compatibility
     */
    private String version = "1.0";

    /**
     * Correlation ID for tracking across services
     */
    private String correlationId;

    /**
     * User ID who triggered this event (if applicable)
     */
    private UUID userId;
}