package com.lovedev.user.model.dto.request;

import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class BulkNotificationRequest {
    private java.util.List<UUID> userIds;
    private String title;
    private String message;
    private String type;
    private Map<String, Object> data;
}
