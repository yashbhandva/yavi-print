package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.service.PrintJobStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders/{orderId}/job-status")
@RequiredArgsConstructor
public class PrintJobStatusController {

    private final PrintJobStatusService printJobStatusService;

    @GetMapping
    public ResponseEntity<?> getJobStatusHistory(@PathVariable Long orderId) {
        try {
            var statusHistory = printJobStatusService.getPrintJobStatusHistory(orderId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", statusHistory
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/progress")
    public ResponseEntity<?> updateJobProgress(
            @PathVariable Long orderId,
            @RequestParam String status,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) Integer progress) {
        try {
            var jobStatus = printJobStatusService.updatePrintJobStatus(orderId, status, message, progress);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Job status updated successfully",
                    "data", jobStatus
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/complete")
    public ResponseEntity<?> markJobAsComplete(@PathVariable Long orderId) {
        try {
            printJobStatusService.recordPrintJobCompletion(orderId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Job marked as completed"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/failure")
    public ResponseEntity<?> markJobAsFailed(
            @PathVariable Long orderId,
            @RequestParam String errorMessage) {
        try {
            printJobStatusService.recordPrintJobFailure(orderId, errorMessage);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Job marked as failed"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}