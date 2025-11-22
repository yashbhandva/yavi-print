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
@Table(name = "daily_user_stats")
public class DailyUserStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate statDate;
    private int newRegistrations;
    private int activeUsers;
    private int totalOrders;
    private int returningUsers;
    private double totalRevenue;
    private String deviceBreakdown; // JSON: {"mobile": 60, "desktop": 40}

    // Getters and setters
}