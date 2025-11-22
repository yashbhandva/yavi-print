package com.web_app.yaviPrint.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private Long id;
    private Long orderId;
    private String paymentId;
    private String razorpayOrderId; // UPDATED: Use new field name
    
    public void setOrderIdRazorpay(String orderIdRazorpay) {
        this.razorpayOrderId = orderIdRazorpay;
    }
    private Double amount;
    private String currency;
    private String status;
    private String method;
    private String description;
    private LocalDateTime paymentDate;
    private String receiptNumber;
}