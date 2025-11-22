package com.web_app.yaviPrint.repository;

import com.web_app.yaviPrint.entity.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    Optional<SupportTicket> findByTicketNumber(String ticketNumber);
    List<SupportTicket> findByUserId(Long userId);
    List<SupportTicket> findByStatus(String status);
    List<SupportTicket> findByPriority(String priority);
    List<SupportTicket> findByCategory(String category);

    // ADD MISSING METHOD
    long countByStatus(String status);

    @Query("SELECT t FROM SupportTicket t WHERE t.createdAt >= :startDate AND t.createdAt <= :endDate")
    List<SupportTicket> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT t FROM SupportTicket t WHERE t.status IN ('OPEN', 'IN_PROGRESS') ORDER BY t.priority DESC, t.createdAt ASC")
    List<SupportTicket> findActiveTickets();

    @Query("SELECT t.category, COUNT(t) FROM SupportTicket t GROUP BY t.category")
    List<Object[]> countTicketsByCategory();
}