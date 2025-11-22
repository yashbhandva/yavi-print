package com.web_app.yaviPrint.repository;

import com.web_app.yaviPrint.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    List<WalletTransaction> findByUserIdOrderByTransactionDateDesc(Long userId);

    @Query("SELECT w FROM WalletTransaction w WHERE w.user.id = :userId AND w.transactionDate BETWEEN :startDate AND :endDate")
    List<WalletTransaction> findByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT SUM(w.amount) FROM WalletTransaction w WHERE w.user.id = :userId AND w.transactionType = 'CREDIT'")
    Double getTotalCreditsByUser(Long userId);

    @Query("SELECT SUM(w.amount) FROM WalletTransaction w WHERE w.user.id = :userId AND w.transactionType = 'DEBIT'")
    Double getTotalDebitsByUser(Long userId);
}