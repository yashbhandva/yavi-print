package com.web_app.yaviPrint.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShopStatusDTO {
    private Long id;
    private Long shopId;
    private Boolean isOnline;
    private Boolean isBusy;
    private Integer currentQueueSize;
    private Integer maxQueueSize;
    private LocalDateTime statusUpdatedAt;
    private String busyReason;

    public Boolean getOnline() {
        return isOnline;
    }

    public void setOnline(Boolean online) {
        isOnline = online;
    }

    public Boolean getBusy() {
        return isBusy;
    }

    public void setBusy(Boolean busy) {
        isBusy = busy;
    }
}