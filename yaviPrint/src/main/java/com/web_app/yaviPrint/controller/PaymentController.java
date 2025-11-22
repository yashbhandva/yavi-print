package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate/{orderId}")
    public ResponseEntity<?> initiatePayment(@PathVariable Long orderId) {
        try {
            var payment = paymentService.initiatePayment(orderId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Payment initiated successfully",
                    "data", payment
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/capture")
    public ResponseEntity<?> capturePayment(
            @RequestParam String razorpay_payment_id,
            @RequestParam String razorpay_order_id,
            @RequestParam String razorpay_signature) {
        try {
            var payment = paymentService.capturePayment(razorpay_payment_id, razorpay_order_id, razorpay_signature);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Payment captured successfully",
                    "data", payment
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getPaymentByOrderId(@PathVariable Long orderId) {
        try {
            var payment = paymentService.getPaymentByOrderId(orderId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", payment
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/razorpay/{razorpayOrderId}")
    public ResponseEntity<?> getPaymentByRazorpayOrderId(@PathVariable String razorpayOrderId) {
        try {
            var payment = paymentService.getPaymentByRazorpayOrderId(razorpayOrderId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", payment
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}