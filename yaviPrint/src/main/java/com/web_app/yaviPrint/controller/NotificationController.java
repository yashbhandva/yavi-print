package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<?> getUserNotifications(Authentication authentication) {
        try {
            Long userId = 1L; // Placeholder - get from authenticated user
            var notifications = notificationService.getUserNotifications(userId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", notifications
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/unread")
    public ResponseEntity<?> getUnreadNotifications(Authentication authentication) {
        try {
            Long userId = 1L; // Placeholder - get from authenticated user
            var notifications = notificationService.getUnreadNotifications(userId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", notifications
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/{notificationId}/read")
    public ResponseEntity<?> markNotificationAsRead(
            Authentication authentication,
            @PathVariable Long notificationId) {
        try {
            Long userId = 1L; // Placeholder - get from authenticated user
            notificationService.markNotificationAsRead(notificationId, userId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Notification marked as read"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/mark-all-read")
    public ResponseEntity<?> markAllNotificationsAsRead(Authentication authentication) {
        try {
            Long userId = 1L; // Placeholder - get from authenticated user
            notificationService.markAllNotificationsAsRead(userId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "All notifications marked as read"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount(Authentication authentication) {
        try {
            Long userId = 1L; // Placeholder - get from authenticated user
            long count = notificationService.getUnreadNotifications(userId).size();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of("unreadCount", count)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}