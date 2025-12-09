package com.lovedev.common.security.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

/**
 * Helper utility for security-related operations
 */
public class SecurityHelper {

    /**
     * Get current authenticated user's ID
     */
    public static UUID getCurrentUserId() {
        String userId = getCurrentUserIdAsString();
        if (userId != null) {
            try {
                return UUID.fromString(userId);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Get current authenticated user's ID as String
     */
    public static String getCurrentUserIdAsString() {
        // Try from request attributes first (set by JwtAuthenticationFilter)
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            Object userId = request.getAttribute("userId");
            if (userId != null) {
                return userId.toString();
            }
        }

        // Fallback to authentication principal
        Authentication authentication = getCurrentAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            return authentication.getPrincipal().toString();
        }

        return null;
    }

    /**
     * Get current authenticated user's email
     */
    public static String getCurrentUserEmail() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            Object email = request.getAttribute("userEmail");
            if (email != null) {
                return email.toString();
            }
        }
        return null;
    }

    /**
     * Get current authentication object
     */
    public static Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Check if user is authenticated
     */
    public static boolean isAuthenticated() {
        Authentication authentication = getCurrentAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String);
    }

    /**
     * Check if user has specific role
     */
    public static boolean hasRole(String role) {
        Authentication authentication = getCurrentAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String roleToCheck = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(roleToCheck));
    }

    /**
     * Check if user has any of the specified roles
     */
    public static boolean hasAnyRole(String... roles) {
        for (String role : roles) {
            if (hasRole(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if user is admin
     */
    public static boolean isAdmin() {
        return hasRole("ROLE_SUPER_ADMIN");
    }

    /**
     * Check if user is manager or higher
     */
    public static boolean isManagerOrHigher() {
        return hasAnyRole("ROLE_SUPER_ADMIN", "ROLE_ORG_ADMIN", "ROLE_MANAGER");
    }
}