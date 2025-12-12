package com.lovedev.email.kafka;

import com.lovedev.common.messaging.constant.KafkaTopics;  // ✅ Import from starter
import com.lovedev.email.model.dto.request.UserEventRequest;
import com.lovedev.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * Kafka Consumer for User Events
 * Updated to use messaging-starter infrastructure
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final EmailService emailService;

    @KafkaListener(
            topics = KafkaTopics.EMAIL_VERIFY,
            groupId = "email-service-group"
    )
    public void handleVerifyEmail(
            @Payload UserEventRequest event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        log.info("Received user verify email event from topic: {}, partition: {}, offset: {}, userId: {}",
                topic, partition, offset, event.getUserId());

        try {
            String email = (String) event.getData().get("email");
            String firstName = (String) event.getData().get("firstName");
            String tokenVerify = (String) event.getData().get("verificationToken");

            if (email == null || firstName == null || tokenVerify == null) {
                log.error("Missing required fields in event data. Email: {}, FirstName: {}, Token: {}",
                        email != null, firstName != null, tokenVerify != null);
                acknowledgment.acknowledge(); // Acknowledge to skip this bad message
                return;
            }

            emailService.sendVerificationEmail(email, tokenVerify, firstName);
            log.info("Successfully sent verification email to: {}", email);

            // Manually acknowledge the message
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error sending verification email for user: {}. Error: {}",
                    event.getUserId(), e.getMessage(), e);
            // Don't acknowledge - message will be retried
        }
    }

    @KafkaListener(
            topics = KafkaTopics.EMAIL_WELCOME,  // ✅ Use constant
            groupId = "email-service-group"
    )
    public void handleEmailWelcome(
            @Payload UserEventRequest event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        log.info("Received user welcome email event from topic: {}, partition: {}, offset: {}, userId: {}",
                topic, partition, offset, event.getUserId());

        try {
            String email = (String) event.getData().get("email");
            String firstName = (String) event.getData().get("firstName");

            if (email == null || firstName == null) {
                log.error("Missing required fields in event data. Email: {}, FirstName: {}",
                        email != null, firstName != null);
                acknowledgment.acknowledge(); // Acknowledge to skip this bad message
                return;
            }

            emailService.sendWelcomeEmail(email, firstName);
            log.info("Successfully sent welcome email to: {}", email);

            // Manually acknowledge the message
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error sending welcome email for user: {}. Error: {}",
                    event.getUserId(), e.getMessage(), e);
            // Don't acknowledge - message will be retried
        }
    }

    @KafkaListener(
            topics = KafkaTopics.EMAIL_RESET_PASSWORD,  // ✅ Use constant
            groupId = "email-service-group"
    )
    public void handleEmailResetPassword(
            @Payload UserEventRequest event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        log.info("Received user reset password email event from topic: {}, partition: {}, offset: {}, userId: {}",
                topic, partition, offset, event.getUserId());

        try {
            String email = (String) event.getData().get("email");
            String firstName = (String) event.getData().get("firstName");
            String token = (String) event.getData().get("token");

            if (email == null || firstName == null || token == null) {
                log.error("Missing required fields in event data. Email: {}, FirstName: {}, Token: {}",
                        email != null, firstName != null, token != null);
                acknowledgment.acknowledge(); // Acknowledge to skip this bad message
                return;
            }

            emailService.sendPasswordResetEmail(email, token, firstName);
            log.info("Successfully sent password reset email to: {}", email);

            // Manually acknowledge the message
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error sending password reset email for user: {}. Error: {}",
                    event.getUserId(), e.getMessage(), e);
            // Don't acknowledge - message will be retried
        }
    }
}