package com.web_app.yaviPrint.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "daily_revenue")
public class DailyRevenue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate revenueDate;
    private double totalRevenue;
    private double platformCommission;
    private double shopEarnings;
    private double taxAmount;
    private int totalTransactions;
    private double refundAmount;
    private String paymentMethodBreakdown; // JSON

    // Getters and setters
}