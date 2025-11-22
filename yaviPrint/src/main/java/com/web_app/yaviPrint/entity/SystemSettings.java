package com.web_app.yaviPrint.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "system_settings")
public class SystemSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String settingKey;
    private String settingValue;
    private String dataType; // STRING, NUMBER, BOOLEAN, JSON
    private String category; // PAYMENT, NOTIFICATION, GENERAL
    private String description;
    private boolean isEditable;

    // Getters and setters
}