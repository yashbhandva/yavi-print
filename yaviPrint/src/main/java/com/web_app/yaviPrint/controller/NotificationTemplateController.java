package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.dto.NotificationTemplateDTO;
import com.web_app.yaviPrint.service.NotificationTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/notification-templates")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class NotificationTemplateController {

    private final NotificationTemplateService notificationTemplateService;

    @PostMapping
    public ResponseEntity<?> createTemplate(@Valid @RequestBody NotificationTemplateDTO templateDTO) {
        try {
            var template = notificationTemplateService.createTemplate(templateDTO);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Notification template created successfully",
                    "data", template
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllTemplates() {
        try {
            var templates = notificationTemplateService.getActiveTemplates();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", templates
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/{templateId}")
    public ResponseEntity<?> getTemplateById(@PathVariable Long templateId) {
        try {
            var template = notificationTemplateService.getTemplateById(templateId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", template
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/name/{templateName}")
    public ResponseEntity<?> getTemplateByName(@PathVariable String templateName) {
        try {
            var template = notificationTemplateService.getTemplateByName(templateName);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", template
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PutMapping("/{templateId}")
    public ResponseEntity<?> updateTemplate(
            @PathVariable Long templateId,
            @Valid @RequestBody NotificationTemplateDTO templateDTO) {
        try {
            var template = notificationTemplateService.updateTemplate(templateId, templateDTO);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Notification template updated successfully",
                    "data", template
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{templateId}")
    public ResponseEntity<?> deleteTemplate(@PathVariable Long templateId) {
        try {
            notificationTemplateService.deleteTemplate(templateId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Notification template deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/{templateName}/render")
    public ResponseEntity<?> renderTemplate(
            @PathVariable String templateName,
            @RequestBody Map<String, Object> variables) {
        try {
            String renderedContent = notificationTemplateService.renderTemplate(templateName, variables);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of("renderedContent", renderedContent)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}