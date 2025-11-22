package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.service.PrintQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/shops/{shopId}/queue")
@RequiredArgsConstructor
public class PrintQueueController {

    private final PrintQueueService printQueueService;

    @GetMapping
    public ResponseEntity<?> getShopPrintQueue(@PathVariable Long shopId) {
        try {
            var queue = printQueueService.getShopPrintQueue(shopId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", queue
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/priority")
    public ResponseEntity<?> getPriorityQueue(@PathVariable Long shopId) {
        try {
            var queue = printQueueService.getPriorityPrintQueue(shopId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", queue
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/next")
    public ResponseEntity<?> moveToNextInQueue(@PathVariable Long shopId) {
        try {
            var nextOrder = printQueueService.moveToNextInQueue(shopId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Moved to next order in queue",
                    "data", nextOrder
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/{orderId}/start-printing")
    public ResponseEntity<?> startPrintingOrder(@PathVariable Long orderId) {
        try {
            var order = printQueueService.startPrintingOrder(orderId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Order printing started",
                    "data", order
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/{orderId}/mark-ready")
    public ResponseEntity<?> markOrderAsReady(@PathVariable Long orderId) {
        try {
            var order = printQueueService.markOrderAsReady(orderId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Order marked as ready for pickup",
                    "data", order
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrderFromQueue(
            @PathVariable Long orderId,
            @RequestParam String reason) {
        try {
            printQueueService.cancelOrderFromQueue(orderId, reason);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Order cancelled from queue"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getQueueStatus(@PathVariable Long shopId) {
        try {
            var status = printQueueService.getQueueStatus(shopId);
            var queueSize = printQueueService.getQueueSize(shopId);
            var estimatedCompletion = printQueueService.getEstimatedCompletionTime(shopId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "status", status,
                            "queueSize", queueSize,
                            "estimatedCompletion", estimatedCompletion
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