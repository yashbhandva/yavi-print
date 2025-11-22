package com.web_app.yaviPrint.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminUserDTO {
    private Long id;
    private String username;
    private String email;
    private String password; // ADDED THIS MISSING FIELD
    private String role;
    private Boolean isActive;
    private LocalDateTime lastLogin;
    private String permissions;

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}