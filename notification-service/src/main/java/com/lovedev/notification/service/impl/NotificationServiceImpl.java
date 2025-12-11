package com.lovedev.notification.service.impl;

import com.lovedev.common.web.util.PaginationUtils;
import com.lovedev.notification.client.UserServiceClient;
import com.lovedev.common.web.exception.ResourceNotFoundException;
import com.lovedev.notification.mapper.NotificationMapper;
import com.lovedev.notification.model.dto.response.NotificationResponse;
import com.lovedev.common.web.dto.PageResponse;
import com.lovedev.notification.model.entity.Notification;
import com.lovedev.notification.model.enums.NotificationStatus;
import com.lovedev.notification.model.enums.NotificationType;
import com.lovedev.notification.repository.NotificationRepository;
import com.lovedev.notification.service.NotificationService;
import com.lovedev.common.security.util.SecurityHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final UserServiceClient userServiceClient;

    /**
     * Get current user ID from security context
     */
    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UUID) {
            return (UUID) authentication.getPrincipal();
        }
        // Fallback: try to get from name (which might be the user ID as string)
        try {
            return SecurityHelper.getCurrentUserId();
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Invalid user authentication");
        }
    }

    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> getUserNotifications(int page, int size, String status) {
        UUID userId = getCurrentUserId();

        Pageable pageable = PaginationUtils.createPageable(page, size);
        Page<Notification> notificationPage;

        if (status != null && !status.isEmpty()) {
            NotificationStatus notificationStatus = NotificationStatus.valueOf(status.toUpperCase());
            notificationPage = notificationRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, notificationStatus, pageable);
        } else {
            notificationPage = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        }

     /*   List<NotificationResponse> responses = notificationMapper.toResponseList(notificationPage.getContent());
        List<NotificationResponse> responses = notificationMapper.toResponseList(notificationPage.getContent());
        return PageResponse.of(notificationPage).toBuilder()
                .content(responses)
                .build();*/

        return PageResponse.of(notificationPage, notificationMapper::toResponse);
    }

    @Transactional
    public NotificationResponse markAsRead(UUID notificationId) {
        UUID userId = getCurrentUserId();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("You don't have permission to access this notification");
        }

        notification.markAsRead();
        notification = notificationRepository.save(notification);

        log.info("Notification marked as read: {}", notificationId);
        return notificationMapper.toResponse(notification);
    }

    @Transactional
    public void markAllAsRead() {
        UUID userId = getCurrentUserId();
        notificationRepository.markAllAsReadByUserId(userId, LocalDateTime.now());
        log.info("All notifications marked as read for user: {}", userId);
    }

    @Transactional
    public void deleteNotification(UUID notificationId) {
        UUID userId = getCurrentUserId();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("You don't have permission to delete this notification");
        }

        notificationRepository.delete(notification);
        log.info("Notification deleted: {}", notificationId);
    }

    @Transactional
    public void deleteAllNotifications() {
        UUID userId = getCurrentUserId();
        notificationRepository.deleteAllByUserId(userId);
        log.info("All notifications deleted for user: {}", userId);
    }

    @Transactional
    public void sendTestNotification() {
        UUID userId = getCurrentUserId();

        Notification notification = Notification.builder()
                .userId(userId)
                .title("Test Notification")
                .body("This is a test notification from LoveDev API")
                .type(NotificationType.INFO)
                .status(NotificationStatus.UNREAD)
                .sentAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
        log.info("Test notification sent to user: {}", userId);
    }

    @Transactional(readOnly = true)
    public Long getUnreadCount() {
        UUID userId = getCurrentUserId();
        return notificationRepository.countByUserIdAndStatus(userId, NotificationStatus.UNREAD);
    }
}