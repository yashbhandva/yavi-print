package com.web_app.yaviPrint.dto;

import lombok.Data;

@Data
public class PrintSettingsDTO {
    private Long id;
    private String paperSize;
    private String printType;
    private Boolean duplex;
    private String pageRanges;
    private Integer copies;
    private String orientation;
    private String quality;
    private Boolean fitToPage;
}