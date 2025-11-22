package com.web_app.yaviPrint.repository;

import com.web_app.yaviPrint.entity.ShopDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopDeviceRepository extends JpaRepository<ShopDevice, Long> {
    List<ShopDevice> findByShopId(Long shopId);
    List<ShopDevice> findByShopIdAndIsActiveTrue(Long shopId);
    Optional<ShopDevice> findByMacAddress(String macAddress);
    Optional<ShopDevice> findByShopIdAndDeviceName(Long shopId, String deviceName);
}