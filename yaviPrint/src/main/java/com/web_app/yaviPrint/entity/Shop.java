package com.web_app.yaviPrint.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;
@Getter
@Setter
@AllArgsConstructor
@ToString
@Entity
@Table(name = "shops")
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shopName;
    private String description;
    private String address;
    private String city;
    private String state;
    private String pincode;

    private double latitude;
    private double longitude;

    private double bwPricePerPage = 2.0;
    private double colorPricePerPage = 5.0;
    private double a3PriceMultiplier = 1.5;

    private LocalTime openingTime;
    private LocalTime closingTime;

    private boolean isActive = true;
    private boolean isOnline = true;
    private boolean isVerified = false;

    private int maxOrdersPerDay = 100;
    private int currentOrdersToday = 0;

    private double rating = 0.0;
    private int totalReviews = 0;

    @OneToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Shop() {}

    public Shop(String shopName, String address, User owner) {
        this.shopName = shopName;
        this.address = address;
        this.owner = owner;
    }

    public double calculatePrice(int pages, boolean isColor, boolean isA3, int copies) {
        double basePrice = isColor ? colorPricePerPage : bwPricePerPage;
        if (isA3) {
            basePrice *= a3PriceMultiplier;
        }
        return basePrice * pages * copies;
    }

    public boolean isOpen() {
        if (!isActive || !isOnline) return false;
        LocalTime now = LocalTime.now();
        return !now.isBefore(openingTime) && !now.isAfter(closingTime);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public boolean isOnline() { return isOnline; }
    public void setOnline(boolean online) { isOnline = online; }
    public LocalTime getOpeningTime() { return openingTime; }
    public void setOpeningTime(LocalTime openingTime) { this.openingTime = openingTime; }
    public LocalTime getClosingTime() { return closingTime; }
    public void setClosingTime(LocalTime closingTime) { this.closingTime = closingTime; }
    public double getBwPricePerPage() { return bwPricePerPage; }
    public void setBwPricePerPage(double bwPricePerPage) { this.bwPricePerPage = bwPricePerPage; }
    public double getColorPricePerPage() { return colorPricePerPage; }
    public void setColorPricePerPage(double colorPricePerPage) { this.colorPricePerPage = colorPricePerPage; }
}