package com.web_app.yaviPrint.repository;

import com.web_app.yaviPrint.entity.RefundRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefundRequestRepository extends JpaRepository<RefundRequest, Long> {
    List<RefundRequest> findByOrderId(Long orderId);
    List<RefundRequest> findByStatus(String status);

    @Query("SELECT r FROM RefundRequest r WHERE r.status = 'PENDING' ORDER BY r.requestedAt ASC")
    List<RefundRequest> findPendingRefunds();

    long countByStatus(String status);
}