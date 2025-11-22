package com.web_app.yaviPrint.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminActionLogDTO {
    private Long id;
    private Long adminUserId;
    private String action;
    private String description;
    private String targetEntity;
    private Long targetId;
    private String ipAddress;
    private LocalDateTime actionTime;
    private String oldValues;
    private String newValues;
    private AdminUserDTO adminUser;
}