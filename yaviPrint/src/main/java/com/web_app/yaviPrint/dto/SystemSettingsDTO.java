package com.web_app.yaviPrint.dto;

import lombok.Data;

@Data
public class SystemSettingsDTO {
    private Long id;
    private String settingKey;
    private String settingValue;
    private String dataType;
    private String category;
    private String description;
    private Boolean isEditable;

    public Boolean getEditable() {
        return isEditable;
    }

    public void setEditable(Boolean editable) {
        isEditable = editable;
    }
}