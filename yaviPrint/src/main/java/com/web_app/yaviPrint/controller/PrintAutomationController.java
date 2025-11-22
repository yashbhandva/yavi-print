package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.service.PrintAutomationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/automation")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class PrintAutomationController {

    private final PrintAutomationService printAutomationService;

    @PostMapping("/process-pending")
    public ResponseEntity<?> processPendingOrders() {
        try {
            printAutomationService.processPendingOrders();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Pending orders processed successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/auto-accept")
    public ResponseEntity<?> autoAcceptOrders() {
        try {
            printAutomationService.autoAcceptOrders();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Auto-accept orders processed successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/cleanup-expired")
    public ResponseEntity<?> cleanupExpiredOrders() {
        try {
            printAutomationService.cleanupExpiredOrders();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Expired orders cleaned up successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/reassign-overflow")
    public ResponseEntity<?> reassignOverflowOrders() {
        try {
            printAutomationService.reassignOverflowOrders();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Overflow orders reassigned successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getAutomationStatus() {
        try {
            // Return automation system status
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "automationEnabled", true,
                            "lastRun", System.currentTimeMillis(),
                            "nextScheduledRun", System.currentTimeMillis() + 300000 // 5 minutes
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