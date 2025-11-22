package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.service.ShopService;
import com.web_app.yaviPrint.service.SystemSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final ShopService shopService;
    private final SystemSettingsService systemSettingsService;

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "YaviPrint API",
                "timestamp", System.currentTimeMillis()
        ));
    }

    @GetMapping("/shops/nearby")
    public ResponseEntity<?> getNearbyShopsPublic(
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

    @GetMapping("/shops/city/{city}")
    public ResponseEntity<?> getShopsByCity(@PathVariable String city) {
        try {
            var shops = shopService.getShopsByCity(city);
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

    @GetMapping("/shops/{shopId}/details")
    public ResponseEntity<?> getShopPublicDetails(@PathVariable Long shopId) {
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

    @GetMapping("/maintenance-status")
    public ResponseEntity<?> getMaintenanceStatus() {
        try {
            boolean maintenanceMode = systemSettingsService.isMaintenanceMode();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of("maintenanceMode", maintenanceMode)
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of("maintenanceMode", false)
            ));
        }
    }

    @GetMapping("/supported-file-types")
    public ResponseEntity<?> getSupportedFileTypes() {
        try {
            String allowedTypes = systemSettingsService.getSettingValue("file.allowed.types");
            Integer maxFileSize = systemSettingsService.getSettingValueAsInteger("file.max.size");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "allowedFileTypes", allowedTypes != null ? allowedTypes.split(",") : new String[0],
                            "maxFileSizeBytes", maxFileSize != null ? maxFileSize : 10485760
                    )
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "allowedFileTypes", new String[]{"PDF", "DOC", "DOCX", "JPG", "JPEG", "PNG"},
                            "maxFileSizeBytes", 10485760
                    )
            ));
        }
    }
}