package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.dto.ReviewReplyDTO;
import com.web_app.yaviPrint.service.ReviewReplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reviews/{reviewId}/replies")
@RequiredArgsConstructor
public class ReviewReplyController {

    private final ReviewReplyService reviewReplyService;

    @PostMapping
    public ResponseEntity<?> createReply(
            Authentication authentication,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewReplyDTO replyDTO) {
        try {
            Long shopOwnerId = 1L; // Placeholder - get from authenticated user
            var reply = reviewReplyService.createReply(reviewId, replyDTO, shopOwnerId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Reply submitted successfully",
                    "data", reply
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<?> getReplyByReviewId(@PathVariable Long reviewId) {
        try {
            var reply = reviewReplyService.getReplyByReviewId(reviewId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", reply
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PutMapping("/{replyId}")
    public ResponseEntity<?> updateReply(
            Authentication authentication,
            @PathVariable Long replyId,
            @Valid @RequestBody ReviewReplyDTO replyDTO) {
        try {
            Long shopOwnerId = 1L; // Placeholder - get from authenticated user
            var reply = reviewReplyService.updateReply(replyId, replyDTO, shopOwnerId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Reply updated successfully",
                    "data", reply
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<?> deleteReply(
            Authentication authentication,
            @PathVariable Long replyId) {
        try {
            Long shopOwnerId = 1L; // Placeholder - get from authenticated user
            reviewReplyService.deleteReply(replyId, shopOwnerId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Reply deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}