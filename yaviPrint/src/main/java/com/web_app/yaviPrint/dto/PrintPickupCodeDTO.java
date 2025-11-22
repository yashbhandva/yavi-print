package com.web_app.yaviPrint.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PrintPickupCodeDTO {
    private Long id;
    private Long orderId;
    private String tokenId;
    private String qrCodeUrl;
    private String shortCode;
    private LocalDateTime generatedAt;
    private LocalDateTime expiresAt;
    private Boolean isUsed;
    private LocalDateTime usedAt;

    public Boolean getUsed() {
        return isUsed;
    }

    public void setUsed(Boolean used) {
        isUsed = used;
    }
}