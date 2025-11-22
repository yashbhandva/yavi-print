package com.web_app.yaviPrint.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShopDeviceDTO {
    private Long id;
    private Long shopId;
    private String deviceName;
    private String deviceType;
    private String macAddress;
    private String ipAddress;
    private Boolean isActive;
    private LocalDateTime lastSeen;
    private String printerName;
    private String printerModel;

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}