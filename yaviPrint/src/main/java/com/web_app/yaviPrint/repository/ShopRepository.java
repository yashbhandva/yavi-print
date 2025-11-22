package com.web_app.yaviPrint.repository;

import com.web_app.yaviPrint.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    List<Shop> findByIsActiveTrueAndIsOnlineTrue();
    Optional<Shop> findByOwnerId(Long ownerId);
    List<Shop> findByCityAndIsActiveTrue(String city);
    List<Shop> findByIsVerifiedTrueAndIsActiveTrue();

    @Query("SELECT s FROM Shop s WHERE s.isActive = true AND " +
            "(:city IS NULL OR s.city = :city) AND " +
            "(:minRating IS NULL OR s.rating >= :minRating)")
    List<Shop> findActiveShopsByFilters(String city, Double minRating);

    @Query("SELECT s FROM Shop s WHERE s.isActive = true AND " +
            "6371 * acos(cos(radians(:lat)) * cos(radians(s.latitude)) * " +
            "cos(radians(s.longitude) - radians(:lng)) + " +
            "sin(radians(:lat)) * sin(radians(s.latitude))) < :radius")
    List<Shop> findNearbyShops(Double lat, Double lng, Double radius);

    long countByIsActiveTrue();
    long countByIsVerifiedTrueAndIsActiveTrue();
}