package com.web_app.yaviPrint.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "print_volume_stats")
public class PrintVolumeStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate statDate;
    private int totalPagesPrinted;
    private int bwPages;
    private int colorPages;
    private int a4Pages;
    private int a3Pages;
    private int photoPages;
    private String hourlyDistribution; // JSON array
    private String popularTimeSlots; // JSON

    // Getters and setters
}