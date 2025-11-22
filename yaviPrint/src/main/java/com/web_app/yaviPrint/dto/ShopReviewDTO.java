package com.web_app.yaviPrint.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ShopReviewDTO {
    private Long id;
    private Long shopId;
    private Long userId;
    private Long orderId;
    private Integer rating;
    private String comment;
    private LocalDateTime reviewDate;
    private Boolean isVerifiedPurchase;
    private Integer helpfulCount;
    private Boolean isEdited;
    private LocalDateTime editedAt;
    private UserResponseDTO user;

    public Boolean getVerifiedPurchase() {
        return isVerifiedPurchase;
    }

    public void setVerifiedPurchase(Boolean verifiedPurchase) {
        isVerifiedPurchase = verifiedPurchase;
    }

    public Boolean getEdited() {
        return isEdited;
    }

    public void setEdited(Boolean edited) {
        isEdited = edited;
    }
}