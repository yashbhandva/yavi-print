package com.web_app.yaviPrint.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DailyUserStatsDTO {
    private Long id;
    private LocalDate statDate;
    private Integer newRegistrations;
    private Integer activeUsers;
    private Integer totalOrders;
    private Integer returningUsers;
    private Double totalRevenue;
    private String deviceBreakdown;
}