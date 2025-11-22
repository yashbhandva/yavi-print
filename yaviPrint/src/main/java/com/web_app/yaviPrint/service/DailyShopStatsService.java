package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.DailyShopStatsDTO;
import com.web_app.yaviPrint.entity.DailyShopStats;
import com.web_app.yaviPrint.entity.Order;
import com.web_app.yaviPrint.entity.Shop;
import com.web_app.yaviPrint.repository.DailyShopStatsRepository;
import com.web_app.yaviPrint.repository.OrderRepository;
import com.web_app.yaviPrint.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DailyShopStatsService {

    private final DailyShopStatsRepository dailyShopStatsRepository;
    private final ShopRepository shopRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public void generateDailyShopStats(LocalDate statDate) {
        List<Shop> activeShops = shopRepository.findByIsActiveTrueAndIsOnlineTrue();

        for (Shop shop : activeShops) {
            DailyShopStats stats = dailyShopStatsRepository.findByShopIdAndStatDate(shop.getId(), statDate)
                    .orElse(new DailyShopStats());

            // Calculate stats for the day
            LocalDate startOfDay = statDate.atStartOfDay().toLocalDate();
            LocalDate endOfDay = statDate.plusDays(1).atStartOfDay().toLocalDate();

            List<Order> dailyOrders = orderRepository.findByShopIdAndDateRange(
                    shop.getId(), startOfDay.atStartOfDay(), endOfDay.atStartOfDay()
            );

            int totalOrders = dailyOrders.size();
            int completedOrders = (int) dailyOrders.stream()
                    .filter(order -> order.getStatus() == Order.OrderStatus.COMPLETED)
                    .count();
            int cancelledOrders = (int) dailyOrders.stream()
                    .filter(order -> order.getStatus() == Order.OrderStatus.CANCELLED)
                    .count();

            double totalRevenue = dailyOrders.stream()
                    .filter(order -> order.getStatus() == Order.OrderStatus.COMPLETED)
                    .mapToDouble(Order::getTotalAmount)
                    .sum();

            double platformEarnings = dailyOrders.stream()
                    .filter(order -> order.getStatus() == Order.OrderStatus.COMPLETED)
                    .mapToDouble(Order::getPlatformFee)
                    .sum();

            // Calculate new customers (users who placed first order at this shop today)
            long newCustomers = dailyOrders.stream()
                    .map(Order::getUser)
                    .distinct()
                    .filter(user -> isFirstOrderAtShop(user.getId(), shop.getId(), statDate))
                    .count();

            stats.setShop(shop);
            stats.setStatDate(statDate);
            stats.setTotalOrders(totalOrders);
            stats.setCompletedOrders(completedOrders);
            stats.setCancelledOrders(cancelledOrders);
            stats.setTotalRevenue(totalRevenue);
            stats.setPlatformEarnings(platformEarnings);
            stats.setNewCustomers((int) newCustomers);
            stats.setAverageRating(shop.getRating());
            stats.setPageViews(0); // Would come from analytics service

            dailyShopStatsRepository.save(stats);
        }
    }

    public DailyShopStatsDTO getShopStatsForDate(Long shopId, LocalDate statDate) {
        DailyShopStats stats = dailyShopStatsRepository.findByShopIdAndStatDate(shopId, statDate)
                .orElseThrow(() -> new RuntimeException("Stats not found for shop id: " + shopId + " on date: " + statDate));
        return mapToDailyShopStatsDTO(stats);
    }

    public List<DailyShopStatsDTO> getShopStatsForDateRange(Long shopId, LocalDate startDate, LocalDate endDate) {
        return dailyShopStatsRepository.findByShopIdAndStatDateBetween(shopId, startDate, endDate).stream()
                .map(this::mapToDailyShopStatsDTO)
                .collect(Collectors.toList());
    }

    public List<DailyShopStatsDTO> getTopPerformingShops(LocalDate startDate, LocalDate endDate, int limit) {
        return dailyShopStatsRepository.findTopPerformingShops(startDate, endDate).stream()
                .limit(limit)
                .map(this::mapToDailyShopStatsDTO)
                .collect(Collectors.toList());
    }

    public Double getShopTotalRevenueForPeriod(Long shopId, LocalDate startDate, LocalDate endDate) {
        Double revenue = dailyShopStatsRepository.getTotalRevenueByShopAndDateRange(shopId, startDate, endDate);
        return revenue != null ? revenue : 0.0;
    }

    public Double getShopAverageRatingForPeriod(Long shopId, LocalDate startDate, LocalDate endDate) {
        Double avgRating = dailyShopStatsRepository.getAverageRatingByShopAndDateRange(shopId, startDate, endDate);
        return avgRating != null ? avgRating : 0.0;
    }

    public ShopPerformanceSummary getShopPerformanceSummary(Long shopId, LocalDate startDate, LocalDate endDate) {
        Double totalRevenue = getShopTotalRevenueForPeriod(shopId, startDate, endDate);
        Double avgRating = getShopAverageRatingForPeriod(shopId, startDate, endDate);

        List<DailyShopStatsDTO> stats = getShopStatsForDateRange(shopId, startDate, endDate);

        int totalOrders = stats.stream().mapToInt(DailyShopStatsDTO::getTotalOrders).sum();
        int completedOrders = stats.stream().mapToInt(DailyShopStatsDTO::getCompletedOrders).sum();
        int newCustomers = stats.stream().mapToInt(DailyShopStatsDTO::getNewCustomers).sum();

        double completionRate = totalOrders > 0 ? (double) completedOrders / totalOrders * 100 : 0;

        return new ShopPerformanceSummary(shopId, totalRevenue, totalOrders, completedOrders,
                completionRate, newCustomers, avgRating);
    }

    @Scheduled(cron = "0 0 2 * * ?") // Run at 2 AM daily
    @Transactional
    public void generateYesterdayStats() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        generateDailyShopStats(yesterday);
    }

    private boolean isFirstOrderAtShop(Long userId, Long shopId, LocalDate date) {
        // Check if this user has any orders at this shop before today
        LocalDate startOfDay = date.atStartOfDay().toLocalDate();
        List<Order> previousOrders = orderRepository.findByShopIdAndDateRange(
                shopId, LocalDate.MIN.atStartOfDay(), startOfDay.atStartOfDay()
        );

        return previousOrders.stream()
                .noneMatch(order -> order.getUser().getId().equals(userId));
    }

    private DailyShopStatsDTO mapToDailyShopStatsDTO(DailyShopStats stats) {
        DailyShopStatsDTO dto = new DailyShopStatsDTO();
        dto.setId(stats.getId());
        dto.setShopId(stats.getShop().getId());
        dto.setStatDate(stats.getStatDate());
        dto.setTotalOrders(stats.getTotalOrders());
        dto.setCompletedOrders(stats.getCompletedOrders());
        dto.setCancelledOrders(stats.getCancelledOrders());
        dto.setTotalRevenue(stats.getTotalRevenue());
        dto.setPlatformEarnings(stats.getPlatformEarnings());
        dto.setNewCustomers(stats.getNewCustomers());
        dto.setAverageRating(stats.getAverageRating());
        dto.setPageViews(stats.getPageViews());
        return dto;
    }

    // Inner class for performance summary
    public static class ShopPerformanceSummary {
        private final Long shopId;
        private final Double totalRevenue;
        private final Integer totalOrders;
        private final Integer completedOrders;
        private final Double completionRate;
        private final Integer newCustomers;
        private final Double averageRating;

        public ShopPerformanceSummary(Long shopId, Double totalRevenue, Integer totalOrders,
                                      Integer completedOrders, Double completionRate,
                                      Integer newCustomers, Double averageRating) {
            this.shopId = shopId;
            this.totalRevenue = totalRevenue;
            this.totalOrders = totalOrders;
            this.completedOrders = completedOrders;
            this.completionRate = completionRate;
            this.newCustomers = newCustomers;
            this.averageRating = averageRating;
        }

        // Getters
        public Long getShopId() { return shopId; }
        public Double getTotalRevenue() { return totalRevenue; }
        public Integer getTotalOrders() { return totalOrders; }
        public Integer getCompletedOrders() { return completedOrders; }
        public Double getCompletionRate() { return completionRate; }
        public Integer getNewCustomers() { return newCustomers; }
        public Double getAverageRating() { return averageRating; }
    }
}