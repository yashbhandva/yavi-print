package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.service.AdminActionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/action-logs")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminActionLogController {

    private final AdminActionLogService adminActionLogService;

    @GetMapping
    public ResponseEntity<?> getAdminActionLogs(
            @RequestParam(required = false) Long adminUserId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            if (adminUserId != null) {
                var logs = adminActionLogService.getAdminActionLogs(adminUserId);
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "data", logs
                ));
            } else if (startDate != null && endDate != null) {
                var logs = adminActionLogService.getActionLogsByDateRange(startDate, endDate);
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "data", logs
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "Either adminUserId or date range must be provided"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<?> getActionLogsByEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        try {
            var logs = adminActionLogService.getActionLogsByEntity(entityType, entityId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", logs
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<?> getActionStatistics() {
        try {
            var statistics = adminActionLogService.getActionStatisticsByEntity();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", statistics
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}