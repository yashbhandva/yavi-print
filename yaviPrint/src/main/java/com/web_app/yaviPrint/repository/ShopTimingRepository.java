package com.web_app.yaviPrint.repository;

import com.web_app.yaviPrint.entity.ShopTiming;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShopTimingRepository extends JpaRepository<ShopTiming, Long> {
    List<ShopTiming> findByShopId(Long shopId);
    Optional<ShopTiming> findByShopIdAndDayOfWeek(Long shopId, DayOfWeek dayOfWeek);
    void deleteByShopId(Long shopId);
}