package com.web_app.yaviPrint.dto;

import lombok.Data;

@Data
public class NotificationPreferencesDTO {
    private Long id;
    private Long userId;
    private Boolean emailNotifications;
    private Boolean smsNotifications;
    private Boolean pushNotifications;
    private Boolean orderUpdates;
    private Boolean promotional;
    private Boolean priceAlerts;
    private Boolean shopUpdates;
}