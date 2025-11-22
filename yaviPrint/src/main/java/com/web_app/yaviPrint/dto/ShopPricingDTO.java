package com.web_app.yaviPrint.dto;

import lombok.Data;

@Data
public class ShopPricingDTO {
    private Long id;
    private String paperType;
    private String printType;
    private Double pricePerPage;
    private Double pricePerCopy;
    private Boolean duplexPricing;
    private Double duplexExtraCharge;

}