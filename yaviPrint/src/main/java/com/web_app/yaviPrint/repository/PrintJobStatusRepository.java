package com.web_app.yaviPrint.repository;

import com.web_app.yaviPrint.entity.PrintJobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrintJobStatusRepository extends JpaRepository<PrintJobStatus, Long> {
    List<PrintJobStatus> findByOrderIdOrderByStatusTimeDesc(Long orderId);
    List<PrintJobStatus> findByOrderId(Long orderId);
}