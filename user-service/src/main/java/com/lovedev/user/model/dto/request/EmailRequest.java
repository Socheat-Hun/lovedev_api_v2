package com.lovedev.user.model.dto.request;

import lombok.Data;

@Data
public class EmailRequest {
    private String to;
    private String subject;
    private String body;
    private boolean isHtml;
}
