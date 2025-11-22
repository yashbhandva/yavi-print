package com.web_app.yaviPrint.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "shop_verifications")
public class ShopVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    private boolean isVerified;
    private LocalDateTime verificationDate;
    private String verifiedBy; // Admin username
    private String documentsSubmitted; // JSON array of document URLs
    private String rejectionReason;
    private LocalDateTime appliedAt;

    // Getters and setters

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }
}