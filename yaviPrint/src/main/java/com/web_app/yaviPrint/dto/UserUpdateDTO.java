package com.web_app.yaviPrint.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDTO {
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Size(min = 10, max = 15, message = "Phone must be between 10 and 15 characters")
    private String phone;

}