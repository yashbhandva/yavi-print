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
@Table(name = "geo_locations")
public class GeoLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    private double latitude;
    private double longitude;
    private String formattedAddress;
    private String city;
    private String state;
    private String country;
    private int accuracy; // in meters
    private LocalDateTime lastUpdated;

    // Getters and setters
}