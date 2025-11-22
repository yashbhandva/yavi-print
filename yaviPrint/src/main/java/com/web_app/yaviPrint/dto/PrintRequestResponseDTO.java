package com.web_app.yaviPrint.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PrintRequestResponseDTO {
    private Long id;
    private String tokenId;
    private String qrCodeUrl;
    private UserResponseDTO user;
    private ShopResponseDTO shop;
    private DocumentFileDTO documentFile;
    private PrintSettingsDTO printSettings;
    private Double totalAmount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime printedAt;
    private LocalDateTime collectedAt;
    private Boolean paymentStatus;
}