package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.dto.NotificationPreferencesDTO;
import com.web_app.yaviPrint.service.NotificationPreferencesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notification-preferences")
@RequiredArgsConstructor
public class NotificationPreferencesController {

    private final NotificationPreferencesService notificationPreferencesService;

    @GetMapping
    public ResponseEntity<?> getUserPreferences(Authentication authentication) {
        try {
            Long userId = 1L; // Placeholder - get from authenticated user
            var preferences = notificationPreferencesService.getUserPreferences(userId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", preferences
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PutMapping
    public ResponseEntity<?> updatePreferences(
            Authentication authentication,
            @Valid @RequestBody NotificationPreferencesDTO preferencesDTO) {
        try {
            Long userId = 1L; // Placeholder - get from authenticated user
            var preferences = notificationPreferencesService.createOrUpdatePreferences(userId, preferencesDTO);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Notification preferences updated successfully",
                    "data", preferences
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PatchMapping("/{preferenceType}")
    public ResponseEntity<?> updateSinglePreference(
            Authentication authentication,
            @PathVariable String preferenceType,
            @RequestParam Boolean value) {
        try {
            Long userId = 1L; // Placeholder - get from authenticated user
            var preferences = notificationPreferencesService.updatePreference(userId, preferenceType, value);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Preference updated successfully",
                    "data", preferences
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}