package com.web_app.yaviPrint.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PrintJobHistoryDTO {
    private Long id;
    private Long orderId;
    private String action;
    private String description;
    private String performedBy;
    private LocalDateTime actionTime;
    private String oldValue;
    private String newValue;
}