package com.lovedev.common.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standardized error response
 * Used by GlobalExceptionHandler to return consistent error format
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Error response details")
public class ErrorResponse {

    @Schema(description = "Error type/code", example = "RESOURCE_NOT_FOUND")
    private String error;

    @Schema(description = "Error message", example = "User not found with id: 123")
    private String message;

    @Schema(description = "HTTP status code", example = "404")
    private int status;

    @Schema(description = "Request path", example = "/api/v1/users/123")
    private String path;

    @Schema(description = "Error timestamp")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    @Schema(description = "Validation field errors")
    private List<FieldError> fieldErrors;

    /**
     * Field validation error details
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Field validation error")
    public static class FieldError {

        @Schema(description = "Field name", example = "email")
        private String field;

        @Schema(description = "Rejected value", example = "invalid-email")
        private Object rejectedValue;

        @Schema(description = "Error message", example = "Email format is invalid")
        private String message;
    }

    /**
     * Create error response for general errors
     */
    public static ErrorResponse of(String error, String message, int status, String path) {
        return ErrorResponse.builder()
                .error(error)
                .message(message)
                .status(status)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create error response with validation errors
     */
    public static ErrorResponse of(String error, String message, int status, String path, List<FieldError> fieldErrors) {
        return ErrorResponse.builder()
                .error(error)
                .message(message)
                .status(status)
                .path(path)
                .fieldErrors(fieldErrors)
                .timestamp(LocalDateTime.now())
                .build();
    }
}