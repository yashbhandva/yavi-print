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
@Table(name = "admin_users")
public class AdminUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;
    private String role; // SUPER_ADMIN, SUPPORT_ADMIN, FINANCE_ADMIN
    private boolean isActive;
    private LocalDateTime lastLogin;
    private String permissions; // JSON array of permissions

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // Getters and setters
}