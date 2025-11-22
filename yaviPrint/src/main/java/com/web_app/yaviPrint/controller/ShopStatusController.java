package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.dto.ShopStatusDTO;
import com.web_app.yaviPrint.service.ShopStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/shops/{shopId}/status")
@RequiredArgsConstructor
public class ShopStatusController {

    private final ShopStatusService shopStatusService;

    @GetMapping
    public ResponseEntity<?> getShopStatus(@PathVariable Long shopId) {
        try {
            var status = shopStatusService.getShopStatus(shopId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", status
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PutMapping
    public ResponseEntity<?> updateShopStatus(
            @PathVariable Long shopId,
            @RequestBody ShopStatusDTO statusDTO) {
        try {
            var status = shopStatusService.updateShopStatus(shopId, statusDTO);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Shop status updated successfully",
                    "data", status
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/busy")
    public ResponseEntity<?> setShopBusy(
            @PathVariable Long shopId,
            @RequestParam String reason) {
        try {
            shopStatusService.setShopBusy(shopId, reason);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Shop marked as busy"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/available")
    public ResponseEntity<?> setShopAvailable(@PathVariable Long shopId) {
        try {
            shopStatusService.setShopAvailable(shopId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Shop marked as available"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/queue")
    public ResponseEntity<?> updateQueueSize(
            @PathVariable Long shopId,
            @RequestParam int queueSize) {
        try {
            shopStatusService.updateQueueSize(shopId, queueSize);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Queue size updated successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}