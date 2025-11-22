package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.PrintJobStatusDTO;
import com.web_app.yaviPrint.entity.Order;
import com.web_app.yaviPrint.entity.PrintJobStatus;
import com.web_app.yaviPrint.repository.OrderRepository;
import com.web_app.yaviPrint.repository.PrintJobStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrintJobStatusService {

    private final PrintJobStatusRepository printJobStatusRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public PrintJobStatusDTO updatePrintJobStatus(Long orderId, String status, String message, Integer progress) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        PrintJobStatus jobStatus = new PrintJobStatus();
        jobStatus.setOrder(order);
        jobStatus.setStatus(status);
        jobStatus.setMessage(message);
        jobStatus.setProgress(progress != null ? progress : 0);
        jobStatus.setStatusTime(LocalDateTime.now());

        PrintJobStatus savedStatus = printJobStatusRepository.save(jobStatus);
        return mapToPrintJobStatusDTO(savedStatus);
    }

    public List<PrintJobStatusDTO> getPrintJobStatusHistory(Long orderId) {
        return printJobStatusRepository.findByOrderIdOrderByStatusTimeDesc(orderId).stream()
                .map(this::mapToPrintJobStatusDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void recordPrintJobProgress(Long orderId, String status, int progress) {
        updatePrintJobStatus(orderId, status, "Print job in progress", progress);
    }

    @Transactional
    public void recordPrintJobCompletion(Long orderId) {
        updatePrintJobStatus(orderId, "COMPLETED", "Print job completed successfully", 100);
    }

    @Transactional
    public void recordPrintJobFailure(Long orderId, String errorMessage) {
        updatePrintJobStatus(orderId, "FAILED", errorMessage, 0);
    }

    private PrintJobStatusDTO mapToPrintJobStatusDTO(PrintJobStatus status) {
        PrintJobStatusDTO dto = new PrintJobStatusDTO();
        dto.setId(status.getId());
        dto.setOrderId(status.getOrder().getId());
        dto.setStatus(status.getStatus());
        dto.setMessage(status.getMessage());
        dto.setStatusTime(status.getStatusTime());
        dto.setProgress(status.getProgress());
        return dto;
    }
}