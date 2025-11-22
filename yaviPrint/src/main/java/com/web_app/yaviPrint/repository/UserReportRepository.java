package com.web_app.yaviPrint.repository;

import com.web_app.yaviPrint.entity.UserReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserReportRepository extends JpaRepository<UserReport, Long> {
    List<UserReport> findByReportedUserId(Long reportedUserId);
    List<UserReport> findByReportedById(Long reportedById);
    List<UserReport> findByStatus(String status);

    // ADD THE MISSING METHOD
    long countByStatus(String status);

    @Query("SELECT r FROM UserReport r WHERE r.status = 'PENDING' ORDER BY r.reportedAt ASC")
    List<UserReport> findPendingUserReports();

    @Query("SELECT r.reportType, COUNT(r) FROM UserReport r GROUP BY r.reportType")
    List<Object[]> countUserReportsByType();
}