package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.ShopPricingDTO;
import com.web_app.yaviPrint.entity.Shop;
import com.web_app.yaviPrint.entity.ShopPricing;
import com.web_app.yaviPrint.repository.ShopPricingRepository;
import com.web_app.yaviPrint.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopPricingService {

    private final ShopPricingRepository shopPricingRepository;
    private final ShopRepository shopRepository;

    @Transactional
    public ShopPricingDTO createOrUpdatePricing(Long shopId, ShopPricingDTO pricingDTO) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found with id: " + shopId));

        ShopPricing pricing = shopPricingRepository
                .findByShopIdAndPaperTypeAndPrintType(shopId, pricingDTO.getPaperType(), pricingDTO.getPrintType())
                .orElse(new ShopPricing());

        pricing.setShop(shop);
        pricing.setPaperType(pricingDTO.getPaperType());
        pricing.setPrintType(pricingDTO.getPrintType());
        pricing.setPricePerPage(pricingDTO.getPricePerPage());
        pricing.setPricePerCopy(pricingDTO.getPricePerCopy());
        pricing.setDuplexPricing(pricingDTO.getDuplexPricing());
        pricing.setDuplexExtraCharge(pricingDTO.getDuplexExtraCharge());

        ShopPricing savedPricing = shopPricingRepository.save(pricing);
        return mapToShopPricingDTO(savedPricing);
    }

    public List<ShopPricingDTO> getShopPricing(Long shopId) {
        return shopPricingRepository.findByShopId(shopId).stream()
                .map(this::mapToShopPricingDTO)
                .collect(Collectors.toList());
    }

    public Double calculatePrintPrice(Long shopId, String paperType, String printType,
                                      int pages, int copies, boolean duplex) {
        ShopPricing pricing = shopPricingRepository
                .findByShopIdAndPaperTypeAndPrintType(shopId, paperType, printType)
                .orElseThrow(() -> new RuntimeException("Pricing not found for the specified criteria"));

        double totalPrice = pricing.getPricePerPage() * pages * copies;
        if (duplex && pricing.isDuplexPricing()) {
            totalPrice += pricing.getDuplexExtraCharge() * copies;
        }

        return totalPrice;
    }

    private ShopPricingDTO mapToShopPricingDTO(ShopPricing pricing) {
        ShopPricingDTO dto = new ShopPricingDTO();
        dto.setId(pricing.getId());
        dto.setPaperType(pricing.getPaperType());
        dto.setPrintType(pricing.getPrintType());
        dto.setPricePerPage(pricing.getPricePerPage());
        dto.setPricePerCopy(pricing.getPricePerCopy());
        dto.setDuplexPricing(pricing.isDuplexPricing());
        dto.setDuplexExtraCharge(pricing.getDuplexExtraCharge());
        return dto;
    }
}