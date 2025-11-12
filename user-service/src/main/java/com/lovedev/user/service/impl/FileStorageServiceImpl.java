package com.lovedev.user.service.impl;

import com.lovedev.user.exception.ResourceNotFoundException;
import com.lovedev.user.model.dto.response.StorageStats;
import com.lovedev.user.service.FileStorageService;
import com.lovedev.user.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;

/**
 * Service implementation for file storage operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    private final FileUtil fileUtil;

    /**
     * Store avatar file
     */
    @Override
    public String storeAvatar(MultipartFile file) {
        log.info("Storing avatar file: {}", file.getOriginalFilename());
        return fileUtil.storeFile(file, "avatars");
    }

    /**
     * Store document file
     */
    @Override
    public String storeDocument(MultipartFile file) {
        log.info("Storing document file: {}", file.getOriginalFilename());
        return fileUtil.storeFile(file, "documents");
    }

    /**
     * Load file as Resource
     */
    @Override
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = fileUtil.getFilePath(fileName);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found: " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File not found: " + fileName);
        }
    }

    /**
     * Delete file
     */
    @Override
    public void deleteFile(String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            boolean deleted = fileUtil.deleteFile(fileName);
            if (!deleted) {
                log.warn("Failed to delete file: {}", fileName);
            }
        }
    }

    /**
     * Check if file exists
     */
    @Override
    public boolean fileExists(String fileName) {
        return fileUtil.fileExists(fileName);
    }

    /**
     * Get file URL
     */
    @Override
    public String getFileUrl(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }
        // This should match your server configuration
        return "/api/v1/files/" + fileName;
    }

    /**
     * Get storage statistics
     */
    @Override
    public StorageStats getStorageStats() {
        return StorageStats.builder()
                .totalSize(fileUtil.getTotalStorageSize())
                .totalSizeReadable(fileUtil.getReadableFileSize(fileUtil.getTotalStorageSize()))
                .fileCount(fileUtil.getFileCount())
                .build();
    }
}