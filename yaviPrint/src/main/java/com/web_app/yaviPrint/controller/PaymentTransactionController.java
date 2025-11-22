package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.dto.PaymentTransactionDTO;
import com.web_app.yaviPrint.service.PaymentTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments/{paymentId}/transactions")
@RequiredArgsConstructor
public class PaymentTransactionController {

    private final PaymentTransactionService paymentTransactionService;

    @PostMapping
    public ResponseEntity<?> recordTransaction(
            @PathVariable Long paymentId,
            @RequestBody PaymentTransactionDTO transactionDTO) {
        try {
            var transaction = paymentTransactionService.recordTransaction(paymentId, transactionDTO);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Transaction recorded successfully",
                    "data", transaction
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<?> getPaymentTransactions(@PathVariable Long paymentId) {
        try {
            var transactions = paymentTransactionService.getPaymentTransactions(paymentId);
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

    @PostMapping("/refund")
    public ResponseEntity<?> recordRefundTransaction(
            @PathVariable Long paymentId,
            @RequestParam Double refundAmount,
            @RequestParam String reason) {
        try {
            var refundTransaction = paymentTransactionService.recordRefundTransaction(paymentId, refundAmount, reason);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Refund transaction recorded successfully",
                    "data", refundTransaction
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}