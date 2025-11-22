package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.GeoLocationDTO;
import com.web_app.yaviPrint.entity.GeoLocation;
import com.web_app.yaviPrint.entity.Shop;
import com.web_app.yaviPrint.repository.GeoLocationRepository;
import com.web_app.yaviPrint.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GeoLocationService {

    private final GeoLocationRepository geoLocationRepository;
    private final ShopRepository shopRepository;

    @Transactional
    public GeoLocationDTO updateShopLocation(Long shopId, GeoLocationDTO locationDTO) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found with id: " + shopId));

        GeoLocation geoLocation = geoLocationRepository.findByShopId(shopId)
                .orElse(new GeoLocation());

        geoLocation.setShop(shop);
        geoLocation.setLatitude(locationDTO.getLatitude());
        geoLocation.setLongitude(locationDTO.getLongitude());
        geoLocation.setFormattedAddress(locationDTO.getFormattedAddress());
        geoLocation.setCity(locationDTO.getCity());
        geoLocation.setState(locationDTO.getState());
        geoLocation.setCountry(locationDTO.getCountry());
        geoLocation.setAccuracy(locationDTO.getAccuracy());
        geoLocation.setLastUpdated(LocalDateTime.now());

        GeoLocation savedLocation = geoLocationRepository.save(geoLocation);
        return mapToGeoLocationDTO(savedLocation);
    }

    public GeoLocationDTO getShopLocation(Long shopId) {
        GeoLocation geoLocation = geoLocationRepository.findByShopId(shopId)
                .orElseThrow(() -> new RuntimeException("Location not found for shop id: " + shopId));
        return mapToGeoLocationDTO(geoLocation);
    }

    public List<GeoLocationDTO> findNearbyShops(Double lat, Double lng, Double radiusInKm) {
        return geoLocationRepository.findNearbyLocations(lat, lng, radiusInKm).stream()
                .map(this::mapToGeoLocationDTO)
                .collect(Collectors.toList());
    }

    public List<GeoLocationDTO> getLocationsByCity(String city) {
        return geoLocationRepository.findByCity(city).stream()
                .map(this::mapToGeoLocationDTO)
                .collect(Collectors.toList());
    }

    public Double calculateDistance(Double lat1, Double lng1, Double lat2, Double lng2) {
        final int R = 6371; // Earth's radius in kilometers

        double latDistance = Math.toRadians(lat2 - lat1);
        double lngDistance = Math.toRadians(lng2 - lng1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    private GeoLocationDTO mapToGeoLocationDTO(GeoLocation geoLocation) {
        GeoLocationDTO dto = new GeoLocationDTO();
        dto.setId(geoLocation.getId());
        dto.setShopId(geoLocation.getShop().getId());
        dto.setLatitude(geoLocation.getLatitude());
        dto.setLongitude(geoLocation.getLongitude());
        dto.setFormattedAddress(geoLocation.getFormattedAddress());
        dto.setCity(geoLocation.getCity());
        dto.setState(geoLocation.getState());
        dto.setCountry(geoLocation.getCountry());
        dto.setAccuracy(geoLocation.getAccuracy());
        dto.setLastUpdated(geoLocation.getLastUpdated());
        return dto;
    }
}