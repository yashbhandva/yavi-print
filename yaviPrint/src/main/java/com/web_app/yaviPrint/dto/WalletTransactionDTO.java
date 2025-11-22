package com.web_app.yaviPrint.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WalletTransactionDTO {
    private Long id;
    private Long userId;
    private String transactionType;
    private Double amount;
    private Double balanceAfter;
    private String description;
    private String referenceType;
    private Long referenceId;
    private LocalDateTime transactionDate;
}