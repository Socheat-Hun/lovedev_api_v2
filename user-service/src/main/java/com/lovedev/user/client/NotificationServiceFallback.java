package com.lovedev.user.client;

import com.lovedev.user.model.dto.request.BulkNotificationRequest;
import com.lovedev.user.model.dto.request.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Fallback implementation for NotificationServiceClient
 * Provides graceful degradation when notification service is unavailable
 */
@Component
public class NotificationServiceFallback implements NotificationServiceClient {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceFallback.class);

    @Override
    public ResponseEntity<Map<String, Object>> sendNotification(NotificationRequest request) {
        log.warn("Notification Service is unavailable. Using fallback method.");
        
        Map<String, Object> fallbackResponse = new HashMap<>();
        fallbackResponse.put("success", false);
        fallbackResponse.put("message", "Notification service is temporarily unavailable. Your notification will be queued.");
        fallbackResponse.put("fallback", true);
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(fallbackResponse);
    }

    @Override
    public ResponseEntity<Map<String, Object>> sendBulkNotification(BulkNotificationRequest request) {
        log.warn("Notification Service is unavailable for bulk notification. Using fallback method.");
        
        Map<String, Object> fallbackResponse = new HashMap<>();
        fallbackResponse.put("success", false);
        fallbackResponse.put("message", "Notification service is temporarily unavailable. Your notifications will be queued.");
        fallbackResponse.put("fallback", true);
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(fallbackResponse);
    }
}
