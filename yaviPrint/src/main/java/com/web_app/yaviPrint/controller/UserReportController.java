package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.dto.UserReportDTO;
import com.web_app.yaviPrint.service.UserReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reports/users")
@RequiredArgsConstructor
public class UserReportController {

    private final UserReportService userReportService;

    @PostMapping("/{reportedUserId}")
    public ResponseEntity<?> createUserReport(
            Authentication authentication,
            @PathVariable Long reportedUserId,
            @Valid @RequestBody UserReportDTO reportDTO) {
        try {
            Long reportedById = 1L; // Placeholder - get from authenticated user
            var report = userReportService.createUserReport(reportedUserId, reportedById, reportDTO);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "User report submitted successfully",
                    "data", report
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/user/{reportedUserId}")
    public ResponseEntity<?> getUserReportsByReportedUser(@PathVariable Long reportedUserId) {
        try {
            var reports = userReportService.getUserReportsByReportedUser(reportedUserId);
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
    public ResponseEntity<?> getMyUserReports(Authentication authentication) {
        try {
            Long userId = 1L; // Placeholder - get from authenticated user
            var reports = userReportService.getUserReportsByReporter(userId);
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

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingUserReports() {
        try {
            var pendingReports = userReportService.getPendingUserReports();
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

    @PostMapping("/{reportId}/process")
    public ResponseEntity<?> processUserReport(
            @PathVariable Long reportId,
            @RequestParam String status,
            @RequestParam(required = false) String adminNotes) {
        try {
            var report = userReportService.updateUserReportStatus(reportId, status, adminNotes);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "User report processed successfully",
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
    public ResponseEntity<?> takeActionOnReportedUser(
            @PathVariable Long reportId,
            @RequestParam String action,
            @RequestParam String reason) {
        try {
            userReportService.takeActionOnReportedUser(reportId, action, reason);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Action taken on reported user: " + action
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}