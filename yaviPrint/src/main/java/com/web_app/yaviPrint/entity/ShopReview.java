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
@Table(name = "shop_reviews")
public class ShopReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private int rating; // 1-5
    private String comment;
    private LocalDateTime reviewDate;
    private boolean isVerifiedPurchase;
    private int helpfulCount;
    private boolean isEdited;
    private LocalDateTime editedAt;

    // Getters and setters
}