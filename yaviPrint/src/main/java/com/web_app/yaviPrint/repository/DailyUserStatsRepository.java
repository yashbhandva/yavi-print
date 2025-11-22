package com.web_app.yaviPrint.repository;

import com.web_app.yaviPrint.entity.DailyUserStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyUserStatsRepository extends JpaRepository<DailyUserStats, Long> {
    Optional<DailyUserStats> findByStatDate(LocalDate statDate);
    List<DailyUserStats> findByStatDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(d.newRegistrations) FROM DailyUserStats d WHERE d.statDate BETWEEN :startDate AND :endDate")
    Long getTotalNewRegistrations(LocalDate startDate, LocalDate endDate);

    @Query("SELECT AVG(d.activeUsers) FROM DailyUserStats d WHERE d.statDate BETWEEN :startDate AND :endDate")
    Double getAverageActiveUsers(LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(d.totalRevenue) FROM DailyUserStats d WHERE d.statDate BETWEEN :startDate AND :endDate")
    Double getTotalRevenueInPeriod(LocalDate startDate, LocalDate endDate);
}