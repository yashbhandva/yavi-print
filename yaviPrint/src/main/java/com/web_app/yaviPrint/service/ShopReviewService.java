package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.ShopReviewDTO;
import com.web_app.yaviPrint.entity.Order;
import com.web_app.yaviPrint.entity.Shop;
import com.web_app.yaviPrint.entity.ShopReview;
import com.web_app.yaviPrint.entity.User;
import com.web_app.yaviPrint.repository.OrderRepository;
import com.web_app.yaviPrint.repository.ShopRepository;
import com.web_app.yaviPrint.repository.ShopReviewRepository;
import com.web_app.yaviPrint.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopReviewService {

    private final ShopReviewRepository shopReviewRepository;
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public ShopReviewDTO createReview(ShopReviewDTO reviewDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Shop shop = shopRepository.findById(reviewDTO.getShopId())
                .orElseThrow(() -> new RuntimeException("Shop not found with id: " + reviewDTO.getShopId()));

        Order order = null;
        if (reviewDTO.getOrderId() != null) {
            order = orderRepository.findById(reviewDTO.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Order not found with id: " + reviewDTO.getOrderId()));
        }

        // Check if user already reviewed this shop
        if (order != null) {
            shopReviewRepository.findByOrderId(reviewDTO.getOrderId())
                    .ifPresent(existingReview -> {
                        throw new RuntimeException("You have already reviewed this order");
                    });
        }

        ShopReview review = new ShopReview();
        review.setShop(shop);
        review.setUser(user);
        review.setOrder(order);
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        review.setReviewDate(LocalDateTime.now());
        review.setVerifiedPurchase(order != null);
        review.setHelpfulCount(0);

        ShopReview savedReview = shopReviewRepository.save(review);

        // Update shop rating
        updateShopRating(shop);

        return mapToShopReviewDTO(savedReview);
    }

    public List<ShopReviewDTO> getShopReviews(Long shopId) {
        return shopReviewRepository.findByShopId(shopId).stream()
                .map(this::mapToShopReviewDTO)
                .collect(Collectors.toList());
    }

    public List<ShopReviewDTO> getUserReviews(Long userId) {
        return shopReviewRepository.findByUserId(userId).stream()
                .map(this::mapToShopReviewDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ShopReviewDTO updateReview(Long reviewId, ShopReviewDTO reviewDTO, Long userId) {
        ShopReview review = shopReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + reviewId));

        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only edit your own reviews");
        }

        if (reviewDTO.getRating() != null) {
            review.setRating(reviewDTO.getRating());
        }
        if (reviewDTO.getComment() != null) {
            review.setComment(reviewDTO.getComment());
        }
        review.setEdited(true);
        review.setEditedAt(LocalDateTime.now());

        ShopReview updatedReview = shopReviewRepository.save(review);

        // Update shop rating
        updateShopRating(review.getShop());

        return mapToShopReviewDTO(updatedReview);
    }

    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        ShopReview review = shopReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + reviewId));

        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only delete your own reviews");
        }

        Shop shop = review.getShop();
        shopReviewRepository.delete(review);

        // Update shop rating
        updateShopRating(shop);
    }

    @Transactional
    public void markReviewAsHelpful(Long reviewId) {
        ShopReview review = shopReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + reviewId));

        review.setHelpfulCount(review.getHelpfulCount() + 1);
        shopReviewRepository.save(review);
    }

    private void updateShopRating(Shop shop) {
        Double averageRating = shopReviewRepository.findAverageRatingByShopId(shop.getId());
        Long totalReviews = shopReviewRepository.countByShopId(shop.getId());

        shop.setRating(averageRating != null ? averageRating : 0.0);
        shop.setTotalReviews(totalReviews != null ? totalReviews.intValue() : 0);
        shopRepository.save(shop);
    }

    private ShopReviewDTO mapToShopReviewDTO(ShopReview review) {
        ShopReviewDTO dto = new ShopReviewDTO();
        dto.setId(review.getId());
        dto.setShopId(review.getShop().getId());
        dto.setUserId(review.getUser().getId());
        dto.setOrderId(review.getOrder() != null ? review.getOrder().getId() : null);
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setReviewDate(review.getReviewDate());
        dto.setVerifiedPurchase(review.isVerifiedPurchase());
        dto.setHelpfulCount(review.getHelpfulCount());
        dto.setEdited(review.isEdited());
        dto.setEditedAt(review.getEditedAt());
        return dto;
    }
}