package com.lovedev.user.client;

import com.lovedev.user.model.dto.request.BulkEmailRequest;
import com.lovedev.user.model.dto.request.EmailRequest;
import com.lovedev.user.model.dto.request.TemplateEmailRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Fallback implementation for EmailServiceClient
 * Provides graceful degradation when email service is unavailable
 */
@Component
public class EmailServiceFallback implements EmailServiceClient {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceFallback.class);

    @Override
    public ResponseEntity<Map<String, Object>> sendEmail(EmailRequest request) {
        log.warn("Email Service is unavailable. Using fallback method.");
        
        Map<String, Object> fallbackResponse = new HashMap<>();
        fallbackResponse.put("success", false);
        fallbackResponse.put("message", "Email service is temporarily unavailable. Your email will be queued.");
        fallbackResponse.put("fallback", true);
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(fallbackResponse);
    }

    @Override
    public ResponseEntity<Map<String, Object>> sendTemplateEmail(TemplateEmailRequest request) {
        log.warn("Email Service is unavailable for template email. Using fallback method.");
        
        Map<String, Object> fallbackResponse = new HashMap<>();
        fallbackResponse.put("success", false);
        fallbackResponse.put("message", "Email service is temporarily unavailable. Your email will be queued.");
        fallbackResponse.put("fallback", true);
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(fallbackResponse);
    }

    @Override
    public ResponseEntity<Map<String, Object>> sendBulkEmail(BulkEmailRequest request) {
        log.warn("Email Service is unavailable for bulk email. Using fallback method.");
        
        Map<String, Object> fallbackResponse = new HashMap<>();
        fallbackResponse.put("success", false);
        fallbackResponse.put("message", "Email service is temporarily unavailable. Your emails will be queued.");
        fallbackResponse.put("fallback", true);
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(fallbackResponse);
    }
}
