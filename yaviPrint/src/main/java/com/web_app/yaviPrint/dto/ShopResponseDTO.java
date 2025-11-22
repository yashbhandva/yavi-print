package com.web_app.yaviPrint.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShopResponseDTO {
    private Long id;
    private String shopName;
    private String description;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private Double latitude;
    private Double longitude;
    private Double bwPricePerPage;
    private Double colorPricePerPage;
    private Double rating;
    private Integer totalReviews;
    private Boolean isActive;
    private Boolean isOnline;
    private Boolean isVerified;
    private UserResponseDTO owner;
    private LocalDateTime createdAt;

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getOnline() {
        return isOnline;
    }

    public void setOnline(Boolean online) {
        isOnline = online;
    }

    public Boolean getVerified() {
        return isVerified;
    }

    public void setVerified(Boolean verified) {
        isVerified = verified;
    }
}