package com.web_app.yaviPrint.repository;

import com.web_app.yaviPrint.entity.PrintPickupCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PrintPickupCodeRepository extends JpaRepository<PrintPickupCode, Long> {
    Optional<PrintPickupCode> findByTokenId(String tokenId);
    Optional<PrintPickupCode> findByOrderId(Long orderId);
    Optional<PrintPickupCode> findByShortCode(String shortCode);

    // ADD MISSING METHOD
    boolean existsByTokenId(String tokenId);

    @Query("SELECT p FROM PrintPickupCode p WHERE p.expiresAt < :currentTime AND p.isUsed = false")
    List<PrintPickupCode> findExpiredUnusedCodes(LocalDateTime currentTime);
}