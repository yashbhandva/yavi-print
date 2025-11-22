package com.web_app.yaviPrint.dto;

import lombok.Data;

@Data
public class PrintRequestUpdateDTO {
    private String status;
    private PrintSettingsDTO printSettings;
    private Integer copies;
}