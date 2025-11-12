package com.lovedev.user.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Storage Statistics DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StorageStats {
    private long totalSize;
    private String totalSizeReadable;
    private long fileCount;
}