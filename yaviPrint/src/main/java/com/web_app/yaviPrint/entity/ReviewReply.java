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
@Table(name = "review_replies")
public class ReviewReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "review_id")
    private ShopReview review;

    @ManyToOne
    @JoinColumn(name = "shop_owner_id")
    private User shopOwner;

    private String replyMessage;
    private LocalDateTime replyDate;
    private boolean isEdited;
    private LocalDateTime editedAt;

    // Getters and setters
}