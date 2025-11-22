package com.web_app.yaviPrint.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DailyShopStatsDTO {
    private Long id;
    private Long shopId;
    private LocalDate statDate;
    private Integer totalOrders;
    private Integer completedOrders;
    private Integer cancelledOrders;
    private Double totalRevenue;
    private Double platformEarnings;
    private Integer newCustomers;
    private Double averageRating;
    private Integer pageViews;
    private ShopResponseDTO shop;
}