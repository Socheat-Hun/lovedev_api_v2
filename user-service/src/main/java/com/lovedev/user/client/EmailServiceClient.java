package com.lovedev.user.client;

import com.lovedev.user.client.impl.EmailServiceFallback;
import com.lovedev.user.model.dto.request.BulkEmailRequest;
import com.lovedev.user.model.dto.request.EmailRequest;
import com.lovedev.user.model.dto.request.TemplateEmailRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * Feign Client for Email Service
 * Uses Eureka for service discovery and Resilience4j for circuit breaking
 */
@FeignClient(
    name = "email-service",
    fallback = EmailServiceFallback.class
)
public interface EmailServiceClient {

    @PostMapping("/api/emails/send")
    ResponseEntity<Map<String, Object>> sendEmail(@RequestBody EmailRequest request);

    @PostMapping("/api/emails/send-template")
    ResponseEntity<Map<String, Object>> sendTemplateEmail(@RequestBody TemplateEmailRequest request);

    @PostMapping("/api/emails/send-bulk")
    ResponseEntity<Map<String, Object>> sendBulkEmail(@RequestBody BulkEmailRequest request);
}
