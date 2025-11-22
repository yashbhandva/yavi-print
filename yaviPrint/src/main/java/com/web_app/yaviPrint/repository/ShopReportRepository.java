package com.web_app.yaviPrint.repository;

import com.web_app.yaviPrint.entity.ShopReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopReportRepository extends JpaRepository<ShopReport, Long> {
    List<ShopReport> findByShopId(Long shopId);
    List<ShopReport> findByStatus(String status);
    List<ShopReport> findByReportedById(Long reportedById);

    // ADD MISSING METHOD
    long countByStatus(String status);

    @Query("SELECT r FROM ShopReport r WHERE r.status = 'PENDING' ORDER BY r.reportedAt ASC")
    List<ShopReport> findPendingReports();

    @Query("SELECT r.reportType, COUNT(r) FROM ShopReport r GROUP BY r.reportType")
    List<Object[]> countReportsByType();
}