package com.web_app.yaviPrint.repository;

import com.web_app.yaviPrint.entity.PrintVolumeStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PrintVolumeStatsRepository extends JpaRepository<PrintVolumeStats, Long> {
    Optional<PrintVolumeStats> findByStatDate(LocalDate statDate);
    List<PrintVolumeStats> findByStatDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(p.totalPagesPrinted) FROM PrintVolumeStats p WHERE p.statDate BETWEEN :startDate AND :endDate")
    Long getTotalPagesPrintedBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(p.bwPages) FROM PrintVolumeStats p WHERE p.statDate BETWEEN :startDate AND :endDate")
    Long getTotalBWPagesBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(p.colorPages) FROM PrintVolumeStats p WHERE p.statDate BETWEEN :startDate AND :endDate")
    Long getTotalColorPagesBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT AVG(p.totalPagesPrinted) FROM PrintVolumeStats p WHERE p.statDate BETWEEN :startDate AND :endDate")
    Double getAverageDailyPrintVolume(LocalDate startDate, LocalDate endDate);
}