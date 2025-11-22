package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.dto.ShopReviewDTO;
import com.web_app.yaviPrint.service.ShopReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ShopReviewController {

    private final ShopReviewService shopReviewService;

    @PostMapping
    public ResponseEntity<?> createReview(
            Authentication authentication,
            @Valid @RequestBody ShopReviewDTO reviewDTO) {
        try {
            Long userId = 1L; // Placeholder - get from authenticated user
            var review = shopReviewService.createReview(reviewDTO, userId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Review submitted successfully",
                    "data", review
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<?> getShopReviews(@PathVariable Long shopId) {
        try {
            var reviews = shopReviewService.getShopReviews(shopId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", reviews
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/my-reviews")
    public ResponseEntity<?> getMyReviews(Authentication authentication) {
        try {
            Long userId = 1L; // Placeholder - get from authenticated user
            var reviews = shopReviewService.getUserReviews(userId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", reviews
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(
            Authentication authentication,
            @PathVariable Long reviewId,
            @Valid @RequestBody ShopReviewDTO reviewDTO) {
        try {
            Long userId = 1L; // Placeholder - get from authenticated user
            var review = shopReviewService.updateReview(reviewId, reviewDTO, userId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Review updated successfully",
                    "data", review
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(
            Authentication authentication,
            @PathVariable Long reviewId) {
        try {
            Long userId = 1L; // Placeholder - get from authenticated user
            shopReviewService.deleteReview(reviewId, userId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Review deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/{reviewId}/helpful")
    public ResponseEntity<?> markReviewAsHelpful(@PathVariable Long reviewId) {
        try {
            shopReviewService.markReviewAsHelpful(reviewId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Review marked as helpful"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}