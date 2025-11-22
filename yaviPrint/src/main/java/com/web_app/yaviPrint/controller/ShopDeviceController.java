package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.dto.ShopDeviceDTO;
import com.web_app.yaviPrint.service.ShopDeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/shops/{shopId}/devices")
@RequiredArgsConstructor
public class ShopDeviceController {

    private final ShopDeviceService shopDeviceService;

    @PostMapping("/register")
    public ResponseEntity<?> registerDevice(
            @PathVariable Long shopId,
            @Valid @RequestBody ShopDeviceDTO deviceDTO) {
        try {
            var device = shopDeviceService.registerDevice(shopId, deviceDTO);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Device registered successfully",
                    "data", device
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<?> getShopDevices(@PathVariable Long shopId) {
        try {
            var devices = shopDeviceService.getShopDevices(shopId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", devices
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveShopDevices(@PathVariable Long shopId) {
        try {
            var devices = shopDeviceService.getActiveShopDevices(shopId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", devices
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/{deviceId}/status")
    public ResponseEntity<?> updateDeviceStatus(
            @PathVariable Long deviceId,
            @RequestParam boolean active) {
        try {
            shopDeviceService.updateDeviceStatus(deviceId, active);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Device status updated successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/heartbeat")
    public ResponseEntity<?> recordDeviceHeartbeat(
            @RequestParam String macAddress,
            @RequestParam String ipAddress) {
        try {
            shopDeviceService.recordDeviceHeartbeat(macAddress, ipAddress);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Heartbeat recorded successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}