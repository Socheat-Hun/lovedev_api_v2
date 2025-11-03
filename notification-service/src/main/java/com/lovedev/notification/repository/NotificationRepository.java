package com.lovedev.notification.repository;

import com.lovedev.notification.model.entity.Notification;
import com.lovedev.notification.model.enums.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    Page<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    Page<Notification> findByUserIdAndStatusOrderByCreatedAtDesc(UUID userId, NotificationStatus status, Pageable pageable);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId AND n.status = :status")
    Long countByUserIdAndStatus(@Param("userId") UUID userId, @Param("status") NotificationStatus status);

    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.status = 'UNREAD' ORDER BY n.createdAt DESC")
    List<Notification> findUnreadByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE Notification n SET n.status = 'READ', n.readAt = :readAt WHERE n.userId = :userId AND n.status = 'UNREAD'")
    void markAllAsReadByUserId(@Param("userId") UUID userId, @Param("readAt") LocalDateTime readAt);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.createdAt < :expiryDate")
    void deleteExpiredNotifications(@Param("expiryDate") LocalDateTime expiryDate);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.userId = :userId")
    void deleteAllByUserId(@Param("userId") UUID userId);
}