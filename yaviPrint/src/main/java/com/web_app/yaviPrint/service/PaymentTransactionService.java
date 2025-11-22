package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.PaymentTransactionDTO;
import com.web_app.yaviPrint.entity.Payment;
import com.web_app.yaviPrint.entity.PaymentTransaction;
import com.web_app.yaviPrint.repository.PaymentRepository;
import com.web_app.yaviPrint.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentTransactionService {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentTransactionDTO recordTransaction(Long paymentId, PaymentTransactionDTO transactionDTO) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setPayment(payment);
        transaction.setTransactionId(transactionDTO.getTransactionId());
        transaction.setTransactionType(transactionDTO.getTransactionType());
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setStatus(transactionDTO.getStatus());
        transaction.setGatewayResponse(transactionDTO.getGatewayResponse());
        transaction.setTransactionTime(LocalDateTime.now());
        transaction.setNotes(transactionDTO.getNotes());

        PaymentTransaction savedTransaction = paymentTransactionRepository.save(transaction);
        return mapToPaymentTransactionDTO(savedTransaction);
    }

    public List<PaymentTransactionDTO> getPaymentTransactions(Long paymentId) {
        return paymentTransactionRepository.findByPaymentId(paymentId).stream()
                .map(this::mapToPaymentTransactionDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PaymentTransactionDTO recordRefundTransaction(Long paymentId, Double refundAmount, String reason) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));

        PaymentTransaction refundTransaction = new PaymentTransaction();
        refundTransaction.setPayment(payment);
        refundTransaction.setTransactionId("REFUND_" + System.currentTimeMillis());
        refundTransaction.setTransactionType("REFUND");
        refundTransaction.setAmount(refundAmount);
        refundTransaction.setStatus("PROCESSED");
        refundTransaction.setGatewayResponse("Refund processed successfully");
        refundTransaction.setTransactionTime(LocalDateTime.now());
        refundTransaction.setNotes("Refund reason: " + reason);

        PaymentTransaction savedTransaction = paymentTransactionRepository.save(refundTransaction);
        return mapToPaymentTransactionDTO(savedTransaction);
    }

    private PaymentTransactionDTO mapToPaymentTransactionDTO(PaymentTransaction transaction) {
        PaymentTransactionDTO dto = new PaymentTransactionDTO();
        dto.setId(transaction.getId());
        dto.setPaymentId(transaction.getPayment().getId());
        dto.setTransactionId(transaction.getTransactionId());
        dto.setTransactionType(transaction.getTransactionType());
        dto.setAmount(transaction.getAmount());
        dto.setStatus(transaction.getStatus());
        dto.setGatewayResponse(transaction.getGatewayResponse());
        dto.setTransactionTime(transaction.getTransactionTime());
        dto.setNotes(transaction.getNotes());
        return dto;
    }
}