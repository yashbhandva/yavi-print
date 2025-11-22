package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.ShopStatusDTO;
import com.web_app.yaviPrint.entity.Shop;
import com.web_app.yaviPrint.entity.ShopStatus;
import com.web_app.yaviPrint.repository.ShopRepository;
import com.web_app.yaviPrint.repository.ShopStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ShopStatusService {

    private final ShopStatusRepository shopStatusRepository;
    private final ShopRepository shopRepository;

    @Transactional
    public ShopStatusDTO updateShopStatus(Long shopId, ShopStatusDTO statusDTO) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found with id: " + shopId));

        ShopStatus status = shopStatusRepository.findByShopId(shopId)
                .orElse(new ShopStatus());

        status.setShop(shop);
        status.setOnline(statusDTO.getOnline());
        status.setBusy(statusDTO.getBusy());
        status.setCurrentQueueSize(statusDTO.getCurrentQueueSize());
        status.setMaxQueueSize(statusDTO.getMaxQueueSize());
        status.setBusyReason(statusDTO.getBusyReason());
        status.setStatusUpdatedAt(LocalDateTime.now());

        ShopStatus savedStatus = shopStatusRepository.save(status);
        return mapToShopStatusDTO(savedStatus);
    }

    public ShopStatusDTO getShopStatus(Long shopId) {
        ShopStatus status = shopStatusRepository.findByShopId(shopId)
                .orElseThrow(() -> new RuntimeException("Shop status not found for shop id: " + shopId));
        return mapToShopStatusDTO(status);
    }

    @Transactional
    public void setShopBusy(Long shopId, String busyReason) {
        ShopStatusDTO statusDTO = new ShopStatusDTO();
        statusDTO.setBusy(true);
        statusDTO.setBusyReason(busyReason);
        updateShopStatus(shopId, statusDTO);
    }

    @Transactional
    public void setShopAvailable(Long shopId) {
        ShopStatusDTO statusDTO = new ShopStatusDTO();
        statusDTO.setBusy(false);
        statusDTO.setBusyReason(null);
        updateShopStatus(shopId, statusDTO);
    }

    @Transactional
    public void updateQueueSize(Long shopId, int currentQueueSize) {
        ShopStatus status = shopStatusRepository.findByShopId(shopId)
                .orElse(new ShopStatus());

        if (status.getShop() == null) {
            Shop shop = shopRepository.findById(shopId)
                    .orElseThrow(() -> new RuntimeException("Shop not found with id: " + shopId));
            status.setShop(shop);
        }

        status.setCurrentQueueSize(currentQueueSize);
        status.setStatusUpdatedAt(LocalDateTime.now());
        shopStatusRepository.save(status);
    }

    private ShopStatusDTO mapToShopStatusDTO(ShopStatus status) {
        ShopStatusDTO dto = new ShopStatusDTO();
        dto.setId(status.getId());
        dto.setShopId(status.getShop().getId());
        dto.setOnline(status.isOnline());
        dto.setBusy(status.isBusy());
        dto.setCurrentQueueSize(status.getCurrentQueueSize());
        dto.setMaxQueueSize(status.getMaxQueueSize());
        dto.setStatusUpdatedAt(status.getStatusUpdatedAt());
        dto.setBusyReason(status.getBusyReason());
        return dto;
    }
}