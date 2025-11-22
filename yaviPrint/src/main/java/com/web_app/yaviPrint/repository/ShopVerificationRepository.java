package com.web_app.yaviPrint.repository;

import com.web_app.yaviPrint.entity.ShopVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopVerificationRepository extends JpaRepository<ShopVerification, Long> {
    Optional<ShopVerification> findByShopId(Long shopId);
    List<ShopVerification> findByIsVerifiedFalse();
    List<ShopVerification> findByIsVerifiedTrue();
    long countByIsVerifiedFalse();
}