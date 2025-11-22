package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.dto.SupportTicketDTO;
import com.web_app.yaviPrint.service.SupportTicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/support/tickets")
@RequiredArgsConstructor
public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    @PostMapping
    public ResponseEntity<?> createSupportTicket(
            Authentication authentication,
            @Valid @RequestBody SupportTicketDTO ticketDTO) {
        try {
            Long userId = 1L; // Placeholder - get from authenticated user
            var ticket = supportTicketService.createSupportTicket(userId, ticketDTO);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Support ticket created successfully",
                    "data", ticket
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/my-tickets")
    public ResponseEntity<?> getMyTickets(Authentication authentication) {
        try {
            Long userId = 1L; // Placeholder - get from authenticated user
            var tickets = supportTicketService.getUserTickets(userId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", tickets
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<?> getTicketById(@PathVariable Long ticketId) {
        try {
            var ticket = supportTicketService.getTicketById(ticketId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", ticket
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/number/{ticketNumber}")
    public ResponseEntity<?> getTicketByNumber(@PathVariable String ticketNumber) {
        try {
            var ticket = supportTicketService.getTicketByNumber(ticketNumber);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", ticket
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/{ticketId}/status")
    public ResponseEntity<?> updateTicketStatus(
            @PathVariable Long ticketId,
            @RequestParam String status,
            @RequestParam(required = false) String adminNotes) {
        try {
            var ticket = supportTicketService.updateTicketStatus(ticketId, status, adminNotes);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Ticket status updated successfully",
                    "data", ticket
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/{ticketId}/escalate")
    public ResponseEntity<?> escalateTicket(
            @PathVariable Long ticketId,
            @RequestParam String reason) {
        try {
            var ticket = supportTicketService.escalateTicket(ticketId, reason);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Ticket escalated successfully",
                    "data", ticket
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveTickets() {
        try {
            var tickets = supportTicketService.getActiveTickets();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", tickets
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}