package com.lovedev.notification.service.impl;

import com.google.firebase.messaging.*;
import com.lovedev.notification.client.UserServiceClient;
import com.lovedev.notification.exception.UnauthorizedException;
import com.lovedev.notification.model.dto.request.FCMTokenRequest;
import com.lovedev.notification.model.dto.request.NotificationSettingsRequest;
import com.lovedev.notification.model.dto.request.SendBulkNotificationRequest;
import com.lovedev.notification.model.dto.request.SendNotificationRequest;
import com.lovedev.notification.model.dto.response.NotificationSettingsResponse;
import com.lovedev.notification.model.entity.FCMToken;
import com.lovedev.notification.model.entity.Notification;
import com.lovedev.notification.model.entity.NotificationSettings;
import com.lovedev.notification.model.enums.NotificationStatus;
import com.lovedev.notification.repository.FCMTokenRepository;
import com.lovedev.notification.repository.NotificationRepository;
import com.lovedev.notification.repository.NotificationSettingsRepository;
import com.lovedev.notification.service.FCMService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FCMServiceImpl implements FCMService {

    private final FCMTokenRepository fcmTokenRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationSettingsRepository notificationSettingsRepository;
    private final UserServiceClient userServiceClient;

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UUID) {
            return (UUID) authentication.getPrincipal();
        }
        try {
            return UUID.fromString(authentication.getName());
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("Invalid user authentication");
        }
    }

    @Transactional
    public void registerFCMToken(FCMTokenRequest request) {
        UUID userId = getCurrentUserId();

        Optional<FCMToken> existingToken = fcmTokenRepository.findByTokenAndActiveTrue(request.getFcmToken());

        if (existingToken.isPresent()) {
            FCMToken token = existingToken.get();
            token.setUpdatedAt(LocalDateTime.now());
            fcmTokenRepository.save(token);
            log.info("FCM token updated for user: {}", userId);
            return;
        }

        FCMToken fcmToken = FCMToken.builder()
                .userId(userId)
                .token(request.getFcmToken())
                .deviceType(request.getDeviceType())
                .deviceId(request.getDeviceId())
                .active(true)
                .build();

        fcmTokenRepository.save(fcmToken);
        log.info("FCM token registered for user: {}", userId);
    }

    @Transactional
    public void removeFCMToken(String token) {
        fcmTokenRepository.deactivateToken(token);
        log.info("FCM token deactivated: {}", token);
    }

    @Transactional
    public void removeAllUserFCMTokens() {
        UUID userId = getCurrentUserId();
        fcmTokenRepository.deactivateAllUserTokens(userId);
        log.info("All FCM tokens deactivated for user: {}", userId);
    }

    @Transactional(readOnly = true)
    public NotificationSettingsResponse getNotificationSettings() {
        UUID userId = getCurrentUserId();
        NotificationSettings settings = notificationSettingsRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultSettings(userId));

        return NotificationSettingsResponse.builder()
                .id(settings.getId())
                .userId(settings.getUserId())
                .pushEnabled(settings.getPushEnabled())
                .emailEnabled(settings.getEmailEnabled())
                .inAppEnabled(settings.getInAppEnabled())
                .marketingEnabled(settings.getMarketingEnabled())
                .createdAt(settings.getCreatedAt())
                .updatedAt(settings.getUpdatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getNotificationStats() {
    //    UUID userId = SecurityHelper.getCurrentUserId();
     //   User user = userRepository.findById(userId)
       //         .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    //    Long unreadCount = notificationRepository.countByUserAndStatus(user, NotificationStatus.UNREAD);
    //    Long readCount = notificationRepository.countByUserAndStatus(user, NotificationStatus.READ);
  //      Long totalCount = unreadCount + readCount;

        Map<String, Long> stats = new HashMap<>();
      //  stats.put("unread", unreadCount);
    //    stats.put("read", readCount);
  //      stats.put("total", totalCount);

        return stats;
    }

    @Transactional
    public NotificationSettingsResponse updateNotificationSettings(NotificationSettingsRequest request) {
        UUID userId = getCurrentUserId();
        NotificationSettings settings = notificationSettingsRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultSettings(userId));

        if (request.getPushEnabled() != null) {
            settings.setPushEnabled(request.getPushEnabled());
        }
        if (request.getEmailEnabled() != null) {
            settings.setEmailEnabled(request.getEmailEnabled());
        }
        if (request.getInAppEnabled() != null) {
            settings.setInAppEnabled(request.getInAppEnabled());
        }
        if (request.getMarketingEnabled() != null) {
            settings.setMarketingEnabled(request.getMarketingEnabled());
        }

        settings = notificationSettingsRepository.save(settings);
        log.info("Notification settings updated for user: {}", userId);

        return NotificationSettingsResponse.builder()
                .id(settings.getId())
                .userId(settings.getUserId())
                .pushEnabled(settings.getPushEnabled())
                .emailEnabled(settings.getEmailEnabled())
                .inAppEnabled(settings.getInAppEnabled())
                .marketingEnabled(settings.getMarketingEnabled())
                .createdAt(settings.getCreatedAt())
                .updatedAt(settings.getUpdatedAt())
                .build();
    }

    private NotificationSettings createDefaultSettings(UUID userId) {
        NotificationSettings settings = NotificationSettings.builder()
                .userId(userId)
                .pushEnabled(true)
                .emailEnabled(true)
                .inAppEnabled(true)
                .marketingEnabled(false)
                .build();
        return notificationSettingsRepository.save(settings);
    }

    @Async
    @Transactional
    public void sendNotification(SendNotificationRequest request) {
        try {
            UUID userId = getCurrentUserId();

            NotificationSettings settings = notificationSettingsRepository.findByUserId(userId)
                    .orElseGet(() -> createDefaultSettings(userId));

            if (!settings.getPushEnabled()) {
                log.info("Push notifications disabled for user: {}", userId);
                return;
            }

            List<FCMToken> tokens = fcmTokenRepository.findActiveTokensByUserId(userId);
            if (tokens.isEmpty()) {
                log.warn("No active FCM tokens found for user: {}", userId);
                return;
            }

            Notification notification = Notification.builder()
                    .userId(userId)
                    .title(request.getTitle())
                    .body(request.getBody())
                    .type(request.getType())
                    .status(NotificationStatus.SENT)
                    .data(request.getData())
                    .actionUrl(request.getActionUrl())
                    .sentAt(LocalDateTime.now())
                    .build();
            notificationRepository.save(notification);

            com.google.firebase.messaging.Notification fcmNotification = com.google.firebase.messaging.Notification.builder()
                    .setTitle(request.getTitle())
                    .setBody(request.getBody())
                    .build();

            for (FCMToken token : tokens) {
                try {
                    Message message = Message.builder()
                            .setToken(token.getToken())
                            .setNotification(fcmNotification)
                            .putData("type", request.getType().toString())
                            .putData("actionUrl", request.getActionUrl() != null ? request.getActionUrl() : "")
                            .build();

                    String response = FirebaseMessaging.getInstance().send(message);
                    log.info("Successfully sent FCM message: {}", response);
                } catch (FirebaseMessagingException e) {
                    log.error("Error sending FCM message to token: {}", token.getToken(), e);
                    if (e.getErrorCode().equals("invalid-registration-token") ||
                            e.getErrorCode().equals("registration-token-not-registered")) {
                        fcmTokenRepository.deactivateToken(token.getToken());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error sending notification", e);
        }
    }

    // ============================================
    // Notification Statistics
    // ============================================

    @Async
    @Transactional
    public void sendBulkNotification(SendBulkNotificationRequest request) {
        try {
            List<FCMToken> allTokens = fcmTokenRepository.findAllActiveTokens();
            if (allTokens.isEmpty()) {
                log.warn("No active FCM tokens found for bulk notification");
                return;
            }

            com.google.firebase.messaging.Notification fcmNotification = com.google.firebase.messaging.Notification.builder()
                    .setTitle(request.getTitle())
                    .setBody(request.getBody())
                    .build();

            Map<UUID, List<FCMToken>> tokensByUser = allTokens.stream()
                    .collect(Collectors.groupingBy(FCMToken::getUserId));

            for (Map.Entry<UUID, List<FCMToken>> entry : tokensByUser.entrySet()) {
                UUID userId = entry.getKey();
                List<FCMToken> userTokens = entry.getValue();

                NotificationSettings settings = notificationSettingsRepository.findByUserId(userId)
                        .orElseGet(() -> createDefaultSettings(userId));

                if (!settings.getPushEnabled()) {
                    continue;
                }

                Notification notification = Notification.builder()
                        .userId(userId)
                        .title(request.getTitle())
                        .body(request.getBody())
                        .type(request.getType())
                        .status(NotificationStatus.SENT)
                        .sentAt(LocalDateTime.now())
                        .build();
                notificationRepository.save(notification);

                for (FCMToken token : userTokens) {
                    try {
                        Message message = Message.builder()
                                .setToken(token.getToken())
                                .setNotification(fcmNotification)
                                .putData("type", request.getType().toString())
                                .build();

                        String response = FirebaseMessaging.getInstance().send(message);
                        log.debug("Sent bulk notification to token: {}", token.getToken());
                    } catch (FirebaseMessagingException e) {
                        log.error("Error sending bulk FCM message", e);
                        if (e.getErrorCode().equals("invalid-registration-token") ||
                                e.getErrorCode().equals("registration-token-not-registered")) {
                            fcmTokenRepository.deactivateToken(token.getToken());
                        }
                    }
                }
            }

            log.info("Bulk notification sent to {} users", tokensByUser.size());
        } catch (Exception e) {
            log.error("Error sending bulk notification", e);
        }
    }

    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void cleanupOldTokens() {
        LocalDateTime expiryDate = LocalDateTime.now().minusDays(90);
        fcmTokenRepository.deactivateOldTokens(expiryDate);
        log.info("Cleaned up FCM tokens older than 90 days");
    }

    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void cleanupOldNotifications() {
        LocalDateTime expiryDate = LocalDateTime.now().minusDays(30);
        notificationRepository.deleteExpiredNotifications(expiryDate);
        log.info("Cleaned up notifications older than 30 days");
    }
}