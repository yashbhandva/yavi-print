package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.service.DailyShopStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/shops/{shopId}/analytics")
@RequiredArgsConstructor
public class ShopAnalyticsController {

    private final DailyShopStatsService dailyShopStatsService;

    @GetMapping("/performance")
    public ResponseEntity<?> getShopPerformance(
            Authentication authentication,
            @PathVariable Long shopId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            if (startDate == null) {
                startDate = LocalDate.now().minusDays(30);
            }
            if (endDate == null) {
                endDate = LocalDate.now();
            }

            // Verify shop ownership (in production)
            // Long ownerId = getCurrentUserId(authentication);
            // verifyShopOwnership(shopId, ownerId);

            var performanceSummary = dailyShopStatsService.getShopPerformanceSummary(shopId, startDate, endDate);
            var dailyStats = dailyShopStatsService.getShopStatsForDateRange(shopId, startDate, endDate);

            Map<String, Object> analyticsData = Map.of(
                    "performanceSummary", performanceSummary,
                    "dailyStats", dailyStats,
                    "period", Map.of("startDate", startDate, "endDate", endDate)
            );

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", analyticsData
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/revenue")
    public ResponseEntity<?> getShopRevenue(
            @PathVariable Long shopId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            var totalRevenue = dailyShopStatsService.getShopTotalRevenueForPeriod(shopId, startDate, endDate);
            var dailyStats = dailyShopStatsService.getShopStatsForDateRange(shopId, startDate, endDate);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "totalRevenue", totalRevenue,
                            "dailyBreakdown", dailyStats
                    )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getShopOrderStats(
            @PathVariable Long shopId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            var dailyStats = dailyShopStatsService.getShopStatsForDateRange(shopId, startDate, endDate);

            int totalOrders = dailyStats.stream()
                    .mapToInt(stat -> stat.getTotalOrders())
                    .sum();
            int completedOrders = dailyStats.stream()
                    .mapToInt(stat -> stat.getCompletedOrders())
                    .sum();
            double completionRate = totalOrders > 0 ? (double) completedOrders / totalOrders * 100 : 0;

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "totalOrders", totalOrders,
                            "completedOrders", completedOrders,
                            "completionRate", completionRate,
                            "dailyBreakdown", dailyStats
                    )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/customers")
    public ResponseEntity<?> getShopCustomerStats(
            @PathVariable Long shopId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            var dailyStats = dailyShopStatsService.getShopStatsForDateRange(shopId, startDate, endDate);

            int totalNewCustomers = dailyStats.stream()
                    .mapToInt(stat -> stat.getNewCustomers())
                    .sum();
            double averageRating = dailyShopStatsService.getShopAverageRatingForPeriod(shopId, startDate, endDate);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "newCustomers", totalNewCustomers,
                            "averageRating", averageRating,
                            "dailyBreakdown", dailyStats
                    )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}