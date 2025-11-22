package com.web_app.yaviPrint.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PrintJobStatusDTO {
    private Long id;
    private Long orderId;
    private String status;
    private String message;
    private LocalDateTime statusTime;
    private Integer progress;
}