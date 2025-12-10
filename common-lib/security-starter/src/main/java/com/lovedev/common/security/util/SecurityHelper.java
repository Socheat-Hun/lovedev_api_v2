package com.lovedev.common.security.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collection;
import java.util.UUID;

/**
 * Security helper utility for accessing authentication information
 * Works with both user-service (CustomUserDetails) and notification-service (request attributes)
 *
 * Usage:
 * - UUID userId = SecurityHelper.getCurrentUserId();
 * - String email = SecurityHelper.getCurrentUserEmail();
 * - boolean isAdmin = SecurityHelper.isAdmin();
 */
public class SecurityHelper {

    private SecurityHelper() {
        // Private constructor to prevent instantiation
    }

    // ============================================
    // User ID Retrieval (Works for both services)
    // ============================================

    /**
     * Get current user ID as UUID
     * Tries multiple sources:
     * 1. CustomUserDetails from Authentication (user-service)
     * 2. Request attribute "userId" (notification-service with JwtAuthenticationFilter)
     * 3. Authentication principal as String UUID
     *
     * @return Current user's UUID, or null if not authenticated
     */
    public static UUID getCurrentUserId() {
        // Try 1: Get from CustomUserDetails (user-service pattern)
        Authentication authentication = getCurrentAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            // Check if it's CustomUserDetails with getId() method
            if (principal != null && hasGetIdMethod(principal)) {
                try {
                    Object id = principal.getClass().getMethod("getId").invoke(principal);
                    if (id instanceof UUID) {
                        return (UUID) id;
                    }
                } catch (Exception e) {
                    // Fall through to next method
                }
            }
        }

        // Try 2: Get from request attributes (notification-service pattern)
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            Object userId = request.getAttribute("userId");
            if (userId != null) {
                if (userId instanceof UUID) {
                    return (UUID) userId;
                }
                try {
                    return UUID.fromString(userId.toString());
                } catch (IllegalArgumentException e) {
                    // Invalid UUID format, fall through
                }
            }
        }

        // Try 3: Parse from authentication name (fallback)
        if (authentication != null) {
            try {
                return UUID.fromString(authentication.getName());
            } catch (IllegalArgumentException e) {
                // Not a valid UUID
            }
        }

        return null;
    }

    /**
     * Get current user ID as String
     * @return Current user's UUID as String, or null if not authenticated
     */
    public static String getCurrentUserIdAsString() {
        UUID userId = getCurrentUserId();
        return userId != null ? userId.toString() : null;
    }

    // ============================================
    // Email Retrieval (Works for both services)
    // ============================================

    /**
     * Get current user email
     * Tries multiple sources:
     * 1. CustomUserDetails.getEmail() (user-service)
     * 2. Request attribute "userEmail" (notification-service)
     * 3. Authentication principal as email string
     *
     * @return Current user's email, or null if not authenticated
     */
    public static String getCurrentUserEmail() {
        Authentication authentication = getCurrentAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        // Try 1: Get email from CustomUserDetails (user-service pattern)
        if (principal != null && hasGetEmailMethod(principal)) {
            try {
                Object email = principal.getClass().getMethod("getEmail").invoke(principal);
                if (email instanceof String) {
                    return (String) email;
                }
            } catch (Exception e) {
                // Fall through to next method
            }
        }

        // Try 2: Get from request attributes (notification-service pattern)
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            Object userEmail = request.getAttribute("userEmail");
            if (userEmail != null) {
                return userEmail.toString();
            }
        }

        // Try 3: Use principal directly as email (fallback)
        if (principal != null && !(principal instanceof String && principal.equals("anonymousUser"))) {
            return principal.toString();
        }

        return null;
    }

    // ============================================
    // Authentication Status
    // ============================================

    /**
     * Get current authentication object
     * @return Current Authentication, or null if not authenticated
     */
    public static Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Check if user is authenticated
     * @return true if authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        Authentication authentication = getCurrentAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String
                && authentication.getPrincipal().equals("anonymousUser"));
    }

    // ============================================
    // Role Checking
    // ============================================

    /**
     * Check if current user has a specific role
     * Automatically adds "ROLE_" prefix if not present
     *
     * @param role Role name (with or without "ROLE_" prefix)
     * @return true if user has the role, false otherwise
     */
    public static boolean hasRole(String role) {
        if (role == null || role.isEmpty()) {
            return false;
        }

        Authentication authentication = getCurrentAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities == null || authorities.isEmpty()) {
            return false;
        }

        // Add ROLE_ prefix if not present
        String roleToCheck = role.startsWith("ROLE_") ? role : "ROLE_" + role;

        return authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals(roleToCheck));
    }

    /**
     * Check if current user has ANY of the specified roles
     * @param roles Role names (with or without "ROLE_" prefix)
     * @return true if user has any of the roles, false otherwise
     */
    public static boolean hasAnyRole(String... roles) {
        if (roles == null || roles.length == 0) {
            return false;
        }

        for (String role : roles) {
            if (hasRole(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if current user has ALL of the specified roles
     * @param roles Role names (with or without "ROLE_" prefix)
     * @return true if user has all of the roles, false otherwise
     */
    public static boolean hasAllRoles(String... roles) {
        if (roles == null || roles.length == 0) {
            return false;
        }

        for (String role : roles) {
            if (!hasRole(role)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if current user has a specific authority (exact match)
     * Does NOT add "ROLE_" prefix
     *
     * @param authority Authority name (e.g., "user:read", "ROLE_ADMIN")
     * @return true if user has the authority, false otherwise
     */
    public static boolean hasAuthority(String authority) {
        if (authority == null || authority.isEmpty()) {
            return false;
        }

        Authentication authentication = getCurrentAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities == null || authorities.isEmpty()) {
            return false;
        }

        return authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals(authority));
    }

    // ============================================
    // Common Role Checks (Convenience Methods)
    // ============================================

    /**
     * Check if current user is SUPER_ADMIN
     * @return true if user is super admin, false otherwise
     */
    public static boolean isSuperAdmin() {
        return hasRole("SUPER_ADMIN");
    }

    /**
     * Check if current user is ADMIN (any admin role)
     * @return true if user has any admin role, false otherwise
     */
    public static boolean isAdmin() {
        return hasAnyRole("SUPER_ADMIN", "ORG_ADMIN", "ADMIN");
    }

    /**
     * Check if current user is MANAGER
     * @return true if user is manager, false otherwise
     */
    public static boolean isManager() {
        return hasRole("MANAGER");
    }

    /**
     * Check if current user is MANAGER or higher (ADMIN roles)
     * @return true if user is manager or admin, false otherwise
     */
    public static boolean isManagerOrHigher() {
        return hasAnyRole("SUPER_ADMIN", "ORG_ADMIN", "ADMIN", "MANAGER");
    }

    /**
     * Check if current user is EMPLOYEE
     * @return true if user is employee, false otherwise
     */
    public static boolean isEmployee() {
        return hasRole("EMPLOYEE");
    }

    /**
     * Check if current user is EMPLOYEE or higher
     * @return true if user is employee, manager, or admin, false otherwise
     */
    public static boolean isEmployeeOrHigher() {
        return hasAnyRole("SUPER_ADMIN", "ORG_ADMIN", "ADMIN", "MANAGER", "EMPLOYEE");
    }

    /**
     * Check if current user is USER (basic user role)
     * @return true if user has USER role, false otherwise
     */
    public static boolean isUser() {
        return hasRole("USER");
    }

    // ============================================
    // Private Helper Methods
    // ============================================

    /**
     * Check if object has getId() method (duck typing for CustomUserDetails)
     */
    private static boolean hasGetIdMethod(Object obj) {
        try {
            obj.getClass().getMethod("getId");
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * Check if object has getEmail() method (duck typing for CustomUserDetails)
     */
    private static boolean hasGetEmailMethod(Object obj) {
        try {
            obj.getClass().getMethod("getEmail");
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}