package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.service.PrintPickupCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pickup-codes")
@RequiredArgsConstructor
public class PickupCodeController {

    private final PrintPickupCodeService printPickupCodeService;

    @GetMapping("/token/{tokenId}")
    public ResponseEntity<?> getPickupCodeByToken(@PathVariable String tokenId) {
        try {
            var pickupCode = printPickupCodeService.getPickupCodeByToken(tokenId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", pickupCode
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/short-code/{shortCode}")
    public ResponseEntity<?> getPickupCodeByShortCode(@PathVariable String shortCode) {
        try {
            var pickupCode = printPickupCodeService.getPickupCodeByShortCode(shortCode);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", pickupCode
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/{tokenId}/use")
    public ResponseEntity<?> usePickupCode(@PathVariable String tokenId) {
        try {
            printPickupCodeService.validateAndUsePickupCode(tokenId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Pickup code used successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/{tokenId}/mark-used")
    public ResponseEntity<?> markPickupCodeAsUsed(@PathVariable String tokenId) {
        try {
            var pickupCode = printPickupCodeService.markPickupCodeAsUsed(tokenId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Pickup code marked as used",
                    "data", pickupCode
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}