package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.dto.SystemSettingsDTO;
import com.web_app.yaviPrint.service.SystemSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/system-settings")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class SystemSettingsController {

    private final SystemSettingsService systemSettingsService;

    @GetMapping
    public ResponseEntity<?> getAllSettings() {
        try {
            var settings = systemSettingsService.getEditableSettings();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", settings
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/{key}")
    public ResponseEntity<?> getSettingByKey(@PathVariable String key) {
        try {
            var setting = systemSettingsService.getSettingByKey(key);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", setting
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PutMapping("/{key}")
    public ResponseEntity<?> updateSetting(
            @PathVariable String key,
            @RequestBody SystemSettingsDTO settingDTO) {
        try {
            var setting = systemSettingsService.createOrUpdateSetting(settingDTO);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Setting updated successfully",
                    "data", setting
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/{key}/value")
    public ResponseEntity<?> updateSettingValue(
            @PathVariable String key,
            @RequestParam String value) {
        try {
            var setting = systemSettingsService.updateSettingValue(key, value);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Setting value updated successfully",
                    "data", setting
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/maintenance")
    public ResponseEntity<?> toggleMaintenanceMode(@RequestParam boolean enabled) {
        try {
            systemSettingsService.setMaintenanceMode(enabled);
            String message = enabled ? "Maintenance mode enabled" : "Maintenance mode disabled";
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", message
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/initialize-defaults")
    public ResponseEntity<?> initializeDefaultSettings() {
        try {
            systemSettingsService.initializeDefaultSettings();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Default settings initialized successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}