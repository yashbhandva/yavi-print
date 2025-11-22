package com.web_app.yaviPrint.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RefundRequestDTO {
    private Long id;
    private Long orderId;
    private String reason;
    private Double refundAmount;
    private String status;
    private String processedBy;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
    private String rejectionReason;
}