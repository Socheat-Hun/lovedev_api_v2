package com.lovedev.email.kafka;
import com.lovedev.email.model.dto.request.UserEventRequest;
import com.lovedev.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = "email.verify", groupId = "email-service-group")
    public void handleVerifyEmail(UserEventRequest event) {
        log.info("Received user verify email event: {}", event.getUserId());
        try {
            String email = (String) event.getData().get("email");
            String firstName = (String) event.getData().get("firstName");
            String tokenVerify = (String) event.getData().get("firstName");
            emailService.sendVerificationEmail(email,tokenVerify,firstName);
        } catch (Exception e) {
            log.error("Error sending email for user: {}", event.getUserId(), e);
        }
    }

    @KafkaListener(topics = "email.welcome", groupId = "email-service-group")
    public void handleEmailWelcome(UserEventRequest event) {
        log.info("Received user welcome email event: {}", event.getUserId());
        try {
            String email = (String) event.getData().get("email");
            String firstName = (String) event.getData().get("firstName");
            emailService.sendWelcomeEmail(email,firstName);
        } catch (Exception e) {
            log.error("Error sending email for user: {}", event.getUserId(), e);
        }
    }

    @KafkaListener(topics = "email.reset.password", groupId = "email-service-group")
    public void handleEmailResetPassword(UserEventRequest event) {
        log.info("Received user reset password email event: {}", event.getUserId());
        try {
            String email = (String) event.getData().get("email");
            String firstName = (String) event.getData().get("firstName");
            String token = (String) event.getData().get("token");
            emailService.sendPasswordResetEmail(email,token,firstName);
        } catch (Exception e) {
            log.error("Error sending email for user: {}", event.getUserId(), e);
        }
    }
}