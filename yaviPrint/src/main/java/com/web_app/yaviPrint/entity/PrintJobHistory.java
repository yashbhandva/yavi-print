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
@Table(name = "print_job_history")
public class PrintJobHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private String action; // CREATED, UPDATED, STATUS_CHANGED
    private String description;
    private String performedBy; // user_id or system
    private LocalDateTime actionTime;
    private String oldValue;
    private String newValue;

    // Getters and setters
}