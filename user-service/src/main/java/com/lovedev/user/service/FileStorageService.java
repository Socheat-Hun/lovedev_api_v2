package com.lovedev.user.service;

import com.lovedev.user.model.dto.response.StorageStats;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for file storage operations
 */
public interface FileStorageService {

    /**
     * Store avatar file
     */
    String storeAvatar(MultipartFile file);

    /**
     * Store document file
     */
    String storeDocument(MultipartFile file);

    /**
     * Load file as Resource
     */
    Resource loadFileAsResource(String fileName);

    /**
     * Delete file
     */
    void deleteFile(String fileName);

    /**
     * Check if file exists
     */
    boolean fileExists(String fileName);

    /**
     * Get file URL
     */
    String getFileUrl(String fileName);

    /**
     * Get storage statistics
     */
    StorageStats getStorageStats();
}