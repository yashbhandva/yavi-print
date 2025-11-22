package com.web_app.yaviPrint.repository;

import com.web_app.yaviPrint.entity.SupportMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportMessageRepository extends JpaRepository<SupportMessage, Long> {
    List<SupportMessage> findByTicketIdOrderBySentAtAsc(Long ticketId);
    List<SupportMessage> findByTicketIdAndIsFromAdmin(Long ticketId, boolean isFromAdmin);

    // ADD MISSING METHOD
    long countByTicketIdAndIsReadFalseAndIsFromAdmin(Long ticketId, boolean isFromAdmin);

    @Query("SELECT m FROM SupportMessage m WHERE m.ticket.id = :ticketId AND m.isRead = false AND m.isFromAdmin = false")
    List<SupportMessage> findUnreadCustomerMessages(Long ticketId);

    @Query("SELECT m FROM SupportMessage m WHERE m.ticket.id = :ticketId AND m.isRead = false AND m.isFromAdmin = true")
    List<SupportMessage> findUnreadAdminMessages(Long ticketId);
}