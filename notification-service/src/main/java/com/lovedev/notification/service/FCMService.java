package com.lovedev.notification.service;

import com.lovedev.notification.model.dto.request.FCMTokenRequest;
import com.lovedev.notification.model.dto.request.NotificationSettingsRequest;
import com.lovedev.notification.model.dto.request.SendBulkNotificationRequest;
import com.lovedev.notification.model.dto.request.SendNotificationRequest;
import com.lovedev.notification.model.dto.response.NotificationSettingsResponse;

import java.util.Map;

/**
 * Service interface for Firebase Cloud Messaging (FCM) operations
 */
public interface FCMService {

    /**
     * Register FCM token for push notifications
     */
    void registerFCMToken(FCMTokenRequest request);

    /**
     * Remove FCM token
     */
    void removeFCMToken(String token);

    /**
     * Remove all FCM tokens for current user
     */
    void removeAllUserFCMTokens();

    /**
     * Get user notification settings
     */
    NotificationSettingsResponse getNotificationSettings();

    /**
     * Get notification statistics (unread, read, total counts)
     */
    Map<String, Long> getNotificationStats();

    /**
     * Update user notification settings
     */
    NotificationSettingsResponse updateNotificationSettings(NotificationSettingsRequest request);

    /**
     * Send notification to specific user
     */
    void sendNotification(SendNotificationRequest request);

    /**
     * Send bulk notification to all users
     */
    void sendBulkNotification(SendBulkNotificationRequest request);

    /**
     * Cleanup old FCM tokens (scheduled task)
     */
    void cleanupOldTokens();

    /**
     * Cleanup old notifications (scheduled task)
     */
    void cleanupOldNotifications();
}