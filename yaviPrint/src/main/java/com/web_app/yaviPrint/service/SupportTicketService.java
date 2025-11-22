package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.SupportTicketDTO;
import com.web_app.yaviPrint.entity.SupportTicket;
import com.web_app.yaviPrint.entity.User;
import com.web_app.yaviPrint.repository.SupportTicketRepository;
import com.web_app.yaviPrint.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public SupportTicketDTO createSupportTicket(Long userId, SupportTicketDTO ticketDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        SupportTicket ticket = new SupportTicket();
        ticket.setUser(user);
        ticket.setTicketNumber(generateTicketNumber());
        ticket.setSubject(ticketDTO.getSubject());
        ticket.setDescription(ticketDTO.getDescription());
        ticket.setPriority(ticketDTO.getPriority() != null ? ticketDTO.getPriority() : "MEDIUM");
        ticket.setStatus("OPEN");
        ticket.setCategory(ticketDTO.getCategory() != null ? ticketDTO.getCategory() : "GENERAL");
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());

        SupportTicket savedTicket = supportTicketRepository.save(ticket);

        // Notify admins about new ticket
        notifyAdminsAboutNewTicket(savedTicket);

        return mapToSupportTicketDTO(savedTicket);
    }

    public SupportTicketDTO getTicketById(Long ticketId) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Support ticket not found with id: " + ticketId));
        return mapToSupportTicketDTO(ticket);
    }

    public SupportTicketDTO getTicketByNumber(String ticketNumber) {
        SupportTicket ticket = supportTicketRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new RuntimeException("Support ticket not found with number: " + ticketNumber));
        return mapToSupportTicketDTO(ticket);
    }

    public List<SupportTicketDTO> getUserTickets(Long userId) {
        return supportTicketRepository.findByUserId(userId).stream()
                .map(this::mapToSupportTicketDTO)
                .collect(Collectors.toList());
    }

    public List<SupportTicketDTO> getTicketsByStatus(String status) {
        return supportTicketRepository.findByStatus(status).stream()
                .map(this::mapToSupportTicketDTO)
                .collect(Collectors.toList());
    }

    public List<SupportTicketDTO> getActiveTickets() {
        return supportTicketRepository.findActiveTickets().stream()
                .map(this::mapToSupportTicketDTO)
                .collect(Collectors.toList());
    }

    public List<SupportTicketDTO> getTicketsByPriority(String priority) {
        return supportTicketRepository.findByPriority(priority).stream()
                .map(this::mapToSupportTicketDTO)
                .collect(Collectors.toList());
    }

    public List<SupportTicketDTO> getTicketsByCategory(String category) {
        return supportTicketRepository.findByCategory(category).stream()
                .map(this::mapToSupportTicketDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SupportTicketDTO updateTicketStatus(Long ticketId, String status, String adminNotes) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Support ticket not found with id: " + ticketId));

        ticket.setStatus(status);
        ticket.setUpdatedAt(LocalDateTime.now());

        if ("RESOLVED".equals(status) || "CLOSED".equals(status)) {
            ticket.setResolvedAt(LocalDateTime.now());
        }

        SupportTicket updatedTicket = supportTicketRepository.save(ticket);

        // Notify user about status change
        notifyUserAboutTicketUpdate(updatedTicket, status);

        return mapToSupportTicketDTO(updatedTicket);
    }

    @Transactional
    public SupportTicketDTO assignTicketToAdmin(Long ticketId, String adminUsername) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Support ticket not found with id: " + ticketId));

        // In a real implementation, you would have an admin assignment field
        // For now, we'll just update the status
        ticket.setStatus("IN_PROGRESS");
        ticket.setUpdatedAt(LocalDateTime.now());

        SupportTicket updatedTicket = supportTicketRepository.save(ticket);
        return mapToSupportTicketDTO(updatedTicket);
    }

    @Transactional
    public SupportTicketDTO escalateTicket(Long ticketId, String reason) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Support ticket not found with id: " + ticketId));

        ticket.setPriority("HIGH");
        ticket.setUpdatedAt(LocalDateTime.now());

        SupportTicket updatedTicket = supportTicketRepository.save(ticket);

        // Notify senior admins about escalated ticket
        notifyAdminsAboutEscalatedTicket(updatedTicket, reason);

        return mapToSupportTicketDTO(updatedTicket);
    }

    @Transactional
    public void closeResolvedTickets() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7); // Close tickets resolved more than 7 days ago
        List<SupportTicket> resolvedTickets = supportTicketRepository.findByStatus("RESOLVED").stream()
                .filter(ticket -> ticket.getResolvedAt() != null && ticket.getResolvedAt().isBefore(cutoffDate))
                .collect(Collectors.toList());

        for (SupportTicket ticket : resolvedTickets) {
            ticket.setStatus("CLOSED");
            ticket.setUpdatedAt(LocalDateTime.now());
        }

        supportTicketRepository.saveAll(resolvedTickets);
    }

    public List<Object[]> getTicketStatisticsByCategory() {
        return supportTicketRepository.countTicketsByCategory();
    }

    public long getOpenTicketsCount() {
        return supportTicketRepository.countByStatus("OPEN");
    }

    public long getInProgressTicketsCount() {
        return supportTicketRepository.countByStatus("IN_PROGRESS");
    }

    private String generateTicketNumber() {
        String ticketNumber;
        do {
            ticketNumber = "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (supportTicketRepository.findByTicketNumber(ticketNumber).isPresent());
        return ticketNumber;
    }

    private void notifyAdminsAboutNewTicket(SupportTicket ticket) {
        // In production, this would notify all available admins
        String message = String.format(
                "New support ticket created: %s - %s (Priority: %s)",
                ticket.getTicketNumber(), ticket.getSubject(), ticket.getPriority()
        );
        // Implementation would send notifications to admin users
    }

    private void notifyUserAboutTicketUpdate(SupportTicket ticket, String newStatus) {
        notificationService.createNotification(
                ticket.getUser().getId(),
                createTicketUpdateNotification(ticket, newStatus)
        );
    }

    private void notifyAdminsAboutEscalatedTicket(SupportTicket ticket, String reason) {
        String message = String.format(
                "Ticket %s has been escalated. Reason: %s",
                ticket.getTicketNumber(), reason
        );
        // Implementation would send notifications to senior admins
    }

    private com.web_app.yaviPrint.dto.NotificationDTO createTicketUpdateNotification(SupportTicket ticket, String newStatus) {
        com.web_app.yaviPrint.dto.NotificationDTO notification = new com.web_app.yaviPrint.dto.NotificationDTO();
        notification.setTitle("Support Ticket Update");
        notification.setMessage(String.format(
                "Your ticket %s has been updated to: %s",
                ticket.getTicketNumber(), newStatus
        ));
        notification.setType("SUPPORT");
        notification.setActionUrl("/support/tickets/" + ticket.getTicketNumber());
        notification.setPriority(3);
        return notification;
    }

    private SupportTicketDTO mapToSupportTicketDTO(SupportTicket ticket) {
        SupportTicketDTO dto = new SupportTicketDTO();
        dto.setId(ticket.getId());
        dto.setUserId(ticket.getUser().getId());
        dto.setTicketNumber(ticket.getTicketNumber());
        dto.setSubject(ticket.getSubject());
        dto.setDescription(ticket.getDescription());
        dto.setPriority(ticket.getPriority());
        dto.setStatus(ticket.getStatus());
        dto.setCategory(ticket.getCategory());
        dto.setCreatedAt(ticket.getCreatedAt());
        dto.setUpdatedAt(ticket.getUpdatedAt());
        dto.setResolvedAt(ticket.getResolvedAt());
        return dto;
    }
}