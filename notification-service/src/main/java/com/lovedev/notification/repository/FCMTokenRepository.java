package com.lovedev.notification.repository;

import com.lovedev.notification.model.entity.FCMToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FCMTokenRepository extends JpaRepository<FCMToken, UUID> {

    Optional<FCMToken> findByTokenAndActiveTrue(String token);

    List<FCMToken> findByUserIdAndActiveTrue(UUID userId);

    @Query("SELECT f FROM FCMToken f WHERE f.userId = :userId AND f.active = true")
    List<FCMToken> findActiveTokensByUserId(@Param("userId") UUID userId);

    @Query("SELECT f FROM FCMToken f WHERE f.active = true")
    List<FCMToken> findAllActiveTokens();

    @Modifying
    @Query("UPDATE FCMToken f SET f.active = false WHERE f.token = :token")
    void deactivateToken(@Param("token") String token);

    @Modifying
    @Query("UPDATE FCMToken f SET f.active = false WHERE f.userId = :userId")
    void deactivateAllUserTokens(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE FCMToken f SET f.active = false WHERE f.updatedAt < :expiryDate")
    void deactivateOldTokens(@Param("expiryDate") LocalDateTime expiryDate);

    boolean existsByTokenAndActiveTrue(String token);
}