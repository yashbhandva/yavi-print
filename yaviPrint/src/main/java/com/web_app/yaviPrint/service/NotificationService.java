package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.NotificationDTO;
import com.web_app.yaviPrint.entity.Notification;
import com.web_app.yaviPrint.entity.User;
import com.web_app.yaviPrint.repository.NotificationRepository;
import com.web_app.yaviPrint.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Transactional
    public NotificationDTO createNotification(Long userId, NotificationDTO notificationDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(notificationDTO.getTitle());
        notification.setMessage(notificationDTO.getMessage());
        notification.setType(notificationDTO.getType());
        notification.setRead(false);
        notification.setSentAt(LocalDateTime.now());
        notification.setActionUrl(notificationDTO.getActionUrl());
        notification.setImageUrl(notificationDTO.getImageUrl());
        notification.setPriority(notificationDTO.getPriority() != null ? notificationDTO.getPriority() : 3);

        Notification savedNotification = notificationRepository.save(notification);

        // Send email notification if enabled
        if (shouldSendEmail(notificationDTO.getType())) {
            sendEmailNotification(user, notificationDTO);
        }

        return mapToNotificationDTO(savedNotification);
    }

    public List<NotificationDTO> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderBySentAtDesc(userId).stream()
                .map(this::mapToNotificationDTO)
                .collect(Collectors.toList());
    }

    public List<NotificationDTO> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalse(userId).stream()
                .map(this::mapToNotificationDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markNotificationAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + notificationId));

        if (!notification.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only mark your own notifications as read");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllNotificationsAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsReadFalse(userId);
        unreadNotifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    @Transactional
    public void sendOrderStatusNotification(Long userId, String orderToken, String status, String message) {
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setTitle("Order Status Update - " + orderToken);
        notificationDTO.setMessage(message);
        notificationDTO.setType("ORDER_UPDATE");
        notificationDTO.setActionUrl("/orders/" + orderToken);
        notificationDTO.setPriority(4);

        createNotification(userId, notificationDTO);
    }

    @Transactional
    public void sendPaymentNotification(Long userId, Double amount, String status, String orderToken) {
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setTitle("Payment " + status);
        notificationDTO.setMessage("Payment of ₹" + amount + " for order " + orderToken + " has been " + status.toLowerCase());
        notificationDTO.setType("PAYMENT");
        notificationDTO.setActionUrl("/orders/" + orderToken);
        notificationDTO.setPriority(4);

        createNotification(userId, notificationDTO);
    }

    @Transactional
    public void cleanupOldNotifications() {
        LocalDateTime expiryTime = LocalDateTime.now().minusDays(30); // Keep notifications for 30 days
        List<Notification> oldNotifications = notificationRepository.findExpiredUnreadNotifications(expiryTime);
        notificationRepository.deleteAll(oldNotifications);
    }

    private boolean shouldSendEmail(String notificationType) {
        return List.of("ORDER_UPDATE", "PAYMENT", "SECURITY").contains(notificationType);
    }

    private void sendEmailNotification(User user, NotificationDTO notificationDTO) {
        try {
            // Simple email sending - in production use templates
            String subject = notificationDTO.getTitle();
            String body = "Hello " + user.getName() + ",\n\n" + notificationDTO.getMessage();

            // emailService.sendSimpleEmail(user.getEmail(), subject, body);
        } catch (Exception e) {
            // Log error but don't fail the notification creation
            System.err.println("Failed to send email notification: " + e.getMessage());
        }
    }

    private NotificationDTO mapToNotificationDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setUserId(notification.getUser().getId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType());
        dto.setRead(notification.isRead());
        dto.setSentAt(notification.getSentAt());
        dto.setActionUrl(notification.getActionUrl());
        dto.setImageUrl(notification.getImageUrl());
        dto.setPriority(notification.getPriority());
        return dto;
    }
}