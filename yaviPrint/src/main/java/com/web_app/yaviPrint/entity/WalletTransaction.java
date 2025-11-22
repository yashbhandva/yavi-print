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
@Table(name = "wallet_transactions")
public class WalletTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String transactionType; // CREDIT, DEBIT
    private double amount;
    private double balanceAfter;
    private String description;
    private String referenceType; // ORDER, REFUND, TOPUP
    private Long referenceId;
    private LocalDateTime transactionDate;

    // Getters and setters
}