package com.lovedev.user.kafka;

import com.lovedev.common.messaging.constant.KafkaTopics;
import com.lovedev.user.model.dto.request.UserEventRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Kafka Producer for User Events
 * Publishes user-related events to Kafka topics
 *
 * Updated to use messaging-starter infrastructure
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;  // ✅ From messaging-starter

    /**
     * Publish user verify email event
     */
    public void publishUserVerifyEmail(UUID userId, Map<String, Object> userData) {
        UserEventRequest event = UserEventRequest.builder()
                .userId(userId)
                .eventType("USER_VERIFY_EMAIL")
                .data(userData)
                .build();

        publishEvent(KafkaTopics.EMAIL_VERIFY, userId.toString(), event);  // ✅ Use constant
    }

    /**
     * Publish user welcome email event
     */
    public void publishUserWelcomeEmail(UUID userId, Map<String, Object> userData) {
        UserEventRequest event = UserEventRequest.builder()
                .userId(userId)
                .eventType("USER_WELCOME_EMAIL")
                .data(userData)
                .build();

        publishEvent(KafkaTopics.EMAIL_WELCOME, userId.toString(), event);  // ✅ Use constant
    }

    /**
     * Publish user reset password event
     */
    public void publishUserResetPassword(UUID userId, Map<String, Object> userData) {
        UserEventRequest event = UserEventRequest.builder()
                .userId(userId)
                .eventType("USER_RESET_PASSWORD")
                .data(userData)
                .build();

        publishEvent(KafkaTopics.EMAIL_RESET_PASSWORD, userId.toString(), event);  // ✅ Use constant
    }

    /**
     * Generic method to publish events to Kafka
     */
    private void publishEvent(String topic, String key, UserEventRequest event) {
        try {
            log.info("Publishing event to topic: {} with key: {}, eventType: {}",
                    topic, key, event.getEventType());

            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send(topic, key, event);  // ✅ Simple send

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Successfully published event to topic: {} with key: {} - Partition: {}, Offset: {}",
                            topic, key,
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to publish event to topic: {} with key: {}", topic, key, ex);
                }
            });
        } catch (Exception e) {
            log.error("Exception while publishing event to topic: {}", topic, e);
            throw new RuntimeException("Failed to publish event", e);
        }
    }
}