package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.service.DailyShopStatsService;
import com.web_app.yaviPrint.service.DailyUserStatsService;
import com.web_app.yaviPrint.service.DailyRevenueService;
import com.web_app.yaviPrint.service.PrintVolumeStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/analytics")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AnalyticsDashboardController {

    private final DailyShopStatsService dailyShopStatsService;
    private final DailyUserStatsService dailyUserStatsService;
    private final DailyRevenueService dailyRevenueService;
    private final PrintVolumeStatsService printVolumeStatsService;

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            if (startDate == null) {
                startDate = LocalDate.now().minusDays(30);
            }
            if (endDate == null) {
                endDate = LocalDate.now();
            }

            // Get user growth metrics
            var userGrowth = dailyUserStatsService.getPlatformGrowthMetrics(startDate, endDate);

            // Get revenue summary
            var revenueSummary = dailyRevenueService.getRevenueSummary(startDate, endDate);

            // Get print volume summary
            var printVolumeSummary = printVolumeStatsService.getPrintVolumeSummary(startDate, endDate);

            // Get top performing shops
            var topShops = dailyShopStatsService.getTopPerformingShops(startDate, endDate, 5);

            Map<String, Object> dashboardData = Map.of(
                    "userGrowth", userGrowth,
                    "revenueSummary", revenueSummary,
                    "printVolumeSummary", printVolumeSummary,
                    "topPerformingShops", topShops,
                    "period", Map.of("startDate", startDate, "endDate", endDate)
            );

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", dashboardData
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/revenue/trend")
    public ResponseEntity<?> getRevenueTrend(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            var trendData = dailyRevenueService.getRevenueTrend(startDate, endDate);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", trendData
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/user-growth")
    public ResponseEntity<?> getUserGrowthStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            var userStats = dailyUserStatsService.getStatsForDateRange(startDate, endDate);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", userStats
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/print-volume")
    public ResponseEntity<?> getPrintVolumeStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            var volumeStats = printVolumeStatsService.getStatsForDateRange(startDate, endDate);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", volumeStats
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}