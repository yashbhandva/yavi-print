package com.web_app.yaviPrint.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PaymentTransactionDTO {
    private Long id;
    private Long paymentId;
    private String transactionId;
    private String transactionType;
    private Double amount;
    private String status;
    private String gatewayResponse;
    private LocalDateTime transactionTime;
    private String notes;
}