package com.web_app.yaviPrint.repository;

import com.web_app.yaviPrint.entity.ReviewReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewReplyRepository extends JpaRepository<ReviewReply, Long> {
    Optional<ReviewReply> findByReviewId(Long reviewId);
    boolean existsByReviewId(Long reviewId);
    void deleteByReviewId(Long reviewId);
}