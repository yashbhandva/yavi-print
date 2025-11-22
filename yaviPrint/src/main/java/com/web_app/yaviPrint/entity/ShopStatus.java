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
@Table(name = "shop_status")
public class ShopStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    private boolean isOnline;
    private boolean isBusy;
    private int currentQueueSize;
    private int maxQueueSize;
    private LocalDateTime statusUpdatedAt;
    private String busyReason; // LUNCH_BREAK, MAINTENANCE, etc.

    // Getters and setters
}