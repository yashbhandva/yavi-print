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
@Table(name = "shop_reports")
public class ShopReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @ManyToOne
    @JoinColumn(name = "reported_by")
    private User reportedBy;

    private String reportType; // FRAUD, POOR_QUALITY, OVERCHARGING
    private String description;
    private String status; // PENDING, INVESTIGATING, RESOLVED
    private String adminNotes;
    private LocalDateTime reportedAt;
    private LocalDateTime resolvedAt;

    // Getters and setters
}