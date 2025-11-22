package com.web_app.yaviPrint.dto;

import lombok.Data;

@Data
public class ShopServiceDTO {
    private Long id;
    private String serviceName;
    private String description;
    private Double price;
    private Boolean isAvailable;
    private Integer estimatedTime;

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }
}