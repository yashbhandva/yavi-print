package com.web_app.yaviPrint.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RazorpayService {

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    private RazorpayClient getRazorpayClient() throws RazorpayException {
        return new RazorpayClient(razorpayKeyId, razorpayKeySecret);
    }

    public String createOrder(Double amount, String currency, String notes) {
        try {
            RazorpayClient razorpay = getRazorpayClient();

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount * 100); // Convert to paise
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", "receipt_" + System.currentTimeMillis());
            orderRequest.put("notes", new JSONObject().put("notes", notes));

            Order order = razorpay.orders.create(orderRequest);
            return order.get("id");

        } catch (RazorpayException e) {
            throw new RuntimeException("Error creating Razorpay order", e);
        }
    }

    public boolean verifyPaymentSignature(String orderId, String paymentId, String signature) {
        try {
            String payload = orderId + "|" + paymentId;
            // In production, use Razorpay's signature verification utility
            // This is a simplified version
            return true; // Placeholder - implement proper signature verification
        } catch (Exception e) {
            throw new RuntimeException("Error verifying payment signature", e);
        }
    }

    public void capturePayment(String paymentId, Double amount) {
        try {
            RazorpayClient razorpay = getRazorpayClient();

            JSONObject captureRequest = new JSONObject();
            captureRequest.put("amount", amount * 100);
            captureRequest.put("currency", "INR");

            // razorpay.payments.capture(paymentId, captureRequest);
            // Implementation depends on Razorpay Java SDK version

        } catch (Exception e) {
            throw new RuntimeException("Error capturing payment", e);
        }
    }
}