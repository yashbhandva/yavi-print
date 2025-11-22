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
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String title;
    private String message;
    private String type; // ORDER_UPDATE, PAYMENT, PROMOTIONAL, SYSTEM
    private boolean isRead;
    private LocalDateTime sentAt;
    private String actionUrl;
    private String imageUrl;
    private int priority; // 1-5, 5 being highest

    // Getters and setters
}