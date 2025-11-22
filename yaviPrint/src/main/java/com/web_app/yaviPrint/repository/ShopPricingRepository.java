package com.web_app.yaviPrint.repository;

import com.web_app.yaviPrint.entity.ShopPricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopPricingRepository extends JpaRepository<ShopPricing, Long> {
    List<ShopPricing> findByShopId(Long shopId);
    Optional<ShopPricing> findByShopIdAndPaperTypeAndPrintType(Long shopId, String paperType, String printType);
    void deleteByShopId(Long shopId);
}