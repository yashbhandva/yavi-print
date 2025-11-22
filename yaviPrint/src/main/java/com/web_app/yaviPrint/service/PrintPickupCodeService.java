package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.PrintPickupCodeDTO;
import com.web_app.yaviPrint.entity.PrintPickupCode;
import com.web_app.yaviPrint.repository.PrintPickupCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PrintPickupCodeService {

    private final PrintPickupCodeRepository printPickupCodeRepository;

    public PrintPickupCodeDTO getPickupCodeByToken(String tokenId) {
        PrintPickupCode pickupCode = printPickupCodeRepository.findByTokenId(tokenId)
                .orElseThrow(() -> new RuntimeException("Pickup code not found for token: " + tokenId));
        return mapToPrintPickupCodeDTO(pickupCode);
    }

    public PrintPickupCodeDTO getPickupCodeByShortCode(String shortCode) {
        PrintPickupCode pickupCode = printPickupCodeRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("Pickup code not found for short code: " + shortCode));
        return mapToPrintPickupCodeDTO(pickupCode);
    }

    @Transactional
    public PrintPickupCodeDTO markPickupCodeAsUsed(String tokenId) {
        PrintPickupCode pickupCode = printPickupCodeRepository.findByTokenId(tokenId)
                .orElseThrow(() -> new RuntimeException("Pickup code not found for token: " + tokenId));

        if (pickupCode.isUsed()) {
            throw new RuntimeException("Pickup code already used");
        }

        if (pickupCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Pickup code has expired");
        }

        pickupCode.setUsed(true);
        pickupCode.setUsedAt(LocalDateTime.now());

        PrintPickupCode updatedPickupCode = printPickupCodeRepository.save(pickupCode);
        return mapToPrintPickupCodeDTO(updatedPickupCode);
    }

    @Transactional
    public void validateAndUsePickupCode(String tokenId) {
        PrintPickupCode pickupCode = printPickupCodeRepository.findByTokenId(tokenId)
                .orElseThrow(() -> new RuntimeException("Invalid pickup code"));

        if (pickupCode.isUsed()) {
            throw new RuntimeException("Pickup code already used");
        }

        if (pickupCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Pickup code has expired");
        }

        markPickupCodeAsUsed(tokenId);
    }

    @Transactional
    public void cleanupExpiredPickupCodes() {
        LocalDateTime now = LocalDateTime.now();
        printPickupCodeRepository.findExpiredUnusedCodes(now)
                .forEach(pickupCode -> {
                    // Optionally notify user about expired pickup codes
                    printPickupCodeRepository.delete(pickupCode);
                });
    }

    private PrintPickupCodeDTO mapToPrintPickupCodeDTO(PrintPickupCode pickupCode) {
        PrintPickupCodeDTO dto = new PrintPickupCodeDTO();
        dto.setId(pickupCode.getId());
        dto.setOrderId(pickupCode.getOrder().getId());
        dto.setTokenId(pickupCode.getTokenId());
        dto.setQrCodeUrl(pickupCode.getQrCodeUrl());
        dto.setShortCode(pickupCode.getShortCode());
        dto.setGeneratedAt(pickupCode.getGeneratedAt());
        dto.setExpiresAt(pickupCode.getExpiresAt());
        dto.setUsed(pickupCode.isUsed());
        dto.setUsedAt(pickupCode.getUsedAt());
        return dto;
    }
}