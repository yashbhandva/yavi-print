package com.web_app.yaviPrint.repository;

import com.web_app.yaviPrint.entity.Order;
import com.web_app.yaviPrint.entity.Order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByTokenId(String tokenId);
    List<Order> findByUserId(Long userId);
    List<Order> findByShopId(Long shopId);
    List<Order> findByShopIdAndStatus(Long shopId, OrderStatus status);
    Optional<Order> findByPaymentId(String paymentId);

    @Query("SELECT o FROM Order o WHERE o.shop.id = :shopId AND o.createdAt >= :startDate AND o.createdAt <= :endDate")
    List<Order> findByShopIdAndDateRange(Long shopId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.shop.id = :shopId AND o.status = :status")
    long countByShopIdAndStatus(Long shopId, OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.status = 'READY' AND o.collectedAt IS NULL AND o.createdAt < :expiryTime")
    List<Order> findExpiredReadyOrders(LocalDateTime expiryTime);
}