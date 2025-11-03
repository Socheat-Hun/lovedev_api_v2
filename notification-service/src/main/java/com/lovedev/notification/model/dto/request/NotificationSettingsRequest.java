package com.lovedev.notification.model.dto.request;

import lombok.Data;

@Data
public class NotificationSettingsRequest {

    private Boolean pushEnabled;

    private Boolean emailEnabled;

    private Boolean systemNotifications;

    private Boolean accountNotifications;

    private Boolean securityAlerts;
    private Boolean inAppEnabled;
    private Boolean marketingEnabled;
}