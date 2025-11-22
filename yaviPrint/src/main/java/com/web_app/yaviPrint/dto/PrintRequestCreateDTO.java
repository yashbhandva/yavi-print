package com.web_app.yaviPrint.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class PrintRequestCreateDTO {
    @NotNull(message = "Shop ID is required")
    private Long shopId;

    @NotNull(message = "Document file ID is required")
    private Long documentFileId;

    private PrintSettingsDTO printSettings;
    private Integer copies;
}