package com.lovedev.common.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Filter to log HTTP requests and responses
 * Helps with debugging and monitoring
 */
@Slf4j
public class RequestResponseLoggingFilter implements Filter {

    private static final int MAX_PAYLOAD_LENGTH = 1000; // Log first 1000 characters

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Wrap request and response to cache content
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(httpRequest);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(httpResponse);

        long startTime = System.currentTimeMillis();

        try {
            // Log request
            logRequest(wrappedRequest);

            // Continue filter chain
            chain.doFilter(wrappedRequest, wrappedResponse);

        } finally {
            long duration = System.currentTimeMillis() - startTime;

            // Log response
            logResponse(wrappedResponse, duration);

            // IMPORTANT: Copy cached response content to actual response
            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();

        StringBuilder logMessage = new StringBuilder();
        logMessage.append("HTTP Request: ")
                .append(method)
                .append(" ")
                .append(uri);

        if (queryString != null) {
            logMessage.append("?").append(queryString);
        }

        log.info(logMessage.toString());

        // Log request body for POST/PUT/PATCH
        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "PATCH".equalsIgnoreCase(method)) {
            byte[] content = request.getContentAsByteArray();
            if (content.length > 0) {
                String body = new String(content, StandardCharsets.UTF_8);
                log.debug("Request Body: {}", truncate(body));
            }
        }
    }

    private void logResponse(ContentCachingResponseWrapper response, long duration) {
        int status = response.getStatus();

        log.info("HTTP Response: Status={}, Duration={}ms", status, duration);

        // Log response body for errors
        if (status >= 400) {
            byte[] content = response.getContentAsByteArray();
            if (content.length > 0) {
                String body = new String(content, StandardCharsets.UTF_8);
                log.error("Error Response Body: {}", truncate(body));
            }
        }
    }

    private String truncate(String str) {
        if (str.length() > MAX_PAYLOAD_LENGTH) {
            return str.substring(0, MAX_PAYLOAD_LENGTH) + "... (truncated)";
        }
        return str;
    }
}