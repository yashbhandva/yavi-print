package com.web_app.yaviPrint.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "support_categories")
public class SupportCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String department; // TECHNICAL, BILLING, GENERAL
    private int slaHours; // Service Level Agreement in hours
    private boolean isActive;

    // Getters and setters
}