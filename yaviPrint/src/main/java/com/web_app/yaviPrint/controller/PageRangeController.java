package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.dto.PageRangeSelectionDTO;
import com.web_app.yaviPrint.service.PageRangeSelectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders/{orderId}/page-ranges")
@RequiredArgsConstructor
public class PageRangeController {

    private final PageRangeSelectionService pageRangeSelectionService;

    @PostMapping
    public ResponseEntity<?> createPageRange(
            @PathVariable Long orderId,
            @Valid @RequestBody PageRangeSelectionDTO rangeDTO) {
        try {
            var pageRange = pageRangeSelectionService.createPageRange(orderId, rangeDTO);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Page range added successfully",
                    "data", pageRange
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<?> getOrderPageRanges(@PathVariable Long orderId) {
        try {
            var pageRanges = pageRangeSelectionService.getOrderPageRanges(orderId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", pageRanges
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PutMapping
    public ResponseEntity<?> updateOrderPageRanges(
            @PathVariable Long orderId,
            @Valid @RequestBody List<PageRangeSelectionDTO> rangeDTOs) {
        try {
            pageRangeSelectionService.updateOrderPageRanges(orderId, rangeDTOs);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Page ranges updated successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{rangeId}")
    public ResponseEntity<?> deletePageRange(@PathVariable Long rangeId) {
        try {
            pageRangeSelectionService.deletePageRange(rangeId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Page range deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/total-pages")
    public ResponseEntity<?> calculateTotalPages(@PathVariable Long orderId) {
        try {
            int totalPages = pageRangeSelectionService.calculateTotalPagesFromRanges(orderId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of("totalPages", totalPages)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/print-instructions")
    public ResponseEntity<?> getPrintInstructions(@PathVariable Long orderId) {
        try {
            String instructions = pageRangeSelectionService.generatePrintInstructions(orderId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of("instructions", instructions)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validatePageRanges(
            @PathVariable Long orderId,
            @RequestParam int totalDocumentPages) {
        try {
            boolean isValid = pageRangeSelectionService.validatePageRanges(orderId, totalDocumentPages);
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
}