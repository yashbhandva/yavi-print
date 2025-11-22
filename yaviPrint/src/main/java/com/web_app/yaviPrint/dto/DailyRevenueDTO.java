package com.web_app.yaviPrint.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DailyRevenueDTO {
    private Long id;
    private LocalDate revenueDate;
    private Double totalRevenue;
    private Double platformCommission;
    private Double shopEarnings;
    private Double taxAmount;
    private Integer totalTransactions;
    private Double refundAmount;
    private String paymentMethodBreakdown;
}