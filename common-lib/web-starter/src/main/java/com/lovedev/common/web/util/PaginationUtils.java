package com.lovedev.common.web.util;

import com.lovedev.common.web.constant.ApiConstants;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Utility class for pagination operations
 */
public final class PaginationUtils {

    private PaginationUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Create Pageable with validation and defaults
     *
     * @param page Page number (0-indexed)
     * @param size Page size
     * @param sortBy Sort field
     * @param sortDir Sort direction (asc/desc)
     * @return Validated Pageable object
     */
    public static Pageable createPageable(int page, int size, String sortBy, String sortDir) {
        // Validate and adjust page number
        page = Math.max(ApiConstants.MIN_PAGE_NUMBER, page);

        // Validate and adjust page size
        size = Math.max(ApiConstants.MIN_PAGE_SIZE, size);
        size = Math.min(ApiConstants.MAX_PAGE_SIZE, size);

        // Create sort
        Sort sort = createSort(sortBy, sortDir);

        return PageRequest.of(page, size, sort);
    }

    /**
     * Create Pageable with default sort
     *
     * @param page Page number
     * @param size Page size
     * @return Pageable with default sort (createdAt desc)
     */
    public static Pageable createPageable(int page, int size) {
        return createPageable(
                page,
                size,
                ApiConstants.DEFAULT_SORT_BY,
                ApiConstants.DEFAULT_SORT_DIRECTION
        );
    }

    /**
     * Create Sort object from sort parameters
     *
     * @param sortBy Sort field
     * @param sortDir Sort direction
     * @return Sort object
     */
    public static Sort createSort(String sortBy, String sortDir) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = ApiConstants.DEFAULT_SORT_BY;
        }

        if (sortDir == null || sortDir.trim().isEmpty()) {
            sortDir = ApiConstants.DEFAULT_SORT_DIRECTION;
        }

        return sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
    }

    /**
     * Validate page number
     *
     * @param page Page number to validate
     * @return Validated page number (minimum 0)
     */
    public static int validatePageNumber(int page) {
        return Math.max(ApiConstants.MIN_PAGE_NUMBER, page);
    }

    /**
     * Validate page size
     *
     * @param size Page size to validate
     * @return Validated page size (between MIN and MAX)
     */
    public static int validatePageSize(int size) {
        size = Math.max(ApiConstants.MIN_PAGE_SIZE, size);
        return Math.min(ApiConstants.MAX_PAGE_SIZE, size);
    }
}