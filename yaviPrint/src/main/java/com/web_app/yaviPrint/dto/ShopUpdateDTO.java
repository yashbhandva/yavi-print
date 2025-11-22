package com.web_app.yaviPrint.dto;

import lombok.Data;

@Data
public class ShopUpdateDTO {
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
    private Boolean isOnline;

    public Boolean getOnline() {
        return isOnline;
    }

    public void setOnline(Boolean online) {
        isOnline = online;
    }


}