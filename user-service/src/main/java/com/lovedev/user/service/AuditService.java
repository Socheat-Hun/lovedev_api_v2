package com.lovedev.user.service;

import com.lovedev.user.model.entity.User;
import com.lovedev.user.model.enums.AuditAction;

import java.util.Map;

/**
 * Service interface for audit logging operations
 */
public interface AuditService {
    /**
     * Log action synchronously (within same transaction)
     * Use this for actions within a transaction (like registration)
     */
    void logAction(User user, AuditAction action, String entityType,
                   String entityId, Map<String, Object> oldValue,
                   Map<String, Object> newValue, String description);

    /**
     * Simplified log action with description only
     */
    void logAction(User user, AuditAction action, String description);

    /**
     * Log action asynchronously (use after transaction commits)
     * Use this for non-critical logging that can happen later
     */
    void logActionAsync(User user, AuditAction action, String description);
}
