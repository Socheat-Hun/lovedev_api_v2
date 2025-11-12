package com.lovedev.user.service;

import com.lovedev.user.model.dto.response.OAuth2AuthResult;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.UUID;

/**
 * Service interface for handling OAuth2 authentication (Google, GitHub, etc.)
 */
public interface OAuth2Service {

    /**
     * Process OAuth2 login/registration
     * If user exists, login. If not, create new user with USER role.
     */
    OAuth2AuthResult processOAuth2Login(OAuth2User oAuth2User, String registrationId);
}
