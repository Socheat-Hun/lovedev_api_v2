package com.lovedev.user.service;

import com.lovedev.user.model.dto.request.*;
import com.lovedev.user.model.dto.response.PageResponse;
import com.lovedev.user.model.dto.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Service interface for user management operations
 */
public interface UserService {

    // ============================================
    // Profile Management (Current User)
    // ============================================

    UserResponse getCurrentUser();

    UserResponse updateCurrentUser(UpdateUserRequest request);

    void changePassword(ChangePasswordRequest request);

    UserResponse uploadAvatar(MultipartFile file);

    void deleteAvatar();

    // ============================================
    // User Management (Admin)
    // ============================================

    UserResponse getUserById(UUID id);

    PageResponse<UserResponse> searchUsers(UserSearchRequest searchRequest,
                                           int page, int size, String sortBy, String sortDir);

    UserResponse updateUser(UUID id, UpdateUserRequest request);

    UserResponse updateUserStatus(UUID id, UpdateStatusRequest request);

    void deleteUser(UUID id);

    void deleteUsers(List<UUID> ids);

    // ============================================
    // Role Management
    // ============================================

    UserResponse addRole(UUID userId, String roleName);

    UserResponse removeRole(UUID userId, String roleName);

    UserResponse updateRoles(UUID userId, Set<String> roleNames);

    UserResponse updateUserRole(UUID id, UpdateRoleRequest request);
}