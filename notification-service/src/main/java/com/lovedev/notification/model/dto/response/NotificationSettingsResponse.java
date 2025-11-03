package com.lovedev.notification.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettingsResponse {
    private UUID id;
    private UUID userId;
    private Boolean pushEnabled;
    private Boolean emailEnabled;
    private Boolean inAppEnabled;
    private Boolean marketingEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}