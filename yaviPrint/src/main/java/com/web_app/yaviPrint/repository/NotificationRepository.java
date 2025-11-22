package com.web_app.yaviPrint.repository;

import com.web_app.yaviPrint.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderBySentAtDesc(Long userId);
    List<Notification> findByUserIdAndIsReadFalse(Long userId);

    // ADD MISSING METHOD
    long countByUserIdAndIsReadFalse(Long userId);

    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.sentAt >= :sinceDate ORDER BY n.sentAt DESC")
    List<Notification> findRecentNotificationsByUserId(Long userId, LocalDateTime sinceDate);

    @Query("SELECT n FROM Notification n WHERE n.sentAt < :expiryTime AND n.isRead = false")
    List<Notification> findExpiredUnreadNotifications(LocalDateTime expiryTime);

    List<Notification> findByType(String type);
}