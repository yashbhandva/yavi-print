package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.RefundRequestDTO;
import com.web_app.yaviPrint.entity.Order;
import com.web_app.yaviPrint.entity.RefundRequest;
import com.web_app.yaviPrint.repository.OrderRepository;
import com.web_app.yaviPrint.repository.RefundRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RefundRequestService {

    private final RefundRequestRepository refundRequestRepository;
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final PaymentTransactionService paymentTransactionService;

    @Transactional
    public RefundRequestDTO createRefundRequest(Long orderId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        // Check if order is eligible for refund
        if (!order.canBeCancelled()) {
            throw new RuntimeException("Order cannot be refunded in current status");
        }

        // Check if refund already exists for this order
        if (!refundRequestRepository.findByOrderId(orderId).isEmpty()) {
            throw new RuntimeException("Refund request already exists for this order");
        }

        RefundRequest refundRequest = new RefundRequest();
        refundRequest.setOrder(order);
        refundRequest.setReason(reason);
        refundRequest.setRefundAmount(order.getTotalAmount());
        refundRequest.setStatus("PENDING");
        refundRequest.setRequestedAt(LocalDateTime.now());

        RefundRequest savedRequest = refundRequestRepository.save(refundRequest);
        return mapToRefundRequestDTO(savedRequest);
    }

    @Transactional
    public RefundRequestDTO processRefundRequest(Long refundRequestId, String processedBy, boolean approve, String notes) {
        RefundRequest refundRequest = refundRequestRepository.findById(refundRequestId)
                .orElseThrow(() -> new RuntimeException("Refund request not found with id: " + refundRequestId));

        if (!"PENDING".equals(refundRequest.getStatus())) {
            throw new RuntimeException("Refund request already processed");
        }

        if (approve) {
            // Process refund through payment gateway
            try {
                // Record refund transaction
                paymentTransactionService.recordRefundTransaction(
                        refundRequest.getOrder().getId(),
                        refundRequest.getRefundAmount(),
                        refundRequest.getReason()
                );

                refundRequest.setStatus("APPROVED");
                refundRequest.setProcessedBy(processedBy);
                refundRequest.setProcessedAt(LocalDateTime.now());

                // Cancel the order
                Order order = refundRequest.getOrder();
                order.setStatus(Order.OrderStatus.CANCELLED);
                order.setCancelledAt(LocalDateTime.now());
                orderRepository.save(order);

            } catch (Exception e) {
                refundRequest.setStatus("FAILED");
                refundRequest.setRejectionReason("Refund processing failed: " + e.getMessage());
            }
        } else {
            refundRequest.setStatus("REJECTED");
            refundRequest.setRejectionReason(notes);
            refundRequest.setProcessedBy(processedBy);
            refundRequest.setProcessedAt(LocalDateTime.now());
        }

        RefundRequest updatedRequest = refundRequestRepository.save(refundRequest);
        return mapToRefundRequestDTO(updatedRequest);
    }

    public List<RefundRequestDTO> getPendingRefundRequests() {
        return refundRequestRepository.findPendingRefunds().stream()
                .map(this::mapToRefundRequestDTO)
                .collect(Collectors.toList());
    }

    public List<RefundRequestDTO> getRefundRequestsByOrderId(Long orderId) {
        return refundRequestRepository.findByOrderId(orderId).stream()
                .map(this::mapToRefundRequestDTO)
                .collect(Collectors.toList());
    }

    public List<RefundRequestDTO> getRefundRequestsByStatus(String status) {
        return refundRequestRepository.findByStatus(status).stream()
                .map(this::mapToRefundRequestDTO)
                .collect(Collectors.toList());
    }

    private RefundRequestDTO mapToRefundRequestDTO(RefundRequest refundRequest) {
        RefundRequestDTO dto = new RefundRequestDTO();
        dto.setId(refundRequest.getId());
        dto.setOrderId(refundRequest.getOrder().getId());
        dto.setReason(refundRequest.getReason());
        dto.setRefundAmount(refundRequest.getRefundAmount());
        dto.setStatus(refundRequest.getStatus());
        dto.setProcessedBy(refundRequest.getProcessedBy());
        dto.setRequestedAt(refundRequest.getRequestedAt());
        dto.setProcessedAt(refundRequest.getProcessedAt());
        dto.setRejectionReason(refundRequest.getRejectionReason());
        return dto;
    }
}