package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.ShopDeviceDTO;
import com.web_app.yaviPrint.entity.Shop;
import com.web_app.yaviPrint.entity.ShopDevice;
import com.web_app.yaviPrint.repository.ShopDeviceRepository;
import com.web_app.yaviPrint.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopDeviceService {

    private final ShopDeviceRepository shopDeviceRepository;
    private final ShopRepository shopRepository;

    @Transactional
    public ShopDeviceDTO registerDevice(Long shopId, ShopDeviceDTO deviceDTO) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found with id: " + shopId));

        // Check if device with same MAC address already exists
        if (deviceDTO.getMacAddress() != null) {
            shopDeviceRepository.findByMacAddress(deviceDTO.getMacAddress())
                    .ifPresent(device -> {
                        throw new RuntimeException("Device with this MAC address already registered");
                    });
        }

        ShopDevice device = new ShopDevice();
        device.setShop(shop);
        device.setDeviceName(deviceDTO.getDeviceName());
        device.setDeviceType(deviceDTO.getDeviceType());
        device.setMacAddress(deviceDTO.getMacAddress());
        device.setIpAddress(deviceDTO.getIpAddress());
        device.setActive(true);
        device.setLastSeen(LocalDateTime.now());
        device.setPrinterName(deviceDTO.getPrinterName());
        device.setPrinterModel(deviceDTO.getPrinterModel());

        ShopDevice savedDevice = shopDeviceRepository.save(device);
        return mapToShopDeviceDTO(savedDevice);
    }

    public List<ShopDeviceDTO> getShopDevices(Long shopId) {
        return shopDeviceRepository.findByShopId(shopId).stream()
                .map(this::mapToShopDeviceDTO)
                .collect(Collectors.toList());
    }

    public List<ShopDeviceDTO> getActiveShopDevices(Long shopId) {
        return shopDeviceRepository.findByShopIdAndIsActiveTrue(shopId).stream()
                .map(this::mapToShopDeviceDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateDeviceStatus(Long deviceId, boolean isActive) {
        ShopDevice device = shopDeviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found with id: " + deviceId));
        device.setActive(isActive);
        device.setLastSeen(LocalDateTime.now());
        shopDeviceRepository.save(device);
    }

    @Transactional
    public void recordDeviceHeartbeat(String macAddress, String ipAddress) {
        ShopDevice device = shopDeviceRepository.findByMacAddress(macAddress)
                .orElseThrow(() -> new RuntimeException("Device not found with MAC address: " + macAddress));
        device.setIpAddress(ipAddress);
        device.setLastSeen(LocalDateTime.now());
        shopDeviceRepository.save(device);
    }

    private ShopDeviceDTO mapToShopDeviceDTO(ShopDevice device) {
        ShopDeviceDTO dto = new ShopDeviceDTO();
        dto.setId(device.getId());
        dto.setShopId(device.getShop().getId());
        dto.setDeviceName(device.getDeviceName());
        dto.setDeviceType(device.getDeviceType());
        dto.setMacAddress(device.getMacAddress());
        dto.setIpAddress(device.getIpAddress());
        dto.setActive(device.isActive());
        dto.setLastSeen(device.getLastSeen());
        dto.setPrinterName(device.getPrinterName());
        dto.setPrinterModel(device.getPrinterModel());
        return dto;
    }
}