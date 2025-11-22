package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.ShopVerificationDTO;
import com.web_app.yaviPrint.entity.Shop;
import com.web_app.yaviPrint.entity.ShopVerification;
import com.web_app.yaviPrint.repository.ShopRepository;
import com.web_app.yaviPrint.repository.ShopVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopVerificationService {

    private final ShopVerificationRepository shopVerificationRepository;
    private final ShopRepository shopRepository;

    @Transactional
    public ShopVerificationDTO applyForVerification(Long shopId, String documentsSubmitted) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found with id: " + shopId));

        ShopVerification verification = shopVerificationRepository.findByShopId(shopId)
                .orElse(new ShopVerification());

        verification.setShop(shop);
        verification.setVerified(false);
        verification.setDocumentsSubmitted(documentsSubmitted);
        verification.setAppliedAt(LocalDateTime.now());

        ShopVerification savedVerification = shopVerificationRepository.save(verification);
        return mapToShopVerificationDTO(savedVerification);
    }

    @Transactional
    public ShopVerificationDTO verifyShop(Long shopId, String verifiedBy) {
        ShopVerification verification = shopVerificationRepository.findByShopId(shopId)
                .orElseThrow(() -> new RuntimeException("Verification application not found for shop id: " + shopId));

        verification.setVerified(true);
        verification.setVerifiedBy(verifiedBy);
        verification.setVerificationDate(LocalDateTime.now());

        // Update shop verification status
        Shop shop = verification.getShop();
        shop.setVerified(true);

        shopRepository.save(shop);
        ShopVerification updatedVerification = shopVerificationRepository.save(verification);
        return mapToShopVerificationDTO(updatedVerification);
    }

    @Transactional
    public ShopVerificationDTO rejectVerification(Long shopId, String rejectionReason, String verifiedBy) {
        ShopVerification verification = shopVerificationRepository.findByShopId(shopId)
                .orElseThrow(() -> new RuntimeException("Verification application not found for shop id: " + shopId));

        verification.setVerified(false);
        verification.setVerifiedBy(verifiedBy);
        verification.setRejectionReason(rejectionReason);
        verification.setVerificationDate(LocalDateTime.now());

        ShopVerification updatedVerification = shopVerificationRepository.save(verification);
        return mapToShopVerificationDTO(updatedVerification);
    }

    public ShopVerificationDTO getShopVerification(Long shopId) {
        ShopVerification verification = shopVerificationRepository.findByShopId(shopId)
                .orElseThrow(() -> new RuntimeException("Verification not found for shop id: " + shopId));
        return mapToShopVerificationDTO(verification);
    }

    public List<ShopVerificationDTO> getPendingVerifications() {
        return shopVerificationRepository.findByIsVerifiedFalse().stream()
                .map(this::mapToShopVerificationDTO)
                .collect(Collectors.toList());
    }

    private ShopVerificationDTO mapToShopVerificationDTO(ShopVerification verification) {
        ShopVerificationDTO dto = new ShopVerificationDTO();
        dto.setId(verification.getId());
        dto.setShopId(verification.getShop().getId());
        dto.setVerified(verification.isVerified());
        dto.setVerificationDate(verification.getVerificationDate());
        dto.setVerifiedBy(verification.getVerifiedBy());
        dto.setDocumentsSubmitted(verification.getDocumentsSubmitted());
        dto.setRejectionReason(verification.getRejectionReason());
        dto.setAppliedAt(verification.getAppliedAt());
        return dto;
    }
}