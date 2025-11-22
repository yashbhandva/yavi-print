package com.web_app.yaviPrint.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private boolean enabled;
    private LocalDateTime verifiedAt;
    private LocalDateTime createdAt;


}