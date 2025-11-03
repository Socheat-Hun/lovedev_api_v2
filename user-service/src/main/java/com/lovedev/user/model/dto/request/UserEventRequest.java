package com.lovedev.user.model.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
public class UserEventRequest {
    private UUID userId;
    private String eventType;
    private Map<String, Object> data;
    private long timestamp = System.currentTimeMillis();

    public UserEventRequest(UUID userId, String eventType, Map<String, Object> data) {
        this.userId = userId;
        this.eventType = eventType;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
}