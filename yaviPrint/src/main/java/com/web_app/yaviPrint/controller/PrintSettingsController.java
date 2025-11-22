package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.dto.PrintSettingsDTO;
import com.web_app.yaviPrint.service.PrintSettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/print-settings")
@RequiredArgsConstructor
public class PrintSettingsController {

    private final PrintSettingsService printSettingsService;

    @PostMapping
    public ResponseEntity<?> createPrintSettings(@Valid @RequestBody PrintSettingsDTO settingsDTO) {
        try {
            if (!printSettingsService.validateSettings(settingsDTO)) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "Invalid print settings"
                ));
            }

            var settings = printSettingsService.createPrintSettings(settingsDTO);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Print settings created successfully",
                    "data", settings
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/{settingsId}")
    public ResponseEntity<?> getPrintSettings(@PathVariable Long settingsId) {
        try {
            var settings = printSettingsService.getPrintSettingsById(settingsId);
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

    @PutMapping("/{settingsId}")
    public ResponseEntity<?> updatePrintSettings(
            @PathVariable Long settingsId,
            @Valid @RequestBody PrintSettingsDTO settingsDTO) {
        try {
            if (!printSettingsService.validateSettings(settingsDTO)) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "Invalid print settings"
                ));
            }

            var settings = printSettingsService.updatePrintSettings(settingsId, settingsDTO);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Print settings updated successfully",
                    "data", settings
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{settingsId}")
    public ResponseEntity<?> deletePrintSettings(@PathVariable Long settingsId) {
        try {
            printSettingsService.deletePrintSettings(settingsId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Print settings deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/default")
    public ResponseEntity<?> getDefaultSettings() {
        try {
            var defaultSettings = printSettingsService.getDefaultSettings();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", defaultSettings
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validatePrintSettings(@Valid @RequestBody PrintSettingsDTO settingsDTO) {
        try {
            boolean isValid = printSettingsService.validateSettings(settingsDTO);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of("valid", isValid)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/calculate-ink")
    public ResponseEntity<?> calculateInkUsage(
            @RequestBody PrintSettingsDTO settingsDTO,
            @RequestParam int totalPages) {
        try {
            double inkUsage = printSettingsService.calculateInkUsage(settingsDTO, totalPages);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of("inkUsageMl", inkUsage)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}