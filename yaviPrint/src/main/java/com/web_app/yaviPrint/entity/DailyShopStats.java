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
@Table(name = "daily_shop_stats")
public class DailyShopStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    private LocalDate statDate;
    private int totalOrders;
    private int completedOrders;
    private int cancelledOrders;
    private double totalRevenue;
    private double platformEarnings;
    private int newCustomers;
    private double averageRating;
    private int pageViews;

    // Getters and setters
}