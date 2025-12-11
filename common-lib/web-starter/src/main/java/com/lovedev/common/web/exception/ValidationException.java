package com.lovedev.common.web.exception;

/**
 * Exception for validation errors
 * Returns HTTP 400
 */
public class ValidationException extends BusinessException {
    public ValidationException(String message) {
        super(message);
    }
}
