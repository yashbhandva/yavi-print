package com.web_app.yaviPrint.repository;

import com.web_app.yaviPrint.entity.DailyShopStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyShopStatsRepository extends JpaRepository<DailyShopStats, Long> {
    Optional<DailyShopStats> findByShopIdAndStatDate(Long shopId, LocalDate statDate);
    List<DailyShopStats> findByShopIdAndStatDateBetween(Long shopId, LocalDate startDate, LocalDate endDate);
    List<DailyShopStats> findByStatDate(LocalDate statDate);

    @Query("SELECT d FROM DailyShopStats d WHERE d.statDate BETWEEN :startDate AND :endDate ORDER BY d.totalRevenue DESC")
    List<DailyShopStats> findTopPerformingShops(LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(d.totalRevenue) FROM DailyShopStats d WHERE d.shop.id = :shopId AND d.statDate BETWEEN :startDate AND :endDate")
    Double getTotalRevenueByShopAndDateRange(Long shopId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT AVG(d.averageRating) FROM DailyShopStats d WHERE d.shop.id = :shopId AND d.statDate BETWEEN :startDate AND :endDate")
    Double getAverageRatingByShopAndDateRange(Long shopId, LocalDate startDate, LocalDate endDate);
}