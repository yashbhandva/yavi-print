package com.web_app.yaviPrint.dto;

import lombok.Data;

@Data
public class NotificationTemplateDTO {
    private Long id;
    private String templateName;
    private String templateType;
    private String subject;
    private String content;
    private String variables;
    private Boolean isActive;
    private String language;

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}