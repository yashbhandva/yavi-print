package com.web_app.yaviPrint.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SupportTicketDTO {
    private Long id;
    private Long userId;
    private String ticketNumber;
    private String subject;
    private String description;
    private String priority;
    private String status;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private UserResponseDTO user;
}