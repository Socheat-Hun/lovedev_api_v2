package com.lovedev.notification.client;

import com.lovedev.notification.model.dto.client.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.user-service.url:http://user-service:8081}")
    private String userServiceUrl;

    /**
     * Get user by ID from user-service
     */
    public Optional<UserDTO> getUserById(Long userId, String authToken) {
        try {
            String url = userServiceUrl + "/api/users/" + userId;
            HttpHeaders headers = new HttpHeaders();
            if (authToken != null && !authToken.isEmpty()) {
                headers.set("Authorization", authToken);
            }
            
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<UserDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                UserDTO.class
            );
            
            log.debug("Fetched user from user-service: {}", userId);
            return Optional.ofNullable(response.getBody());
        } catch (Exception e) {
            log.error("Error fetching user from user-service: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Get user by email from user-service
     */
    public Optional<UserDTO> getUserByEmail(String email, String authToken) {
        try {
            String url = userServiceUrl + "/api/users/email/" + email;
            HttpHeaders headers = new HttpHeaders();
            if (authToken != null && !authToken.isEmpty()) {
                headers.set("Authorization", authToken);
            }
            
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<UserDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                UserDTO.class
            );
            
            log.debug("Fetched user by email from user-service: {}", email);
            return Optional.ofNullable(response.getBody());
        } catch (Exception e) {
            log.error("Error fetching user by email from user-service: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
