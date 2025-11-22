package com.web_app.yaviPrint.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "print_job_status")
public class PrintJobStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private String status; // UPLOADED, PROCESSING, PRINTING, READY, COMPLETED, FAILED
    private String message;
    private LocalDateTime statusTime;
    private int progress; // 0-100 percentage

    // Getters and setters
}