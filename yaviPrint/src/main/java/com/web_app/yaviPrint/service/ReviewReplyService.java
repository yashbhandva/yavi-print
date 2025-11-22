package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.ReviewReplyDTO;
import com.web_app.yaviPrint.entity.ReviewReply;
import com.web_app.yaviPrint.entity.ShopReview;
import com.web_app.yaviPrint.entity.User;
import com.web_app.yaviPrint.repository.ReviewReplyRepository;
import com.web_app.yaviPrint.repository.ShopReviewRepository;
import com.web_app.yaviPrint.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReviewReplyService {

    private final ReviewReplyRepository reviewReplyRepository;
    private final ShopReviewRepository shopReviewRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReviewReplyDTO createReply(Long reviewId, ReviewReplyDTO replyDTO, Long shopOwnerId) {
        ShopReview review = shopReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + reviewId));

        User shopOwner = userRepository.findById(shopOwnerId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + shopOwnerId));

        // Check if shop owner owns the shop being reviewed
        if (!review.getShop().getOwner().getId().equals(shopOwnerId)) {
            throw new RuntimeException("You can only reply to reviews for your own shop");
        }

        // Check if reply already exists
        if (reviewReplyRepository.existsByReviewId(reviewId)) {
            throw new RuntimeException("Reply already exists for this review");
        }

        ReviewReply reply = new ReviewReply();
        reply.setReview(review);
        reply.setShopOwner(shopOwner);
        reply.setReplyMessage(replyDTO.getReplyMessage());
        reply.setReplyDate(LocalDateTime.now());

        ReviewReply savedReply = reviewReplyRepository.save(reply);
        return mapToReviewReplyDTO(savedReply);
    }

    public ReviewReplyDTO getReplyByReviewId(Long reviewId) {
        ReviewReply reply = reviewReplyRepository.findByReviewId(reviewId)
                .orElseThrow(() -> new RuntimeException("Reply not found for review id: " + reviewId));
        return mapToReviewReplyDTO(reply);
    }

    @Transactional
    public ReviewReplyDTO updateReply(Long replyId, ReviewReplyDTO replyDTO, Long shopOwnerId) {
        ReviewReply reply = reviewReplyRepository.findById(replyId)
                .orElseThrow(() -> new RuntimeException("Reply not found with id: " + replyId));

        if (!reply.getShopOwner().getId().equals(shopOwnerId)) {
            throw new RuntimeException("You can only edit your own replies");
        }

        if (replyDTO.getReplyMessage() != null) {
            reply.setReplyMessage(replyDTO.getReplyMessage());
        }
        reply.setEdited(true);
        reply.setEditedAt(LocalDateTime.now());

        ReviewReply updatedReply = reviewReplyRepository.save(reply);
        return mapToReviewReplyDTO(updatedReply);
    }

    @Transactional
    public void deleteReply(Long replyId, Long shopOwnerId) {
        ReviewReply reply = reviewReplyRepository.findById(replyId)
                .orElseThrow(() -> new RuntimeException("Reply not found with id: " + replyId));

        if (!reply.getShopOwner().getId().equals(shopOwnerId)) {
            throw new RuntimeException("You can only delete your own replies");
        }

        reviewReplyRepository.delete(reply);
    }

    private ReviewReplyDTO mapToReviewReplyDTO(ReviewReply reply) {
        ReviewReplyDTO dto = new ReviewReplyDTO();
        dto.setId(reply.getId());
        dto.setReviewId(reply.getReview().getId());
        dto.setShopOwnerId(reply.getShopOwner().getId());
        dto.setReplyMessage(reply.getReplyMessage());
        dto.setReplyDate(reply.getReplyDate());
        dto.setEdited(reply.isEdited());
        dto.setEditedAt(reply.getEditedAt());
        return dto;
    }
}