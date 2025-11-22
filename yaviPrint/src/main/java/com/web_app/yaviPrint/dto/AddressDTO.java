package com.web_app.yaviPrint.dto;

import lombok.Data;

@Data
public class AddressDTO {
    private String street;
    private String city;
    private String state;
    private String pincode;
    private String landmark;
    private String addressType;
}