package com.web_app.yaviPrint.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ShopReportDTO {
    private Long id;
    private Long shopId;
    private Long reportedById;
    private String reportType;
    private String description;
    private String status;
    private String adminNotes;
    private LocalDateTime reportedAt;
    private LocalDateTime resolvedAt;
    private ShopResponseDTO shop;
    private UserResponseDTO reportedBy;
}