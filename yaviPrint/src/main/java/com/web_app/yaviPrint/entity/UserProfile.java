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
@Table(name = "user_profiles")
public class UserProfile {
    @Id
    private Long id; // Same as User ID

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private String profilePicture;
    private LocalDate dateOfBirth;
    private String gender;
    private String alternatePhone;

    @Embedded
    private Address address;

    private String preferences; // JSON: {"notifications": true, "newsletter": false}

    // Getters and setters
}