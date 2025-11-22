package com.web_app.yaviPrint.repository;

import com.web_app.yaviPrint.entity.PrintJobHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrintJobHistoryRepository extends JpaRepository<PrintJobHistory, Long> {
    List<PrintJobHistory> findByOrderIdOrderByActionTimeDesc(Long orderId);
    List<PrintJobHistory> findByOrderId(Long orderId);
}