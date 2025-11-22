package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.PaymentDTO;
import com.web_app.yaviPrint.entity.Order;
import com.web_app.yaviPrint.entity.Payment;
import com.web_app.yaviPrint.repository.OrderRepository;
import com.web_app.yaviPrint.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final RazorpayService razorpayService;

    @Transactional
    public PaymentDTO initiatePayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        // Create Razorpay order
        String razorpayOrderId = razorpayService.createOrder(
                order.getTotalAmount(),
                "INR",
                "Order for " + order.getTokenId()
        );

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setRazorpayOrderId(razorpayOrderId); // UPDATED: Use new field name
        payment.setAmount(order.getTotalAmount());
        payment.setCurrency("INR");
        payment.setStatus("CREATED");
        payment.setPaymentDate(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);

        // Update order with payment reference
        order.setPaymentId(razorpayOrderId);
        orderRepository.save(order);

        return mapToPaymentDTO(savedPayment);
    }

    @Transactional
    public PaymentDTO capturePayment(String razorpayPaymentId, String razorpayOrderId, String razorpaySignature) {
        Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId) // UPDATED: Use new field name
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + razorpayOrderId));

        // Verify payment signature
        boolean isValidSignature = razorpayService.verifyPaymentSignature(
                razorpayOrderId, razorpayPaymentId, razorpaySignature
        );

        if (!isValidSignature) {
            throw new RuntimeException("Invalid payment signature");
        }

        // Capture payment
        razorpayService.capturePayment(razorpayPaymentId, payment.getAmount());

        payment.setPaymentId(razorpayPaymentId);
        payment.setStatus("CAPTURED");
        payment.setPaymentDate(LocalDateTime.now());

        Payment updatedPayment = paymentRepository.save(payment);

        // Update order payment status
        Order order = payment.getOrder();
        order.setPaymentStatus(true);
        orderRepository.save(order);

        return mapToPaymentDTO(updatedPayment);
    }

    public PaymentDTO getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order id: " + orderId));
        return mapToPaymentDTO(payment);
    }

    public PaymentDTO getPaymentByRazorpayOrderId(String razorpayOrderId) {
        Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for Razorpay order: " + razorpayOrderId));
        return mapToPaymentDTO(payment);
    }
    private PaymentDTO mapToPaymentDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setOrderId(payment.getOrder().getId());
        dto.setPaymentId(payment.getPaymentId());
        dto.setOrderIdRazorpay(payment.getRazorpayOrderId()); // UPDATED: Use new field name
        dto.setAmount(payment.getAmount());
        dto.setCurrency(payment.getCurrency());
        dto.setStatus(payment.getStatus());
        dto.setMethod(payment.getMethod());
        dto.setDescription(payment.getDescription());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setReceiptNumber(payment.getReceiptNumber());
        return dto;
    }
}