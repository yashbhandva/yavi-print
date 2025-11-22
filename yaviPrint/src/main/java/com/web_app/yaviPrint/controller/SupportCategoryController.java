package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.dto.SupportCategoryDTO;
import com.web_app.yaviPrint.service.SupportCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/support-categories")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class SupportCategoryController {

    private final SupportCategoryService supportCategoryService;

    @PostMapping
    public ResponseEntity<?> createCategory(@Valid @RequestBody SupportCategoryDTO categoryDTO) {
        try {
            var category = supportCategoryService.createCategory(categoryDTO);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Support category created successfully",
                    "data", category
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        try {
            var categories = supportCategoryService.getActiveCategories();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", categories
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long categoryId) {
        try {
            var category = supportCategoryService.getCategoryById(categoryId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", category
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<?> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody SupportCategoryDTO categoryDTO) {
        try {
            var category = supportCategoryService.updateCategory(categoryId, categoryDTO);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Support category updated successfully",
                    "data", category
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long categoryId) {
        try {
            supportCategoryService.deleteCategory(categoryId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Support category deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/{categoryId}/deactivate")
    public ResponseEntity<?> deactivateCategory(@PathVariable Long categoryId) {
        try {
            supportCategoryService.deactivateCategory(categoryId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Support category deactivated successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/initialize-defaults")
    public ResponseEntity<?> initializeDefaultCategories() {
        try {
            supportCategoryService.initializeDefaultCategories();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Default support categories initialized successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}