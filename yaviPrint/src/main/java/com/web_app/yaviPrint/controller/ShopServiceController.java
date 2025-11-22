package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.dto.ShopServiceDTO;
import com.web_app.yaviPrint.service.ShopServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/shops/{shopId}/services")
@RequiredArgsConstructor
public class ShopServiceController {

    private final ShopServiceService shopServiceService;

    @PostMapping
    public ResponseEntity<?> createShopService(
            @PathVariable Long shopId,
            @Valid @RequestBody ShopServiceDTO serviceDTO) {
        try {
            var service = shopServiceService.createShopService(shopId, serviceDTO);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Shop service created successfully",
                    "data", service
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<?> getShopServices(@PathVariable Long shopId) {
        try {
            var services = shopServiceService.getShopServices(shopId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", services
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/available")
    public ResponseEntity<?> getAvailableShopServices(@PathVariable Long shopId) {
        try {
            var services = shopServiceService.getAvailableShopServices(shopId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", services
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PutMapping("/{serviceId}")
    public ResponseEntity<?> updateShopService(
            @PathVariable Long serviceId,
            @Valid @RequestBody ShopServiceDTO serviceDTO) {
        try {
            var service = shopServiceService.updateShopService(serviceId, serviceDTO);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Shop service updated successfully",
                    "data", service
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{serviceId}")
    public ResponseEntity<?> deleteShopService(@PathVariable Long serviceId) {
        try {
            shopServiceService.deleteShopService(serviceId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Shop service deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}