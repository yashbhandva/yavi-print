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
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private String paymentId; // Razorpay payment ID
    private String razorpayOrderId; // CHANGED: Razorpay order ID (was 'orderId')
    private double amount;
    private String currency;
    private String status; // CREATED, CAPTURED, FAILED
    private String method; // UPI, CARD, NETBANKING
    private String description;
    private LocalDateTime paymentDate;
    private String receiptNumber;
}