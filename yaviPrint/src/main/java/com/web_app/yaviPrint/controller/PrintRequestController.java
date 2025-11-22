package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.dto.PrintRequestCreateDTO;
import com.web_app.yaviPrint.service.PrintRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/print-requests")
@RequiredArgsConstructor
public class PrintRequestController {

    private final PrintRequestService printRequestService;

    @PostMapping
    public ResponseEntity<?> createPrintRequest(
            Authentication authentication,
            @Valid @RequestBody PrintRequestCreateDTO requestDTO) {
        try {
            Long userId = 1L; // Placeholder - get from authenticated user
            var printRequest = printRequestService.createPrintRequest(requestDTO, userId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Print request created successfully",
                    "data", printRequest
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/my-orders")
    public ResponseEntity<?> getMyPrintRequests(Authentication authentication) {
        try {
            Long userId = 1L; // Placeholder - get from authenticated user
            var orders = printRequestService.getUserPrintRequests(userId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", orders
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getPrintRequestById(@PathVariable Long orderId) {
        try {
            var order = printRequestService.getPrintRequestById(orderId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", order
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/token/{tokenId}")
    public ResponseEntity<?> getPrintRequestByToken(@PathVariable String tokenId) {
        try {
            var order = printRequestService.getPrintRequestByToken(tokenId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
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
    public ResponseEntity<?> cancelPrintRequest(@PathVariable Long orderId) {
        try {
            printRequestService.cancelPrintRequest(orderId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Print request cancelled successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<?> getShopPrintRequests(@PathVariable Long shopId) {
        try {
            var orders = printRequestService.getShopPrintRequests(shopId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", orders
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/{orderId}/status")
    public ResponseEntity<?> updatePrintRequestStatus(
            @PathVariable Long orderId,
            @RequestParam String status) {
        try {
            var updatedOrder = printRequestService.updatePrintRequestStatus(orderId, status);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Order status updated successfully",
                    "data", updatedOrder
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}