package com.web_app.yaviPrint.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GeoLocationDTO {
    private Long id;
    private Long shopId;
    private Double latitude;
    private Double longitude;
    private String formattedAddress;
    private String city;
    private String state;
    private String country;
    private Integer accuracy;
    private LocalDateTime lastUpdated;
}