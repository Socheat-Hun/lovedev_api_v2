package com.lovedev.email.controller;

import com.lovedev.email.model.dto.request.SendEmailRequest;
import com.lovedev.email.model.dto.response.ApiResponse;
import com.lovedev.email.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
@Tag(name = "Email", description = "Email Management APIs")
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Send email", description = "Send email to specified recipient")
    public ResponseEntity<ApiResponse<String>> sendEmail(@Valid @RequestBody SendEmailRequest request) {
        emailService.sendEmail(request.getTo(), request.getSubject(), request.getBody());
        return ResponseEntity.ok(ApiResponse.success("Email sent successfully"));
    }
}
