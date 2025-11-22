package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.service.PrintJobHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders/{orderId}/history")
@RequiredArgsConstructor
public class PrintJobHistoryController {

    private final PrintJobHistoryService printJobHistoryService;

    @GetMapping
    public ResponseEntity<?> getOrderHistory(@PathVariable Long orderId) {
        try {
            var history = printJobHistoryService.getOrderHistory(orderId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", history
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/timeline")
    public ResponseEntity<?> getOrderTimeline(@PathVariable Long orderId) {
        try {
            var timeline = printJobHistoryService.getOrderTimeline(orderId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of("timeline", timeline)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/recent")
    public ResponseEntity<?> getRecentActivity(
            @PathVariable Long orderId,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            var recentActivity = printJobHistoryService.getRecentActivity(orderId, limit);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", recentActivity
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}