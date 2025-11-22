package com.web_app.yaviPrint.repository;

import com.web_app.yaviPrint.entity.DailyRevenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyRevenueRepository extends JpaRepository<DailyRevenue, Long> {
    Optional<DailyRevenue> findByRevenueDate(LocalDate revenueDate);
    List<DailyRevenue> findByRevenueDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(d.totalRevenue) FROM DailyRevenue d WHERE d.revenueDate BETWEEN :startDate AND :endDate")
    Double getTotalRevenueBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(d.platformCommission) FROM DailyRevenue d WHERE d.revenueDate BETWEEN :startDate AND :endDate")
    Double getTotalPlatformCommissionBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT AVG(d.totalRevenue) FROM DailyRevenue d WHERE d.revenueDate BETWEEN :startDate AND :endDate")
    Double getAverageDailyRevenue(LocalDate startDate, LocalDate endDate);

    @Query("SELECT d.revenueDate, d.totalRevenue FROM DailyRevenue d WHERE d.revenueDate BETWEEN :startDate AND :endDate ORDER BY d.revenueDate")
    List<Object[]> getRevenueTrend(LocalDate startDate, LocalDate endDate);
}