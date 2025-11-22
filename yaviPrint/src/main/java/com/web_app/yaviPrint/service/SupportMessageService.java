package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.SupportMessageDTO;
import com.web_app.yaviPrint.entity.SupportMessage;
import com.web_app.yaviPrint.entity.SupportTicket;
import com.web_app.yaviPrint.entity.User;
import com.web_app.yaviPrint.repository.SupportMessageRepository;
import com.web_app.yaviPrint.repository.SupportTicketRepository;
import com.web_app.yaviPrint.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupportMessageService {

    private final SupportMessageRepository supportMessageRepository;
    private final SupportTicketRepository supportTicketRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public SupportMessageDTO createMessage(Long ticketId, Long senderId, SupportMessageDTO messageDTO, boolean isFromAdmin) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Support ticket not found with id: " + ticketId));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + senderId));

        SupportMessage message = new SupportMessage();
        message.setTicket(ticket);
        message.setSender(sender);
        message.setFromAdmin(isFromAdmin);
        message.setMessage(messageDTO.getMessage());
        message.setAttachmentUrl(messageDTO.getAttachmentUrl());
        message.setSentAt(LocalDateTime.now());
        message.setRead(false);

        SupportMessage savedMessage = supportMessageRepository.save(message);

        // Update ticket updated timestamp
        ticket.setUpdatedAt(LocalDateTime.now());
        supportTicketRepository.save(ticket);

        // Notify the other party
        notifyOtherParty(ticket, sender, isFromAdmin, messageDTO.getMessage());

        return mapToSupportMessageDTO(savedMessage);
    }

    public List<SupportMessageDTO> getTicketMessages(Long ticketId) {
        return supportMessageRepository.findByTicketIdOrderBySentAtAsc(ticketId).stream()
                .map(this::mapToSupportMessageDTO)
                .collect(Collectors.toList());
    }

    public List<SupportMessageDTO> getUnreadMessages(Long ticketId, boolean isFromAdmin) {
        if (isFromAdmin) {
            return supportMessageRepository.findUnreadCustomerMessages(ticketId).stream()
                    .map(this::mapToSupportMessageDTO)
                    .collect(Collectors.toList());
        } else {
            return supportMessageRepository.findUnreadAdminMessages(ticketId).stream()
                    .map(this::mapToSupportMessageDTO)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    public void markMessagesAsRead(Long ticketId, boolean isFromAdmin) {
        List<SupportMessage> unreadMessages;

        if (isFromAdmin) {
            unreadMessages = supportMessageRepository.findUnreadCustomerMessages(ticketId);
        } else {
            unreadMessages = supportMessageRepository.findUnreadAdminMessages(ticketId);
        }

        unreadMessages.forEach(message -> {
            message.setRead(true);
            message.setReadAt(LocalDateTime.now());
        });

        supportMessageRepository.saveAll(unreadMessages);
    }

    @Transactional
    public SupportMessageDTO addAttachmentToMessage(Long messageId, String attachmentUrl) {
        SupportMessage message = supportMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Support message not found with id: " + messageId));

        message.setAttachmentUrl(attachmentUrl);
        SupportMessage updatedMessage = supportMessageRepository.save(message);
        return mapToSupportMessageDTO(updatedMessage);
    }

    public long getUnreadMessageCount(Long ticketId, boolean isFromAdmin) {
        return supportMessageRepository.countByTicketIdAndIsReadFalseAndIsFromAdmin(ticketId, isFromAdmin);
    }

    @Transactional
    public SupportMessageDTO createCustomerMessage(Long ticketId, Long customerId, String messageText, String attachmentUrl) {
        SupportMessageDTO messageDTO = new SupportMessageDTO();
        messageDTO.setMessage(messageText);
        messageDTO.setAttachmentUrl(attachmentUrl);

        return createMessage(ticketId, customerId, messageDTO, false);
    }

    @Transactional
    public SupportMessageDTO createAdminMessage(Long ticketId, Long adminId, String messageText, String attachmentUrl) {
        SupportMessageDTO messageDTO = new SupportMessageDTO();
        messageDTO.setMessage(messageText);
        messageDTO.setAttachmentUrl(attachmentUrl);

        return createMessage(ticketId, adminId, messageDTO, true);
    }

    @Transactional
    public void sendAutoResponse(Long ticketId, String messageText) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Support ticket not found with id: " + ticketId));

        // Get system admin user (in production, this would be a dedicated system user)
        User systemUser = userRepository.findByEmail("system@yaviprint.com")
                .orElseGet(() -> createSystemUser());

        SupportMessageDTO messageDTO = new SupportMessageDTO();
        messageDTO.setMessage(messageText);

        createMessage(ticketId, systemUser.getId(), messageDTO, true);
    }

    private void notifyOtherParty(SupportTicket ticket, User sender, boolean isFromAdmin, String message) {
        Long notifyUserId;
        String notificationTitle;

        if (isFromAdmin) {
            // Notify customer
            notifyUserId = ticket.getUser().getId();
            notificationTitle = "New Response on Support Ticket";
        } else {
            // Notify admins (in production, this would notify all available admins)
            // For now, we'll just log it
            notificationTitle = "New Customer Message on Support Ticket";
            return;
        }

        notificationService.createNotification(
                notifyUserId,
                createSupportMessageNotification(ticket, notificationTitle, message)
        );
    }

    private com.web_app.yaviPrint.dto.NotificationDTO createSupportMessageNotification(SupportTicket ticket, String title, String messagePreview) {
        com.web_app.yaviPrint.dto.NotificationDTO notification = new com.web_app.yaviPrint.dto.NotificationDTO();
        notification.setTitle(title);
        notification.setMessage(String.format(
                "Ticket %s: %s",
                ticket.getTicketNumber(),
                messagePreview.length() > 100 ? messagePreview.substring(0, 100) + "..." : messagePreview
        ));
        notification.setType("SUPPORT");
        notification.setActionUrl("/support/tickets/" + ticket.getTicketNumber());
        notification.setPriority(3);
        return notification;
    }

    private User createSystemUser() {
        User systemUser = new User();
        systemUser.setName("YaviPrint System");
        systemUser.setEmail("system@yaviprint.com");
        systemUser.setPassword("system"); // In production, use proper password
        systemUser.setRole(com.web_app.yaviPrint.entity.UserRole.ADMIN);
        systemUser.setEnabled(true);
        systemUser.setVerifiedAt(LocalDateTime.now());
        return userRepository.save(systemUser);
    }

    private SupportMessageDTO mapToSupportMessageDTO(SupportMessage message) {
        SupportMessageDTO dto = new SupportMessageDTO();
        dto.setId(message.getId());
        dto.setTicketId(message.getTicket().getId());
        dto.setSenderId(message.getSender().getId());
        dto.setFromAdmin(message.isFromAdmin());
        dto.setMessage(message.getMessage());
        dto.setAttachmentUrl(message.getAttachmentUrl());
        dto.setSentAt(message.getSentAt());
        dto.setRead(message.isRead());
        dto.setReadAt(message.getReadAt());
        return dto;
    }
}