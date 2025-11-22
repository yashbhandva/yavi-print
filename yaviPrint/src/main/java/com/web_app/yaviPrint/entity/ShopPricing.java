package com.web_app.yaviPrint.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "shop_pricing")
public class ShopPricing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    private String paperType; // A4, A3, PHOTO
    private String printType; // BW, COLOR
    private double pricePerPage;
    private double pricePerCopy;
    private boolean duplexPricing;
    private double duplexExtraCharge;

    // Getters and setters
}