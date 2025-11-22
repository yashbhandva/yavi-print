package com.web_app.yaviPrint.dto;

import lombok.Data;

@Data
public class SupportCategoryDTO {
    private Long id;
    private String name;
    private String description;
    private String department;
    private Integer slaHours;
    private Boolean isActive;

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}