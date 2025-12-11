package com.lovedev.common.web.exception;

/**
 * Exception for resource conflicts
 * Returns HTTP 409
 */
public class ConflictException extends BusinessException {
    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue));
    }
}
