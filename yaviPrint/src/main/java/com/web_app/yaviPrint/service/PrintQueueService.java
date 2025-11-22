package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.PrintRequestResponseDTO;
import com.web_app.yaviPrint.entity.Order;
import com.web_app.yaviPrint.entity.Shop;
import com.web_app.yaviPrint.repository.OrderRepository;
import com.web_app.yaviPrint.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrintQueueService {

    private final OrderRepository orderRepository;
    private final ShopRepository shopRepository;
    private final PrintJobStatusService printJobStatusService;

    public List<PrintRequestResponseDTO> getShopPrintQueue(Long shopId) {
        // Get all pending, accepted, and printing orders for the shop
        List<Order> queueOrders = orderRepository.findByShopId(shopId).stream()
                .filter(order -> order.getStatus() == Order.OrderStatus.PENDING ||
                        order.getStatus() == Order.OrderStatus.ACCEPTED ||
                        order.getStatus() == Order.OrderStatus.PRINTING)
                .sorted(Comparator.comparing(Order::getCreatedAt))
                .collect(Collectors.toList());

        return queueOrders.stream()
                .map(this::mapToPrintRequestResponseDTO)
                .collect(Collectors.toList());
    }

    public List<PrintRequestResponseDTO> getPriorityPrintQueue(Long shopId) {
        List<PrintRequestResponseDTO> queue = getShopPrintQueue(shopId);

        // Prioritize orders that have been waiting longer
        return queue.stream()
                .sorted(Comparator.comparing(PrintRequestResponseDTO::getCreatedAt))
                .collect(Collectors.toList());
    }

    @Transactional
    public PrintRequestResponseDTO moveToNextInQueue(Long shopId) {
        List<Order> queue = orderRepository.findByShopIdAndStatus(shopId, Order.OrderStatus.PENDING);

        if (queue.isEmpty()) {
            throw new RuntimeException("No pending orders in queue");
        }

        // Get the oldest pending order
        Order nextOrder = queue.stream()
                .min(Comparator.comparing(Order::getCreatedAt))
                .orElseThrow(() -> new RuntimeException("No orders to process"));

        // Update status to ACCEPTED
        nextOrder.setStatus(Order.OrderStatus.ACCEPTED);
        Order updatedOrder = orderRepository.save(nextOrder);

        // Record status change
        printJobStatusService.recordPrintJobProgress(updatedOrder.getId(), "ACCEPTED", 10);

        return mapToPrintRequestResponseDTO(updatedOrder);
    }

    @Transactional
    public PrintRequestResponseDTO startPrintingOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (order.getStatus() != Order.OrderStatus.ACCEPTED) {
            throw new RuntimeException("Order must be accepted before printing");
        }

        order.setStatus(Order.OrderStatus.PRINTING);
        order.setPrintedAt(LocalDateTime.now());
        Order updatedOrder = orderRepository.save(order);

        // Record status change
        printJobStatusService.recordPrintJobProgress(updatedOrder.getId(), "PRINTING", 50);

        return mapToPrintRequestResponseDTO(updatedOrder);
    }

    @Transactional
    public PrintRequestResponseDTO markOrderAsReady(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (order.getStatus() != Order.OrderStatus.PRINTING) {
            throw new RuntimeException("Order must be printing before marking as ready");
        }

        order.setStatus(Order.OrderStatus.READY);
        Order updatedOrder = orderRepository.save(order);

        // Record status change
        printJobStatusService.recordPrintJobProgress(updatedOrder.getId(), "READY", 100);

        return mapToPrintRequestResponseDTO(updatedOrder);
    }

    @Transactional
    public void reassignOrderToShop(Long orderId, Long newShopId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        Shop newShop = shopRepository.findById(newShopId)
                .orElseThrow(() -> new RuntimeException("Shop not found with id: " + newShopId));

        // Only allow reassignment if order is still pending
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("Cannot reassign order that is already being processed");
        }

        order.setShop(newShop);
        orderRepository.save(order);

        // Notify both shops about the reassignment
        notifyShopReassignment(order, newShop);
    }

    @Transactional
    public void cancelOrderFromQueue(Long orderId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        // Only allow cancellation if order is not completed
        if (order.getStatus() == Order.OrderStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel completed order");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());
        orderRepository.save(order);

        // Record cancellation
        printJobStatusService.recordPrintJobFailure(orderId, "Order cancelled: " + reason);
    }

    public int getQueueSize(Long shopId) {
        return orderRepository.findByShopIdAndStatus(shopId, Order.OrderStatus.PENDING).size() +
                orderRepository.findByShopIdAndStatus(shopId, Order.OrderStatus.ACCEPTED).size() +
                orderRepository.findByShopIdAndStatus(shopId, Order.OrderStatus.PRINTING).size();
    }

    public String getQueueStatus(Long shopId) {
        int pending = orderRepository.findByShopIdAndStatus(shopId, Order.OrderStatus.PENDING).size();
        int accepted = orderRepository.findByShopIdAndStatus(shopId, Order.OrderStatus.ACCEPTED).size();
        int printing = orderRepository.findByShopIdAndStatus(shopId, Order.OrderStatus.PRINTING).size();

        if (printing > 0) {
            return "PRINTING";
        } else if (accepted > 0) {
            return "PROCESSING";
        } else if (pending > 0) {
            return "PENDING";
        } else {
            return "IDLE";
        }
    }

    public LocalDateTime getEstimatedCompletionTime(Long shopId) {
        List<Order> queue = getShopPrintQueue(shopId).stream()
                .map(dto -> orderRepository.findById(dto.getId()).orElse(null))
                .filter(order -> order != null)
                .collect(Collectors.toList());

        if (queue.isEmpty()) {
            return LocalDateTime.now();
        }

        // Simple estimation: 5 minutes per order
        int totalMinutes = queue.size() * 5;
        return LocalDateTime.now().plusMinutes(totalMinutes);
    }

    private void notifyShopReassignment(Order order, Shop newShop) {
        // In production, this would send notifications to both shops
        String message = String.format(
                "Order %s has been reassigned from shop %s to shop %s",
                order.getTokenId(), order.getShop().getShopName(), newShop.getShopName()
        );
        // Implementation would send notifications
    }

    private PrintRequestResponseDTO mapToPrintRequestResponseDTO(Order order) {
        PrintRequestResponseDTO dto = new PrintRequestResponseDTO();
        dto.setId(order.getId());
        dto.setTokenId(order.getTokenId());
        dto.setStatus(order.getStatus().name());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setPrintedAt(order.getPrintedAt());
        // Additional mapping would be needed for full DTO
        return dto;
    }
}