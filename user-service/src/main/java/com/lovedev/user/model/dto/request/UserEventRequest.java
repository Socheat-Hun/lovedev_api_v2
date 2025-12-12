package com.lovedev.user.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * User event request DTO for Kafka messages
 * Used for email-related events
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEventRequest {

    /**
     * User ID
     */
    private UUID userId;

    /**
     * Event type (USER_VERIFY_EMAIL, USER_WELCOME_EMAIL, USER_RESET_PASSWORD)
     */
    private String eventType;

    /**
     * Event data payload
     */
    private Map<String, Object> data;
}