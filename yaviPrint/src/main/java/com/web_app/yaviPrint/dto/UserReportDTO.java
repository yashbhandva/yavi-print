package com.web_app.yaviPrint.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserReportDTO {
    private Long id;
    private Long reportedUserId;
    private Long reportedById;
    private String reportType;
    private String description;
    private String status;
    private LocalDateTime reportedAt;
    private UserResponseDTO reportedUser;
    private UserResponseDTO reportedBy;
}