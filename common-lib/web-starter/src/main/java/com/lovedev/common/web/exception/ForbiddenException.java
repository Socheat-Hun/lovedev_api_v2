package com.lovedev.common.web.exception;

/**
 * Exception for forbidden access
 * Returns HTTP 403
 */
public class ForbiddenException extends BusinessException {
    public ForbiddenException(String message) {
        super(message);
    }
}
