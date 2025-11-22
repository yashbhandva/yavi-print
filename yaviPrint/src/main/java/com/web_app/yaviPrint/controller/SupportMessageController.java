package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.dto.SupportMessageDTO;
import com.web_app.yaviPrint.service.SupportMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/support/tickets/{ticketId}/messages")
@RequiredArgsConstructor
public class SupportMessageController {

    private final SupportMessageService supportMessageService;

    @PostMapping
    public ResponseEntity<?> createMessage(
            Authentication authentication,
            @PathVariable Long ticketId,
            @RequestBody SupportMessageDTO messageDTO) {
        try {
            Long userId = 1L; // Placeholder - get from authenticated user
            boolean isFromAdmin = false; // Determine if user is admin

            var message = supportMessageService.createMessage(ticketId, userId, messageDTO, isFromAdmin);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Message sent successfully",
                    "data", message
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<?> getTicketMessages(@PathVariable Long ticketId) {
        try {
            var messages = supportMessageService.getTicketMessages(ticketId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", messages
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/{messageId}/attachment")
    public ResponseEntity<?> addAttachmentToMessage(
            @PathVariable Long messageId,
            @RequestParam String attachmentUrl) {
        try {
            var message = supportMessageService.addAttachmentToMessage(messageId, attachmentUrl);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Attachment added successfully",
                    "data", message
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/mark-read")
    public ResponseEntity<?> markMessagesAsRead(
            Authentication authentication,
            @PathVariable Long ticketId) {
        try {
            boolean isFromAdmin = false; // Determine if user is admin
            supportMessageService.markMessagesAsRead(ticketId, isFromAdmin);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Messages marked as read"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadMessageCount(
            Authentication authentication,
            @PathVariable Long ticketId) {
        try {
            boolean isFromAdmin = false; // Determine if user is admin
            long count = supportMessageService.getUnreadMessageCount(ticketId, isFromAdmin);
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