package com.lovedev.user.client;

import com.lovedev.user.client.impl.NotificationServiceFallback;
import com.lovedev.user.model.dto.request.BulkNotificationRequest;
import com.lovedev.user.model.dto.request.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * Feign Client for Notification Service
 * Uses Eureka for service discovery and Resilience4j for circuit breaking
 */
@FeignClient(
    name = "notification-service",
    fallback = NotificationServiceFallback.class
)
public interface NotificationServiceClient {

    @PostMapping("/api/notifications/send")
    ResponseEntity<Map<String, Object>> sendNotification(@RequestBody NotificationRequest request);

    @PostMapping("/api/notifications/send-bulk")
    ResponseEntity<Map<String, Object>> sendBulkNotification(@RequestBody BulkNotificationRequest request);
}
