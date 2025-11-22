package com.web_app.yaviPrint.dto;

import lombok.Data;

@Data
public class PageRangeSelectionDTO {
    private Long id;
    private Long orderId;
    private Integer startPage;
    private Integer endPage;
    private Boolean isRange;
    private Integer copies;
}