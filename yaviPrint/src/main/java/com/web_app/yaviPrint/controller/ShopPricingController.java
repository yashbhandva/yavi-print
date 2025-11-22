package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.dto.ShopPricingDTO;
import com.web_app.yaviPrint.service.ShopPricingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/shops/{shopId}/pricing")
@RequiredArgsConstructor
public class ShopPricingController {

    private final ShopPricingService shopPricingService;

    @PostMapping
    public ResponseEntity<?> createOrUpdatePricing(
            @PathVariable Long shopId,
            @Valid @RequestBody ShopPricingDTO pricingDTO) {
        try {
            var pricing = shopPricingService.createOrUpdatePricing(shopId, pricingDTO);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Pricing updated successfully",
                    "data", pricing
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<?> getShopPricing(@PathVariable Long shopId) {
        try {
            var pricing = shopPricingService.getShopPricing(shopId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", pricing
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/calculate")
    public ResponseEntity<?> calculatePrintPrice(
            @PathVariable Long shopId,
            @RequestParam String paperType,
            @RequestParam String printType,
            @RequestParam int pages,
            @RequestParam int copies,
            @RequestParam(defaultValue = "false") boolean duplex) {
        try {
            var price = shopPricingService.calculatePrintPrice(shopId, paperType, printType, pages, copies, duplex);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of("calculatedPrice", price)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}