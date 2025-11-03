package com.lovedev.user.model.dto.request;

import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class NotificationRequest {
    private UUID userId;
    private String title;
    private String message;
    private String type;
    private Map<String, Object> data;
}
