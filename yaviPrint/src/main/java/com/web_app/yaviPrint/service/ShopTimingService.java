package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.ShopTimingDTO;
import com.web_app.yaviPrint.entity.Shop;
import com.web_app.yaviPrint.entity.ShopTiming;
import com.web_app.yaviPrint.repository.ShopRepository;
import com.web_app.yaviPrint.repository.ShopTimingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopTimingService {

    private final ShopTimingRepository shopTimingRepository;
    private final ShopRepository shopRepository;

    @Transactional
    public ShopTimingDTO createOrUpdateShopTiming(Long shopId, ShopTimingDTO timingDTO) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found with id: " + shopId));

        ShopTiming timing = shopTimingRepository.findByShopIdAndDayOfWeek(shopId, timingDTO.getDayOfWeek())
                .orElse(new ShopTiming());

        timing.setShop(shop);
        timing.setDayOfWeek(timingDTO.getDayOfWeek());
        timing.setOpeningTime(timingDTO.getOpeningTime());
        timing.setClosingTime(timingDTO.getClosingTime());
        timing.setClosed(timingDTO.getClosed());

        ShopTiming savedTiming = shopTimingRepository.save(timing);
        return mapToShopTimingDTO(savedTiming);
    }

    public List<ShopTimingDTO> getShopTimings(Long shopId) {
        return shopTimingRepository.findByShopId(shopId).stream()
                .map(this::mapToShopTimingDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteShopTiming(Long timingId) {
        shopTimingRepository.deleteById(timingId);
    }

    private ShopTimingDTO mapToShopTimingDTO(ShopTiming timing) {
        ShopTimingDTO dto = new ShopTimingDTO();
        dto.setId(timing.getId());
        dto.setDayOfWeek(timing.getDayOfWeek());
        dto.setOpeningTime(timing.getOpeningTime());
        dto.setClosingTime(timing.getClosingTime());
        dto.setClosed(timing.isClosed());
        return dto;
    }
}