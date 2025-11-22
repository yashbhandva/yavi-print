package com.web_app.yaviPrint.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "notification_preferences")
public class NotificationPreferences {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private boolean emailNotifications;
    private boolean smsNotifications;
    private boolean pushNotifications;
    private boolean orderUpdates;
    private boolean promotional;
    private boolean priceAlerts;
    private boolean shopUpdates;

    // Getters and setters
}