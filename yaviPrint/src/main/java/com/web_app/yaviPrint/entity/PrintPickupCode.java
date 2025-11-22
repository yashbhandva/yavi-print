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
@Table(name = "print_pickup_codes")
public class PrintPickupCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private String tokenId; // A3921
    private String qrCodeUrl;
    private String shortCode; // 4-digit code for manual entry
    private LocalDateTime generatedAt;
    private LocalDateTime expiresAt;
    private boolean isUsed;
    private LocalDateTime usedAt;

    // Getters and setters
}