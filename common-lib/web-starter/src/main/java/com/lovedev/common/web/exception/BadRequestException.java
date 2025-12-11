package com.lovedev.common.web.exception;

/**
 * Exception for bad request errors
 * Returns HTTP 400
 */
public class BadRequestException extends BusinessException {
    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}

