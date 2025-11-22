package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.dto.GeoLocationDTO;
import com.web_app.yaviPrint.service.GeoLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final GeoLocationService geoLocationService;

    @PostMapping("/shops/{shopId}")
    public ResponseEntity<?> updateShopLocation(
            @PathVariable Long shopId,
            @RequestBody GeoLocationDTO locationDTO) {
        try {
            var location = geoLocationService.updateShopLocation(shopId, locationDTO);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Shop location updated successfully",
                    "data", location
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/shops/{shopId}")
    public ResponseEntity<?> getShopLocation(@PathVariable Long shopId) {
        try {
            var location = geoLocationService.getShopLocation(shopId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", location
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/nearby-shops")
    public ResponseEntity<?> findNearbyShops(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "5.0") Double radius) {
        try {
            var shops = geoLocationService.findNearbyShops(lat, lng, radius);
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
            var shops = geoLocationService.getLocationsByCity(city);
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

    @GetMapping("/distance")
    public ResponseEntity<?> calculateDistance(
            @RequestParam Double lat1,
            @RequestParam Double lng1,
            @RequestParam Double lat2,
            @RequestParam Double lng2) {
        try {
            Double distance = geoLocationService.calculateDistance(lat1, lng1, lat2, lng2);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "distanceKm", distance,
                            "distanceMeters", distance * 1000
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