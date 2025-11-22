package com.web_app.yaviPrint.repository;

import com.web_app.yaviPrint.entity.ShopReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopReviewRepository extends JpaRepository<ShopReview, Long> {
    List<ShopReview> findByShopId(Long shopId);
    List<ShopReview> findByUserId(Long userId);
    Optional<ShopReview> findByOrderId(Long orderId);

    @Query("SELECT AVG(r.rating) FROM ShopReview r WHERE r.shop.id = :shopId")
    Double findAverageRatingByShopId(Long shopId);

    @Query("SELECT COUNT(r) FROM ShopReview r WHERE r.shop.id = :shopId")
    Long countByShopId(Long shopId);

    List<ShopReview> findByShopIdAndRatingOrderByReviewDateDesc(Long shopId, Integer rating);

    @Query("SELECT r FROM ShopReview r WHERE r.shop.id = :shopId AND r.isVerifiedPurchase = true")
    List<ShopReview> findVerifiedReviewsByShopId(Long shopId);
}