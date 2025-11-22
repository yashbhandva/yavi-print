package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/shop-management")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminShopManagementController {

    private final ShopService shopService;

    @GetMapping("/shops")
    public ResponseEntity<?> getAllShops() {
        try {
            var shops = shopService.getAllActiveShops();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", shops
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/shops/{shopId}/verify")
    public ResponseEntity<?> verifyShop(@PathVariable Long shopId) {
        try {
            // Implementation to verify shop
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Shop verified successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/shops/{shopId}/suspend")
    public ResponseEntity<?> suspendShop(@PathVariable Long shopId) {
        try {
            var shop = shopService.getShopById(shopId);
            shopService.toggleShopStatus(shopId, false);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Shop suspended successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/shops/{shopId}/activate")
    public ResponseEntity<?> activateShop(@PathVariable Long shopId) {
        try {
            shopService.toggleShopStatus(shopId, true);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Shop activated successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/shops/stats")
    public ResponseEntity<?> getShopStats() {
        try {
            var activeShops = shopService.getAllActiveShops().size();
            var totalShops = shopService.getAllActiveShops().size(); // Would need repository method for total

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "activeShops", activeShops,
                            "totalShops", totalShops,
                            "verifiedShops", activeShops // Placeholder
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