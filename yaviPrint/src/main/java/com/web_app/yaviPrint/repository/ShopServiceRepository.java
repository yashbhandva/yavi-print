package com.web_app.yaviPrint.repository;

import com.web_app.yaviPrint.entity.ShopService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopServiceRepository extends JpaRepository<ShopService, Long> {
    List<ShopService> findByShopIdAndIsAvailableTrue(Long shopId);
    List<ShopService> findByShopId(Long shopId);
    boolean existsByShopIdAndServiceName(Long shopId, String serviceName);
}