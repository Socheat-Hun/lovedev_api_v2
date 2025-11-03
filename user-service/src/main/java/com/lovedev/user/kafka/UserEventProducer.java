package com.lovedev.user.kafka;

import com.lovedev.user.model.dto.request.UserEventRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Kafka Producer for User Events
 * Publishes user-related events to Kafka topics
 */
@Service
public class UserEventProducer {

    private static final Logger log = LoggerFactory.getLogger(UserEventProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.email-verify:email.verify}")
    private String emailVerifyTopic;

    @Value("${kafka.topics.email-welcome:email.welcome")
    private String emailWelcomeTopic;

    @Value("${kafka.topics.email-reset-password:email.reset.password}")
    private String emailResetPasswordTopic;

    public UserEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publish user verify email event
     */
    public void publishUserVerifyEmail(UUID userId, Map<String, Object> userData) {
        UserEventRequest event = new UserEventRequest(userId, "USER_VERIFY_EMAIL", userData);
        publishEvent(emailVerifyTopic, userId.toString() , event);
    }

    /**
     * Publish user welcome email event
     */
    public void publishUserWelcomeEmail(UUID userId, Map<String, Object> userData) {
        UserEventRequest event = new UserEventRequest(userId, "USER_WELCOME_EMAIL", userData);
        publishEvent(emailWelcomeTopic, userId.toString(), event);
    }

    /**
     * Publish user reset password event
     */
    public void publishUserResetPassword(UUID userId, Map<String, Object> userData) {
        UserEventRequest event = new UserEventRequest(userId, "USER_RESET_PASSWORD", userData);
        publishEvent(emailResetPasswordTopic, userId.toString(), event);
    }

    /**
     * Generic method to publish events to Kafka
     */
    private void publishEvent(String topic, String key, UserEventRequest event) {
        try {
            Message<UserEventRequest> message = MessageBuilder
                    .withPayload(event)
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .setHeader(KafkaHeaders.KEY, key)
                    .build();

            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(message);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Published event to topic: {} with key: {} - Offset: {}", 
                            topic, key, result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to publish event to topic: {} with key: {}", topic, key, ex);
                }
            });
        } catch (Exception e) {
            log.error("Exception while publishing event to topic: {}", topic, e);
        }
    }
}