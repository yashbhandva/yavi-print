package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.entity.Order;
import com.web_app.yaviPrint.entity.Shop;
import com.web_app.yaviPrint.repository.OrderRepository;
import com.web_app.yaviPrint.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrintAutomationService {

    private final OrderRepository orderRepository;
    private final ShopRepository shopRepository;
    private final PrintQueueService printQueueService;
    private final PrintPickupCodeService printPickupCodeService;
    private final NotificationService notificationService;

    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    @Transactional
    public void processPendingOrders() {
        List<Shop> onlineShops = shopRepository.findByIsActiveTrueAndIsOnlineTrue();

        for (Shop shop : onlineShops) {
            processShopPendingOrders(shop);
        }
    }

    @Scheduled(fixedRate = 600000) // Run every 10 minutes
    @Transactional
    public void autoAcceptOrders() {
        List<Shop> onlineShops = shopRepository.findByIsActiveTrueAndIsOnlineTrue();

        for (Shop shop : onlineShops) {
            autoAcceptShopOrders(shop);
        }
    }

    @Scheduled(cron = "0 0 2 * * ?") // Run daily at 2 AM
    @Transactional
    public void cleanupExpiredOrders() {
        LocalDateTime expiryTime = LocalDateTime.now().minusHours(24);

        List<Order> expiredReadyOrders = orderRepository.findExpiredReadyOrders(expiryTime);

        for (Order order : expiredReadyOrders) {
            handleExpiredOrder(order);
        }
    }

    @Scheduled(cron = "0 0 3 * * ?") // Run daily at 3 AM
    @Transactional
    public void cleanupExpiredPickupCodes() {
        printPickupCodeService.cleanupExpiredPickupCodes();
    }

    private void processShopPendingOrders(Shop shop) {
        List<Order> pendingOrders = orderRepository.findByShopIdAndStatus(shop.getId(), Order.OrderStatus.PENDING);

        // Auto-accept orders if shop is not busy and has capacity
        if (!isShopBusy(shop) && hasCapacity(shop)) {
            for (Order order : pendingOrders) {
                if (shouldAutoAccept(order)) {
                    autoAcceptOrder(order);
                }
            }
        }
    }

    private void autoAcceptShopOrders(Shop shop) {
        List<Order> pendingOrders = orderRepository.findByShopIdAndStatus(shop.getId(), Order.OrderStatus.PENDING);

        // Auto-accept orders that have been pending for more than 30 minutes
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(30);

        for (Order order : pendingOrders) {
            if (order.getCreatedAt().isBefore(threshold)) {
                autoAcceptOrder(order);
            }
        }
    }

    private void autoAcceptOrder(Order order) {
        order.setStatus(Order.OrderStatus.ACCEPTED);
        orderRepository.save(order);

        // Notify customer
        notificationService.sendOrderStatusNotification(
                order.getUser().getId(),
                order.getTokenId(),
                "ACCEPTED",
                "Your order has been accepted and is being processed"
        );
    }

    private void handleExpiredOrder(Order order) {
        // Mark order as expired
        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());
        orderRepository.save(order);

        // Notify customer
        notificationService.sendOrderStatusNotification(
                order.getUser().getId(),
                order.getTokenId(),
                "EXPIRED",
                "Your order has been cancelled as it was not picked up within 24 hours"
        );

        // Process refund if payment was made
        if (order.isPaymentStatus()) {
            processAutomaticRefund(order);
        }
    }

    private void processAutomaticRefund(Order order) {
        // In production, this would integrate with payment gateway
        // For now, just log the refund action
        String refundMessage = String.format(
                "Automatic refund processed for order %s. Amount: ₹%.2f",
                order.getTokenId(), order.getTotalAmount()
        );
        System.out.println(refundMessage);

        // Record the refund in system
        // refundService.processRefund(order.getId(), "Order not picked up");
    }

    private boolean isShopBusy(Shop shop) {
        int queueSize = printQueueService.getQueueSize(shop.getId());
        return queueSize >= 10; // Consider shop busy if more than 10 orders in queue
    }

    private boolean hasCapacity(Shop shop) {
        // Check if shop has reached daily order limit
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        List<Order> todayOrders = orderRepository.findByShopIdAndDateRange(
                shop.getId(), startOfDay, endOfDay
        );

        return todayOrders.size() < shop.getMaxOrdersPerDay();
    }

    private boolean shouldAutoAccept(Order order) {
        // Auto-accept small orders (less than 10 pages) immediately
        if (order.getTotalPages() <= 10) {
            return true;
        }

        // Auto-accept orders from returning customers
        if (isReturningCustomer(order.getUser().getId(), order.getShop().getId())) {
            return true;
        }

        return false;
    }

    private boolean isReturningCustomer(Long userId, Long shopId) {
        // Check if user has previous completed orders at this shop
        List<Order> previousOrders = orderRepository.findByShopId(shopId).stream()
                .filter(order -> order.getUser().getId().equals(userId) &&
                        order.getStatus() == Order.OrderStatus.COMPLETED)
                .collect(Collectors.toList());

        return !previousOrders.isEmpty();
    }

    @Transactional
    public void reassignOverflowOrders() {
        List<Shop> overloadedShops = shopRepository.findAll().stream()
                .filter(shop -> printQueueService.getQueueSize(shop.getId()) > 15)
                .collect(Collectors.toList());

        List<Shop> availableShops = shopRepository.findByIsActiveTrueAndIsOnlineTrue().stream()
                .filter(shop -> printQueueService.getQueueSize(shop.getId()) < 5)
                .collect(Collectors.toList());

        for (Shop overloadedShop : overloadedShops) {
            reassignOverflowFromShop(overloadedShop, availableShops);
        }
    }

    private void reassignOverflowFromShop(Shop overloadedShop, List<Shop> availableShops) {
        if (availableShops.isEmpty()) return;

        List<Order> pendingOrders = orderRepository.findByShopIdAndStatus(
                overloadedShop.getId(), Order.OrderStatus.PENDING
        );

        // Reassign up to 5 oldest pending orders
        List<Order> ordersToReassign = pendingOrders.stream()
                .sorted((o1, o2) -> o1.getCreatedAt().compareTo(o2.getCreatedAt()))
                .limit(5)
                .collect(Collectors.toList());

        for (Order order : ordersToReassign) {
            Shop newShop = findBestShopForReassignment(order, availableShops);
            if (newShop != null) {
                printQueueService.reassignOrderToShop(order.getId(), newShop.getId());

                // Notify customer about shop change
                notificationService.sendOrderStatusNotification(
                        order.getUser().getId(),
                        order.getTokenId(),
                        "REASSIGNED",
                        String.format("Your order has been reassigned to %s for faster processing",
                                newShop.getShopName())
                );
            }
        }
    }

    private Shop findBestShopForReassignment(Order order, List<Shop> availableShops) {
        // Find the closest available shop
        return availableShops.stream()
                .min((s1, s2) -> compareShopSuitability(order, s1, s2))
                .orElse(null);
    }

    private int compareShopSuitability(Order order, Shop shop1, Shop shop2) {
        // Compare shops based on distance, rating, and current queue size
        double score1 = calculateShopScore(order, shop1);
        double score2 = calculateShopScore(order, shop2);

        return Double.compare(score2, score1); // Higher score is better
    }

    private double calculateShopScore(Order order, Shop shop) {
        double score = 0;

        // Prefer shops with higher ratings
        score += shop.getRating() * 10;

        // Prefer shops with smaller queues
        score -= printQueueService.getQueueSize(shop.getId()) * 2;

        // Prefer shops that are closer (simplified)
        // In production, would use actual distance calculation
        score += 50; // Base score

        return score;
    }
}