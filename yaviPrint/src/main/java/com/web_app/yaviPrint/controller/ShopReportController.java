package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.dto.ShopReportDTO;
import com.web_app.yaviPrint.service.ShopReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reports/shops")
@RequiredArgsConstructor
public class ShopReportController {

    private final ShopReportService shopReportService;

    @PostMapping("/{shopId}")
    public ResponseEntity<?> createShopReport(
            Authentication authentication,
            @PathVariable Long shopId,
            @Valid @RequestBody ShopReportDTO reportDTO) {
        try {
            Long reportedById = 1L; // Placeholder - get from authenticated user
            var report = shopReportService.createShopReport(shopId, reportedById, reportDTO);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Shop report submitted successfully",
                    "data", report
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<?> getShopReports(@PathVariable Long shopId) {
        try {
            var reports = shopReportService.getShopReports(shopId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", reports
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/my-reports")
    public ResponseEntity<?> getMyShopReports(Authentication authentication) {
        try {
            Long userId = 1L; // Placeholder - get from authenticated user
            // Implementation would get reports created by this user
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", "User's shop reports would be returned here"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingShopReports() {
        try {
            var pendingReports = shopReportService.getPendingShopReports();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", pendingReports
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/{reportId}/investigate")
    public ResponseEntity<?> investigateReport(
            @PathVariable Long reportId,
            @RequestParam String adminNotes,
            @RequestParam String processedBy) {
        try {
            var report = shopReportService.investigateReport(reportId, adminNotes, processedBy);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Report marked as under investigation",
                    "data", report
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/{reportId}/resolve")
    public ResponseEntity<?> resolveReport(
            @PathVariable Long reportId,
            @RequestParam String adminNotes,
            @RequestParam String processedBy) {
        try {
            var report = shopReportService.resolveReport(reportId, adminNotes, processedBy);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Report resolved successfully",
                    "data", report
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/{reportId}/action")
    public ResponseEntity<?> takeActionOnReportedShop(
            @PathVariable Long reportId,
            @RequestParam String action,
            @RequestParam String reason) {
        try {
            shopReportService.takeActionOnReportedShop(reportId, action, reason);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Action taken on reported shop: " + action
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}