package com.lovedev.user.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * OAuth2 Authentication Result DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2AuthResult {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private UUID userId;
    private String email;
    private boolean isNewUser;
}