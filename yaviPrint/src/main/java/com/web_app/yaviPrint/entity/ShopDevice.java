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
@Table(name = "shop_devices")
public class ShopDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    private String deviceName; // Laptop-1, PC-Main, etc.
    private String deviceType; // LAPTOP, DESKTOP, RASPBERRY_PI
    private String macAddress;
    private String ipAddress;
    private boolean isActive;
    private LocalDateTime lastSeen;
    private String printerName;
    private String printerModel;

    // Getters and setters
}