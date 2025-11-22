package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.service.WalletTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletTransactionService walletTransactionService;

    @GetMapping("/balance")
    public ResponseEntity<?> getWalletBalance(Authentication authentication) {
        try {
            Long userId = 1L; // Placeholder - get from authenticated user
            Double balance = walletTransactionService.getCurrentBalance(userId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of("balance", balance)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getWalletTransactions(Authentication authentication) {
        try {
            Long userId = 1L; // Placeholder - get from authenticated user
            var transactions = walletTransactionService.getUserWalletTransactions(userId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", transactions
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/add-credit")
    public ResponseEntity<?> addWalletCredit(
            Authentication authentication,
            @RequestParam Double amount,
            @RequestParam String description) {
        try {
            Long userId = 1L; // Placeholder - get from authenticated user
            var transaction = walletTransactionService.addCredit(userId, amount, description, "MANUAL", null);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Credit added to wallet successfully",
                    "data", transaction
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}