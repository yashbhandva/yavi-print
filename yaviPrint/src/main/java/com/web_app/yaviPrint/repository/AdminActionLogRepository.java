package com.web_app.yaviPrint.repository;

import com.web_app.yaviPrint.entity.AdminActionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdminActionLogRepository extends JpaRepository<AdminActionLog, Long> {
    List<AdminActionLog> findByAdminUserIdOrderByActionTimeDesc(Long adminUserId);

    @Query("SELECT l FROM AdminActionLog l WHERE l.actionTime BETWEEN :startDate AND :endDate")
    List<AdminActionLog> findByActionTimeBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<AdminActionLog> findByTargetEntityAndTargetId(String targetEntity, Long targetId);

    @Query("SELECT l.targetEntity, COUNT(l) FROM AdminActionLog l GROUP BY l.targetEntity")
    List<Object[]> countActionsByEntityType();
}