package com.web_app.yaviPrint.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "notification_templates")
public class NotificationTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String templateName;
    private String templateType; // EMAIL, SMS, PUSH
    private String subject;
    private String content;
    private String variables; // JSON: {"name", "orderId", "amount"}
    private boolean isActive;
    private String language; // en, hi, etc.

    // Getters and setters
}