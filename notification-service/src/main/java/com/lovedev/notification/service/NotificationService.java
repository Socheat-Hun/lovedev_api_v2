package com.lovedev.notification.service;

import com.lovedev.notification.model.dto.response.NotificationResponse;
import com.lovedev.common.web.dto.PageResponse;

import java.util.UUID;

/**
 * Service interface for notification management operations
 */
public interface NotificationService {

    /**
     * Get user notifications with pagination
     */
    PageResponse<NotificationResponse> getUserNotifications(int page, int size, String status);

    /**
     * Mark specific notification as read
     */
    NotificationResponse markAsRead(UUID notificationId);

    /**
     * Mark all user notifications as read
     */
    void markAllAsRead();

    /**
     * Delete specific notification
     */
    void deleteNotification(UUID notificationId);

    /**
     * Delete all user notifications
     */
    void deleteAllNotifications();

    /**
     * Send test notification to current user
     */
    void sendTestNotification();

    /**
     * Get unread notification count
     */
    Long getUnreadCount();
}