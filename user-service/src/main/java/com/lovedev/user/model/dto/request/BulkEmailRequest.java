package com.lovedev.user.model.dto.request;

import lombok.Data;

@Data
public class BulkEmailRequest {
    private java.util.List<String> recipients;
    private String subject;
    private String body;
    private boolean isHtml;
}
