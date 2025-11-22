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
@Table(name = "support_tickets")
public class SupportTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String ticketNumber;
    private String subject;
    private String description;
    private String priority; // LOW, MEDIUM, HIGH, URGENT
    private String status; // OPEN, IN_PROGRESS, RESOLVED, CLOSED
    private String category; // TECHNICAL, PAYMENT, QUALITY, OTHER
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;

    // Getters and setters
}