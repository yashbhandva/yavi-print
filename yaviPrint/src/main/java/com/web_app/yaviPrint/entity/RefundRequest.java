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
@Table(name = "refund_requests")
public class RefundRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private String reason;
    private double refundAmount;
    private String status; // PENDING, APPROVED, REJECTED
    private String processedBy;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
    private String rejectionReason;

    // Getters and setters
}