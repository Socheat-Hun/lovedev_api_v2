package com.lovedev.user.model.dto.request;

import lombok.Data;

import java.util.Map;

@Data
public class TemplateEmailRequest {
    private String to;
    private String subject;
    private String templateName;
    private Map<String, Object> variables;
}
