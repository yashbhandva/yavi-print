package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.ShopServiceDTO;
import com.web_app.yaviPrint.entity.Shop;
import com.web_app.yaviPrint.entity.ShopService;
import com.web_app.yaviPrint.repository.ShopRepository;
import com.web_app.yaviPrint.repository.ShopServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopServiceService {

    private final ShopServiceRepository shopServiceRepository;
    private final ShopRepository shopRepository;

    @Transactional
    public ShopServiceDTO createShopService(Long shopId, ShopServiceDTO serviceDTO) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found with id: " + shopId));

        if (shopServiceRepository.existsByShopIdAndServiceName(shopId, serviceDTO.getServiceName())) {
            throw new RuntimeException("Service already exists for this shop");
        }

        ShopService service = new ShopService();
        service.setShop(shop);
        service.setServiceName(serviceDTO.getServiceName());
        service.setDescription(serviceDTO.getDescription());
        service.setPrice(serviceDTO.getPrice());
        service.setAvailable(serviceDTO.getAvailable());
        service.setEstimatedTime(serviceDTO.getEstimatedTime());

        ShopService savedService = shopServiceRepository.save(service);
        return mapToShopServiceDTO(savedService);
    }

    public List<ShopServiceDTO> getShopServices(Long shopId) {
        return shopServiceRepository.findByShopId(shopId).stream()
                .map(this::mapToShopServiceDTO)
                .collect(Collectors.toList());
    }

    public List<ShopServiceDTO> getAvailableShopServices(Long shopId) {
        return shopServiceRepository.findByShopIdAndIsAvailableTrue(shopId).stream()
                .map(this::mapToShopServiceDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ShopServiceDTO updateShopService(Long serviceId, ShopServiceDTO serviceDTO) {
        ShopService service = shopServiceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Shop service not found with id: " + serviceId));

        if (serviceDTO.getServiceName() != null) {
            service.setServiceName(serviceDTO.getServiceName());
        }
        if (serviceDTO.getDescription() != null) {
            service.setDescription(serviceDTO.getDescription());
        }
        if (serviceDTO.getPrice() != null) {
            service.setPrice(serviceDTO.getPrice());
        }
        if (serviceDTO.getAvailable() != null) {
            service.setAvailable(serviceDTO.getAvailable());
        }
        if (serviceDTO.getEstimatedTime() != null) {
            service.setEstimatedTime(serviceDTO.getEstimatedTime());
        }

        ShopService updatedService = shopServiceRepository.save(service);
        return mapToShopServiceDTO(updatedService);
    }

    @Transactional
    public void deleteShopService(Long serviceId) {
        shopServiceRepository.deleteById(serviceId);
    }

    private ShopServiceDTO mapToShopServiceDTO(ShopService service) {
        ShopServiceDTO dto = new ShopServiceDTO();
        dto.setId(service.getId());
        dto.setServiceName(service.getServiceName());
        dto.setDescription(service.getDescription());
        dto.setPrice(service.getPrice());
        dto.setAvailable(service.isAvailable());
        dto.setEstimatedTime(service.getEstimatedTime());
        return dto;
    }
}