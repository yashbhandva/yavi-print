package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.WalletTransactionDTO;
import com.web_app.yaviPrint.entity.User;
import com.web_app.yaviPrint.entity.WalletTransaction;
import com.web_app.yaviPrint.repository.UserRepository;
import com.web_app.yaviPrint.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletTransactionService {

    private final WalletTransactionRepository walletTransactionRepository;
    private final UserRepository userRepository;

    @Transactional
    public WalletTransactionDTO addWalletTransaction(Long userId, WalletTransactionDTO transactionDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Calculate new balance
        Double currentBalance = getCurrentBalance(userId);
        Double newBalance = calculateNewBalance(currentBalance, transactionDTO);

        WalletTransaction transaction = new WalletTransaction();
        transaction.setUser(user);
        transaction.setTransactionType(transactionDTO.getTransactionType());
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setBalanceAfter(newBalance);
        transaction.setDescription(transactionDTO.getDescription());
        transaction.setReferenceType(transactionDTO.getReferenceType());
        transaction.setReferenceId(transactionDTO.getReferenceId());
        transaction.setTransactionDate(LocalDateTime.now());

        WalletTransaction savedTransaction = walletTransactionRepository.save(transaction);
        return mapToWalletTransactionDTO(savedTransaction);
    }

    public Double getCurrentBalance(Long userId) {
        Double totalCredits = walletTransactionRepository.getTotalCreditsByUser(userId);
        Double totalDebits = walletTransactionRepository.getTotalDebitsByUser(userId);

        totalCredits = totalCredits != null ? totalCredits : 0.0;
        totalDebits = totalDebits != null ? totalDebits : 0.0;

        return totalCredits - totalDebits;
    }

    public List<WalletTransactionDTO> getUserWalletTransactions(Long userId) {
        return walletTransactionRepository.findByUserIdOrderByTransactionDateDesc(userId).stream()
                .map(this::mapToWalletTransactionDTO)
                .collect(Collectors.toList());
    }

    public List<WalletTransactionDTO> getUserWalletTransactionsByDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return walletTransactionRepository.findByUserIdAndDateRange(userId, startDate, endDate).stream()
                .map(this::mapToWalletTransactionDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public WalletTransactionDTO addCredit(Long userId, Double amount, String description, String referenceType, Long referenceId) {
        WalletTransactionDTO transactionDTO = new WalletTransactionDTO();
        transactionDTO.setTransactionType("CREDIT");
        transactionDTO.setAmount(amount);
        transactionDTO.setDescription(description);
        transactionDTO.setReferenceType(referenceType);
        transactionDTO.setReferenceId(referenceId);

        return addWalletTransaction(userId, transactionDTO);
    }

    @Transactional
    public WalletTransactionDTO addDebit(Long userId, Double amount, String description, String referenceType, Long referenceId) {
        Double currentBalance = getCurrentBalance(userId);
        if (currentBalance < amount) {
            throw new RuntimeException("Insufficient wallet balance");
        }

        WalletTransactionDTO transactionDTO = new WalletTransactionDTO();
        transactionDTO.setTransactionType("DEBIT");
        transactionDTO.setAmount(amount);
        transactionDTO.setDescription(description);
        transactionDTO.setReferenceType(referenceType);
        transactionDTO.setReferenceId(referenceId);

        return addWalletTransaction(userId, transactionDTO);
    }

    private Double calculateNewBalance(Double currentBalance, WalletTransactionDTO transactionDTO) {
        if ("CREDIT".equals(transactionDTO.getTransactionType())) {
            return currentBalance + transactionDTO.getAmount();
        } else if ("DEBIT".equals(transactionDTO.getTransactionType())) {
            return currentBalance - transactionDTO.getAmount();
        } else {
            throw new RuntimeException("Invalid transaction type: " + transactionDTO.getTransactionType());
        }
    }

    private WalletTransactionDTO mapToWalletTransactionDTO(WalletTransaction transaction) {
        WalletTransactionDTO dto = new WalletTransactionDTO();
        dto.setId(transaction.getId());
        dto.setUserId(transaction.getUser().getId());
        dto.setTransactionType(transaction.getTransactionType());
        dto.setAmount(transaction.getAmount());
        dto.setBalanceAfter(transaction.getBalanceAfter());
        dto.setDescription(transaction.getDescription());
        dto.setReferenceType(transaction.getReferenceType());
        dto.setReferenceId(transaction.getReferenceId());
        dto.setTransactionDate(transaction.getTransactionDate());
        return dto;
    }
}