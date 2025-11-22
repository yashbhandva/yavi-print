package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.DailyUserStatsDTO;
import com.web_app.yaviPrint.entity.DailyUserStats;
import com.web_app.yaviPrint.entity.Order;
import com.web_app.yaviPrint.entity.User;
import com.web_app.yaviPrint.repository.DailyUserStatsRepository;
import com.web_app.yaviPrint.repository.UserRepository;
import com.web_app.yaviPrint.repository.OrderRepository;
import com.web_app.yaviPrint.repository.UserLoginHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DailyUserStatsService {

    private final DailyUserStatsRepository dailyUserStatsRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final UserLoginHistoryRepository userLoginHistoryRepository;

    @Transactional
    public void generateDailyUserStats(LocalDate statDate) {
        DailyUserStats stats = dailyUserStatsRepository.findByStatDate(statDate)
                .orElse(new DailyUserStats());

        LocalDateTime startOfDay = statDate.atStartOfDay();
        LocalDateTime endOfDay = statDate.plusDays(1).atStartOfDay();

        // New registrations
        long newRegistrations = userRepository.findAll().stream()
                .filter(user -> user.getCreatedAt() != null &&
                        user.getCreatedAt().isAfter(startOfDay) &&
                        user.getCreatedAt().isBefore(endOfDay))
                .count();

        // Active users (users who logged in today)
        long activeUsers = userLoginHistoryRepository.countActiveUsersSince(startOfDay);

        // Total orders placed today
        long totalOrders = orderRepository.findAll().stream()
                .filter(order -> order.getCreatedAt() != null &&
                        order.getCreatedAt().isAfter(startOfDay) &&
                        order.getCreatedAt().isBefore(endOfDay))
                .count();

        // Returning users (users who logged in today and had previous activity)
        long returningUsers = calculateReturningUsers(startOfDay, endOfDay);

        // Total revenue today
        double totalRevenue = orderRepository.findAll().stream()
                .filter(order -> order.getCreatedAt() != null &&
                        order.getCreatedAt().isAfter(startOfDay) &&
                        order.getCreatedAt().isBefore(endOfDay) &&
                        order.isPaymentStatus())
                .mapToDouble(Order::getTotalAmount)
                .sum();

        // Device breakdown (simplified)
        String deviceBreakdown = calculateDeviceBreakdown(startOfDay, endOfDay);

        stats.setStatDate(statDate);
        stats.setNewRegistrations((int) newRegistrations);
        stats.setActiveUsers((int) activeUsers);
        stats.setTotalOrders((int) totalOrders);
        stats.setReturningUsers((int) returningUsers);
        stats.setTotalRevenue(totalRevenue);
        stats.setDeviceBreakdown(deviceBreakdown);

        dailyUserStatsRepository.save(stats);
    }

    public DailyUserStatsDTO getStatsForDate(LocalDate statDate) {
        DailyUserStats stats = dailyUserStatsRepository.findByStatDate(statDate)
                .orElseThrow(() -> new RuntimeException("Stats not found for date: " + statDate));
        return mapToDailyUserStatsDTO(stats);
    }

    public List<DailyUserStatsDTO> getStatsForDateRange(LocalDate startDate, LocalDate endDate) {
        return dailyUserStatsRepository.findByStatDateBetween(startDate, endDate).stream()
                .map(this::mapToDailyUserStatsDTO)
                .collect(Collectors.toList());
    }

    public Long getTotalNewRegistrations(LocalDate startDate, LocalDate endDate) {
        Long count = dailyUserStatsRepository.getTotalNewRegistrations(startDate, endDate);
        return count != null ? count : 0L;
    }

    public Double getAverageActiveUsers(LocalDate startDate, LocalDate endDate) {
        Double avg = dailyUserStatsRepository.getAverageActiveUsers(startDate, endDate);
        return avg != null ? avg : 0.0;
    }

    public Double getTotalRevenueForPeriod(LocalDate startDate, LocalDate endDate) {
        Double revenue = dailyUserStatsRepository.getTotalRevenueInPeriod(startDate, endDate);
        return revenue != null ? revenue : 0.0;
    }

    public PlatformGrowthMetrics getPlatformGrowthMetrics(LocalDate startDate, LocalDate endDate) {
        List<DailyUserStatsDTO> stats = getStatsForDateRange(startDate, endDate);

        long totalNewUsers = stats.stream().mapToLong(DailyUserStatsDTO::getNewRegistrations).sum();
        double avgDailyActiveUsers = stats.stream().mapToInt(DailyUserStatsDTO::getActiveUsers).average().orElse(0);
        long totalOrders = stats.stream().mapToLong(DailyUserStatsDTO::getTotalOrders).sum();
        double totalRevenue = stats.stream().mapToDouble(DailyUserStatsDTO::getTotalRevenue).sum();

        // Calculate growth compared to previous period
        LocalDate previousStartDate = startDate.minusDays(endDate.toEpochDay() - startDate.toEpochDay() + 1);
        LocalDate previousEndDate = startDate.minusDays(1);

        long previousNewUsers = getTotalNewRegistrations(previousStartDate, previousEndDate);
        double userGrowthRate = previousNewUsers > 0 ?
                ((double) (totalNewUsers - previousNewUsers) / previousNewUsers) * 100 : 0;

        double previousRevenue = getTotalRevenueForPeriod(previousStartDate, previousEndDate);
        double revenueGrowthRate = previousRevenue > 0 ?
                ((totalRevenue - previousRevenue) / previousRevenue) * 100 : 0;

        return new PlatformGrowthMetrics(totalNewUsers, userGrowthRate, avgDailyActiveUsers,
                totalOrders, totalRevenue, revenueGrowthRate);
    }

    @Scheduled(cron = "0 5 2 * * ?") // Run at 2:05 AM daily
    @Transactional
    public void generateYesterdayUserStats() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        generateDailyUserStats(yesterday);
    }

    private long calculateReturningUsers(LocalDateTime startOfDay, LocalDateTime endOfDay) {
        return userLoginHistoryRepository.findAll().stream()
                .filter(login -> login.getLoginTime() != null &&
                        login.getLoginTime().isAfter(startOfDay) &&
                        login.getLoginTime().isBefore(endOfDay) &&
                        login.isSuccess())
                .map(login -> login.getUser().getId())
                .distinct()
                .filter(userId -> hasPreviousActivity(userId, startOfDay))
                .count();
    }

    private boolean hasPreviousActivity(Long userId, LocalDateTime beforeDate) {
        return userLoginHistoryRepository.findAll().stream()
                .anyMatch(login -> login.getUser().getId().equals(userId) &&
                        login.getLoginTime() != null &&
                        login.getLoginTime().isBefore(beforeDate) &&
                        login.isSuccess());
    }

    private String calculateDeviceBreakdown(LocalDateTime startOfDay, LocalDateTime endOfDay) {
        long mobileLogins = userLoginHistoryRepository.findAll().stream()
                .filter(login -> login.getLoginTime() != null &&
                        login.getLoginTime().isAfter(startOfDay) &&
                        login.getLoginTime().isBefore(endOfDay) &&
                        "MOBILE".equals(login.getDeviceType()))
                .count();

        long desktopLogins = userLoginHistoryRepository.findAll().stream()
                .filter(login -> login.getLoginTime() != null &&
                        login.getLoginTime().isAfter(startOfDay) &&
                        login.getLoginTime().isBefore(endOfDay) &&
                        "DESKTOP".equals(login.getDeviceType()))
                .count();

        long tabletLogins = userLoginHistoryRepository.findAll().stream()
                .filter(login -> login.getLoginTime() != null &&
                        login.getLoginTime().isAfter(startOfDay) &&
                        login.getLoginTime().isBefore(endOfDay) &&
                        "TABLET".equals(login.getDeviceType()))
                .count();

        long totalLogins = mobileLogins + desktopLogins + tabletLogins;

        if (totalLogins == 0) {
            return "{\"mobile\":0,\"desktop\":0,\"tablet\":0}";
        }

        int mobilePercent = (int) ((mobileLogins * 100) / totalLogins);
        int desktopPercent = (int) ((desktopLogins * 100) / totalLogins);
        int tabletPercent = (int) ((tabletLogins * 100) / totalLogins);

        return String.format("{\"mobile\":%d,\"desktop\":%d,\"tablet\":%d}",
                mobilePercent, desktopPercent, tabletPercent);
    }

    private DailyUserStatsDTO mapToDailyUserStatsDTO(DailyUserStats stats) {
        DailyUserStatsDTO dto = new DailyUserStatsDTO();
        dto.setId(stats.getId());
        dto.setStatDate(stats.getStatDate());
        dto.setNewRegistrations(stats.getNewRegistrations());
        dto.setActiveUsers(stats.getActiveUsers());
        dto.setTotalOrders(stats.getTotalOrders());
        dto.setReturningUsers(stats.getReturningUsers());
        dto.setTotalRevenue(stats.getTotalRevenue());
        dto.setDeviceBreakdown(stats.getDeviceBreakdown());
        return dto;
    }

    // Inner class for growth metrics
    public static class PlatformGrowthMetrics {
        private final Long totalNewUsers;
        private final Double userGrowthRate;
        private final Double avgDailyActiveUsers;
        private final Long totalOrders;
        private final Double totalRevenue;
        private final Double revenueGrowthRate;

        public PlatformGrowthMetrics(Long totalNewUsers, Double userGrowthRate, Double avgDailyActiveUsers,
                                     Long totalOrders, Double totalRevenue, Double revenueGrowthRate) {
            this.totalNewUsers = totalNewUsers;
            this.userGrowthRate = userGrowthRate;
            this.avgDailyActiveUsers = avgDailyActiveUsers;
            this.totalOrders = totalOrders;
            this.totalRevenue = totalRevenue;
            this.revenueGrowthRate = revenueGrowthRate;
        }

        // Getters
        public Long getTotalNewUsers() { return totalNewUsers; }
        public Double getUserGrowthRate() { return userGrowthRate; }
        public Double getAvgDailyActiveUsers() { return avgDailyActiveUsers; }
        public Long getTotalOrders() { return totalOrders; }
        public Double getTotalRevenue() { return totalRevenue; }
        public Double getRevenueGrowthRate() { return revenueGrowthRate; }
    }
}