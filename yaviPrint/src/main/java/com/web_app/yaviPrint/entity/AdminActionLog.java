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
@Table(name = "admin_action_logs")
public class AdminActionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private AdminUser adminUser;

    private String action; // CREATE_SHOP, VERIFY_USER, UPDATE_SETTINGS
    private String description;
    private String targetEntity; // SHOP, USER, ORDER
    private Long targetId;
    private String ipAddress;
    private LocalDateTime actionTime;
    private String oldValues;
    private String newValues;

    // Getters and setters
}