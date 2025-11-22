package com.web_app.yaviPrint.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShopVerificationDTO {
    private Long id;
    private Long shopId;
    private Boolean isVerified;
    private LocalDateTime verificationDate;
    private String verifiedBy;
    private String documentsSubmitted;
    private String rejectionReason;
    private LocalDateTime appliedAt;

    public Boolean getVerified() {
        return isVerified;
    }

    public void setVerified(Boolean verified) {
        isVerified = verified;
    }
}