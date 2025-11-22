package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.dto.ShopCreateDTO;
import com.web_app.yaviPrint.dto.ShopUpdateDTO;
import com.web_app.yaviPrint.service.ShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    @PostMapping
    public ResponseEntity<?> createShop(
            Authentication authentication,
            @Valid @RequestBody ShopCreateDTO shopCreateDTO) {
        try {
            // Get current user ID from authentication
            String email = authentication.getName();
            // In real implementation, you'd get user ID from user service
            Long ownerId = 1L; // Placeholder - get from authenticated user

            var shop = shopService.createShop(shopCreateDTO, ownerId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Shop created successfully",
                    "data", shop
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllActiveShops() {
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

    @GetMapping("/nearby")
    public ResponseEntity<?> getNearbyShops(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "5.0") Double radius) {
        try {
            var shops = shopService.getNearbyShops(lat, lng, radius);
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

    @GetMapping("/{shopId}")
    public ResponseEntity<?> getShopById(@PathVariable Long shopId) {
        try {
            var shop = shopService.getShopById(shopId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", shop
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PutMapping("/{shopId}")
    public ResponseEntity<?> updateShop(
            @PathVariable Long shopId,
            @Valid @RequestBody ShopUpdateDTO shopUpdateDTO) {
        try {
            var shop = shopService.updateShop(shopId, shopUpdateDTO);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Shop updated successfully",
                    "data", shop
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/my-shop")
    public ResponseEntity<?> getMyShop(Authentication authentication) {
        try {
            // Get current user ID from authentication
            Long ownerId = 1L; // Placeholder - get from authenticated user
            var shop = shopService.getShopByOwnerId(ownerId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", shop
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/{shopId}/toggle-status")
    public ResponseEntity<?> toggleShopStatus(
            @PathVariable Long shopId,
            @RequestParam Boolean online) {
        try {
            shopService.toggleShopStatus(shopId, online);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Shop status updated successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}