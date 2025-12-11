package com.lovedev.common.web.exception;

/**
 * Exception for unauthorized access
 * Returns HTTP 401
 */
public class UnauthorizedException extends BusinessException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
