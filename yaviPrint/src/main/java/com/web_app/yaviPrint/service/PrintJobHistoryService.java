package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.PrintJobHistoryDTO;
import com.web_app.yaviPrint.entity.Order;
import com.web_app.yaviPrint.entity.PrintJobHistory;
import com.web_app.yaviPrint.repository.OrderRepository;
import com.web_app.yaviPrint.repository.PrintJobHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrintJobHistoryService {

    private final PrintJobHistoryRepository printJobHistoryRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public PrintJobHistoryDTO recordHistoryEntry(Long orderId, String action, String description,
                                                 String performedBy, String oldValue, String newValue) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        PrintJobHistory history = new PrintJobHistory();
        history.setOrder(order);
        history.setAction(action);
        history.setDescription(description);
        history.setPerformedBy(performedBy);
        history.setActionTime(LocalDateTime.now());
        history.setOldValue(oldValue);
        history.setNewValue(newValue);

        PrintJobHistory savedHistory = printJobHistoryRepository.save(history);
        return mapToPrintJobHistoryDTO(savedHistory);
    }

    public List<PrintJobHistoryDTO> getOrderHistory(Long orderId) {
        return printJobHistoryRepository.findByOrderIdOrderByActionTimeDesc(orderId).stream()
                .map(this::mapToPrintJobHistoryDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void recordStatusChange(Long orderId, String oldStatus, String newStatus, String performedBy) {
        recordHistoryEntry(
                orderId,
                "STATUS_CHANGE",
                String.format("Order status changed from %s to %s", oldStatus, newStatus),
                performedBy,
                oldStatus,
                newStatus
        );
    }

    @Transactional
    public void recordPrintSettingsChange(Long orderId, String oldSettings, String newSettings, String performedBy) {
        recordHistoryEntry(
                orderId,
                "SETTINGS_CHANGE",
                "Print settings updated",
                performedBy,
                oldSettings,
                newSettings
        );
    }

    @Transactional
    public void recordPaymentUpdate(Long orderId, boolean oldPaymentStatus, boolean newPaymentStatus, String performedBy) {
        recordHistoryEntry(
                orderId,
                "PAYMENT_UPDATE",
                String.format("Payment status changed from %s to %s", oldPaymentStatus, newPaymentStatus),
                performedBy,
                String.valueOf(oldPaymentStatus),
                String.valueOf(newPaymentStatus)
        );
    }

    @Transactional
    public void recordShopAssignment(Long orderId, Long oldShopId, Long newShopId, String performedBy) {
        recordHistoryEntry(
                orderId,
                "SHOP_ASSIGNMENT",
                "Order assigned to different shop",
                performedBy,
                String.valueOf(oldShopId),
                String.valueOf(newShopId)
        );
    }

    @Transactional
    public void recordPickupCodeGeneration(Long orderId, String tokenId, String performedBy) {
        recordHistoryEntry(
                orderId,
                "PICKUP_CODE_GENERATED",
                String.format("Pickup code generated: %s", tokenId),
                performedBy,
                null,
                tokenId
        );
    }

    @Transactional
    public void recordPickupCompletion(Long orderId, String tokenId, String performedBy) {
        recordHistoryEntry(
                orderId,
                "PICKUP_COMPLETED",
                String.format("Order picked up using token: %s", tokenId),
                performedBy,
                tokenId,
                "COMPLETED"
        );
    }

    @Transactional
    public void recordCancellation(Long orderId, String reason, String performedBy) {
        recordHistoryEntry(
                orderId,
                "ORDER_CANCELLED",
                String.format("Order cancelled. Reason: %s", reason),
                performedBy,
                "ACTIVE",
                "CANCELLED"
        );
    }

    public List<PrintJobHistoryDTO> getRecentActivity(Long orderId, int limit) {
        return printJobHistoryRepository.findByOrderIdOrderByActionTimeDesc(orderId).stream()
                .limit(limit)
                .map(this::mapToPrintJobHistoryDTO)
                .collect(Collectors.toList());
    }

    public String getOrderTimeline(Long orderId) {
        List<PrintJobHistoryDTO> history = getOrderHistory(orderId);
        StringBuilder timeline = new StringBuilder();

        for (PrintJobHistoryDTO entry : history) {
            timeline.append(String.format("[%s] %s: %s\n",
                    entry.getActionTime(),
                    entry.getAction(),
                    entry.getDescription()));
        }

        return timeline.toString();
    }

    private PrintJobHistoryDTO mapToPrintJobHistoryDTO(PrintJobHistory history) {
        PrintJobHistoryDTO dto = new PrintJobHistoryDTO();
        dto.setId(history.getId());
        dto.setOrderId(history.getOrder().getId());
        dto.setAction(history.getAction());
        dto.setDescription(history.getDescription());
        dto.setPerformedBy(history.getPerformedBy());
        dto.setActionTime(history.getActionTime());
        dto.setOldValue(history.getOldValue());
        dto.setNewValue(history.getNewValue());
        return dto;
    }
}