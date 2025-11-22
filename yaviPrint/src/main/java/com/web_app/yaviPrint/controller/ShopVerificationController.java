package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.service.ShopVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/shops/{shopId}/verification")
@RequiredArgsConstructor
public class ShopVerificationController {

    private final ShopVerificationService shopVerificationService;

    @PostMapping("/apply")
    public ResponseEntity<?> applyForVerification(
            @PathVariable Long shopId,
            @RequestParam String documents) {
        try {
            var verification = shopVerificationService.applyForVerification(shopId, documents);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Verification application submitted successfully",
                    "data", verification
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<?> getShopVerification(@PathVariable Long shopId) {
        try {
            var verification = shopVerificationService.getShopVerification(shopId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", verification
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/approve")
    public ResponseEntity<?> approveVerification(
            @PathVariable Long shopId,
            @RequestParam String verifiedBy) {
        try {
            var verification = shopVerificationService.verifyShop(shopId, verifiedBy);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Shop verification approved",
                    "data", verification
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/reject")
    public ResponseEntity<?> rejectVerification(
            @PathVariable Long shopId,
            @RequestParam String rejectionReason,
            @RequestParam String verifiedBy) {
        try {
            var verification = shopVerificationService.rejectVerification(shopId, rejectionReason, verifiedBy);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Shop verification rejected",
                    "data", verification
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingVerifications() {
        try {
            var pendingVerifications = shopVerificationService.getPendingVerifications();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", pendingVerifications
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}