package com.web_app.yaviPrint.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "shop_services")
public class ShopService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    private String serviceName; // LAMINATION, SPIRAL_BINDING, COLOR_PRINTING, etc.
    private String description;
    private double price;
    private boolean isAvailable;
    private int estimatedTime; // in minutes

    // Getters and setters
}