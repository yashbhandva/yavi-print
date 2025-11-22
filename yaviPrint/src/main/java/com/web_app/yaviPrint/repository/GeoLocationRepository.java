package com.web_app.yaviPrint.repository;

import com.web_app.yaviPrint.entity.GeoLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GeoLocationRepository extends JpaRepository<GeoLocation, Long> {
    Optional<GeoLocation> findByShopId(Long shopId);

    @Query("SELECT g FROM GeoLocation g WHERE " +
            "6371 * acos(cos(radians(:lat)) * cos(radians(g.latitude)) * " +
            "cos(radians(g.longitude) - radians(:lng)) + " +
            "sin(radians(:lat)) * sin(radians(g.latitude))) < :radius")
    List<GeoLocation> findNearbyLocations(Double lat, Double lng, Double radius);

    List<GeoLocation> findByCity(String city);
    List<GeoLocation> findByState(String state);
}