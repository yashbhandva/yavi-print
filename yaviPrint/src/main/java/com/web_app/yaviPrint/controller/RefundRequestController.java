package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.service.RefundRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/refunds")
@RequiredArgsConstructor
public class RefundRequestController {

    private final RefundRequestService refundRequestService;

    @PostMapping("/order/{orderId}")
    public ResponseEntity<?> createRefundRequest(
            Authentication authentication,
            @PathVariable Long orderId,
            @RequestParam String reason) {
        try {
            var refundRequest = refundRequestService.createRefundRequest(orderId, reason);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Refund request submitted successfully",
                    "data", refundRequest
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/my-refunds")
    public ResponseEntity<?> getMyRefundRequests(Authentication authentication) {
        try {
            Long userId = 1L; // Placeholder - get from authenticated user
            // Implementation would get user's orders and their refund requests
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", "User refund requests would be returned here"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getRefundRequestsByOrder(@PathVariable Long orderId) {
        try {
            var refundRequests = refundRequestService.getRefundRequestsByOrderId(orderId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", refundRequests
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingRefundRequests() {
        try {
            var pendingRefunds = refundRequestService.getPendingRefundRequests();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", pendingRefunds
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/{refundRequestId}/process")
    public ResponseEntity<?> processRefundRequest(
            @PathVariable Long refundRequestId,
            @RequestParam String processedBy,
            @RequestParam boolean approve,
            @RequestParam(required = false) String notes) {
        try {
            var refundRequest = refundRequestService.processRefundRequest(refundRequestId, processedBy, approve, notes);
            String message = approve ? "Refund approved successfully" : "Refund rejected";
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", message,
                    "data", refundRequest
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}