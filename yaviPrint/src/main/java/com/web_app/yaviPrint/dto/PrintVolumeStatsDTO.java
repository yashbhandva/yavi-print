package com.web_app.yaviPrint.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PrintVolumeStatsDTO {
    private Long id;
    private LocalDate statDate;
    private Integer totalPagesPrinted;
    private Integer bwPages;
    private Integer colorPages;
    private Integer a4Pages;
    private Integer a3Pages;
    private Integer photoPages;
    private String hourlyDistribution;
    private String popularTimeSlots;
}