package com.web_app.yaviPrint.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "print_settings")
public class PrintSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paperSize; // A4, A3
    private String printType; // BW, COLOR
    private boolean duplex;
    private String pageRanges; // "1-5,8,10-12"
    private int copies;
    private String orientation; // PORTRAIT, LANDSCAPE
    private String quality; // DRAFT, STANDARD, HIGH
    private boolean fitToPage;

    // Getters and setters
}