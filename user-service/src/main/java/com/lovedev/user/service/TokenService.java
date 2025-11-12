package com.lovedev.user.service;

import com.lovedev.user.model.entity.RefreshToken;
import com.lovedev.user.model.entity.User;

/**
 * Service interface for token management operations
 */
public interface TokenService {

    /**
     * Create new refresh token for user
     */
    RefreshToken createRefreshToken(User user);

    /**
     * Verify and validate refresh token
     */
    RefreshToken verifyRefreshToken(String token);

    /**
     * Revoke single refresh token
     */
    void revokeRefreshToken(String token);

    /**
     * Revoke all refresh tokens for user
     */
    void revokeAllUserTokens(User user);

    /**
     * Clean up expired tokens (scheduled task)
     */
    void cleanupExpiredTokens();
}