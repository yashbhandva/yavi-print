package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.ShopCreateDTO;
import com.web_app.yaviPrint.dto.ShopResponseDTO;
import com.web_app.yaviPrint.dto.ShopUpdateDTO;
import com.web_app.yaviPrint.entity.Shop;
import com.web_app.yaviPrint.entity.User;
import com.web_app.yaviPrint.repository.ShopRepository;
import com.web_app.yaviPrint.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final UserRepository userRepository;

    @Transactional
    public ShopResponseDTO createShop(ShopCreateDTO shopCreateDTO, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + ownerId));

        // Check if user already has a shop
        if (shopRepository.findByOwnerId(ownerId).isPresent()) {
            throw new RuntimeException("User already has a shop");
        }

        Shop shop = new Shop();
        shop.setShopName(shopCreateDTO.getShopName());
        shop.setDescription(shopCreateDTO.getDescription());
        shop.setAddress(shopCreateDTO.getAddress());
        shop.setCity(shopCreateDTO.getCity());
        shop.setState(shopCreateDTO.getState());
        shop.setPincode(shopCreateDTO.getPincode());
        shop.setLatitude(shopCreateDTO.getLatitude());
        shop.setLongitude(shopCreateDTO.getLongitude());
        shop.setOwner(owner);

        Shop savedShop = shopRepository.save(shop);
        return mapToShopResponseDTO(savedShop);
    }

    public ShopResponseDTO getShopById(Long id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shop not found with id: " + id));
        return mapToShopResponseDTO(shop);
    }

    public ShopResponseDTO getShopByOwnerId(Long ownerId) {
        Shop shop = shopRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new RuntimeException("Shop not found for owner id: " + ownerId));
        return mapToShopResponseDTO(shop);
    }

    @Transactional
    public ShopResponseDTO updateShop(Long id, ShopUpdateDTO shopUpdateDTO) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shop not found with id: " + id));

        if (shopUpdateDTO.getShopName() != null) {
            shop.setShopName(shopUpdateDTO.getShopName());
        }
        if (shopUpdateDTO.getDescription() != null) {
            shop.setDescription(shopUpdateDTO.getDescription());
        }
        if (shopUpdateDTO.getAddress() != null) {
            shop.setAddress(shopUpdateDTO.getAddress());
        }
        if (shopUpdateDTO.getCity() != null) {
            shop.setCity(shopUpdateDTO.getCity());
        }
        if (shopUpdateDTO.getState() != null) {
            shop.setState(shopUpdateDTO.getState());
        }
        if (shopUpdateDTO.getPincode() != null) {
            shop.setPincode(shopUpdateDTO.getPincode());
        }
        if (shopUpdateDTO.getLatitude() != null) {
            shop.setLatitude(shopUpdateDTO.getLatitude());
        }
        if (shopUpdateDTO.getLongitude() != null) {
            shop.setLongitude(shopUpdateDTO.getLongitude());
        }
        if (shopUpdateDTO.getBwPricePerPage() != null) {
            shop.setBwPricePerPage(shopUpdateDTO.getBwPricePerPage());
        }
        if (shopUpdateDTO.getColorPricePerPage() != null) {
            shop.setColorPricePerPage(shopUpdateDTO.getColorPricePerPage());
        }
        if (shopUpdateDTO.getOnline() != null) {
            shop.setOnline(shopUpdateDTO.getOnline());
        }

        Shop updatedShop = shopRepository.save(shop);
        return mapToShopResponseDTO(updatedShop);
    }

    public List<ShopResponseDTO> getAllActiveShops() {
        return shopRepository.findByIsActiveTrueAndIsOnlineTrue().stream()
                .map(this::mapToShopResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ShopResponseDTO> getShopsByCity(String city) {
        return shopRepository.findByCityAndIsActiveTrue(city).stream()
                .map(this::mapToShopResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ShopResponseDTO> getNearbyShops(Double lat, Double lng, Double radius) {
        return shopRepository.findNearbyShops(lat, lng, radius).stream()
                .map(this::mapToShopResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void toggleShopStatus(Long id, boolean isOnline) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shop not found with id: " + id));
        shop.setOnline(isOnline);
        shopRepository.save(shop);
    }

    private ShopResponseDTO mapToShopResponseDTO(Shop shop) {
        ShopResponseDTO dto = new ShopResponseDTO();
        dto.setId(shop.getId());
        dto.setShopName(shop.getShopName());
        dto.setDescription(shop.getDescription());
        dto.setAddress(shop.getAddress());
        dto.setCity(shop.getCity());
        dto.setState(shop.getState());
        dto.setPincode(shop.getPincode());
        dto.setLatitude(shop.getLatitude());
        dto.setLongitude(shop.getLongitude());
        dto.setBwPricePerPage(shop.getBwPricePerPage());
        dto.setColorPricePerPage(shop.getColorPricePerPage());
        dto.setRating(shop.getRating());
        dto.setTotalReviews(shop.getTotalReviews());
        dto.setActive(shop.isActive());
        dto.setOnline(shop.isOnline());
        dto.setVerified(shop.isVerified());
        dto.setCreatedAt(shop.getCreatedAt());
        return dto;
    }
}