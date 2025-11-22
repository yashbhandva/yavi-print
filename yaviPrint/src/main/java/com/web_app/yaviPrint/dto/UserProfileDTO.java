package com.web_app.yaviPrint.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserProfileDTO {
    private Long userId;
    private String profilePicture;
    private LocalDate dateOfBirth;
    private String gender;
    private String alternatePhone;
    private AddressDTO address;
    private String preferences;

}