package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.dto.ShopTimingDTO;
import com.web_app.yaviPrint.service.ShopTimingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shops/{shopId}/timings")
@RequiredArgsConstructor
public class ShopTimingController {

    private final ShopTimingService shopTimingService;

    @PostMapping
    public ResponseEntity<?> createOrUpdateTiming(
            @PathVariable Long shopId,
            @Valid @RequestBody ShopTimingDTO timingDTO) {
        try {
            var timing = shopTimingService.createOrUpdateShopTiming(shopId, timingDTO);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Shop timing updated successfully",
                    "data", timing
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<?> getShopTimings(@PathVariable Long shopId) {
        try {
            List<ShopTimingDTO> timings = shopTimingService.getShopTimings(shopId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", timings
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PutMapping("/bulk")
    public ResponseEntity<?> updateAllTimings(
            @PathVariable Long shopId,
            @Valid @RequestBody List<ShopTimingDTO> timingsDTO) {
        try {
            // Delete existing timings and create new ones
            var existingTimings = shopTimingService.getShopTimings(shopId);
            for (var timing : existingTimings) {
                shopTimingService.deleteShopTiming(timing.getId());
            }

            for (var timingDTO : timingsDTO) {
                shopTimingService.createOrUpdateShopTiming(shopId, timingDTO);
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "All shop timings updated successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{timingId}")
    public ResponseEntity<?> deleteTiming(@PathVariable Long timingId) {
        try {
            shopTimingService.deleteShopTiming(timingId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Shop timing deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}