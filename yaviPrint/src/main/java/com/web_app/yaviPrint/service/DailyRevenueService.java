package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.DailyRevenueDTO;
import com.web_app.yaviPrint.entity.DailyRevenue;
import com.web_app.yaviPrint.entity.Order;
import com.web_app.yaviPrint.entity.Payment;
import com.web_app.yaviPrint.repository.DailyRevenueRepository;
import com.web_app.yaviPrint.repository.PaymentRepository;
import com.web_app.yaviPrint.repository.OrderRepository;
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
public class DailyRevenueService {

    private final DailyRevenueRepository dailyRevenueRepository;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public void generateDailyRevenueStats(LocalDate revenueDate) {
        DailyRevenue revenue = dailyRevenueRepository.findByRevenueDate(revenueDate)
                .orElse(new DailyRevenue());

        LocalDateTime startOfDay = revenueDate.atStartOfDay();
        LocalDateTime endOfDay = revenueDate.plusDays(1).atStartOfDay();

        // Get all successful payments for the day
        List<Payment> dailyPayments = paymentRepository.findByPaymentDateBetween(startOfDay, endOfDay).stream()
                .filter(payment -> "CAPTURED".equals(payment.getStatus()))
                .collect(Collectors.toList());

        double totalRevenue = dailyPayments.stream()
                .mapToDouble(Payment::getAmount)
                .sum();

        double platformCommission = dailyPayments.stream()
                .mapToDouble(payment -> payment.getOrder().getPlatformFee())
                .sum();

        double shopEarnings = totalRevenue - platformCommission;
        double taxAmount = calculateTaxAmount(totalRevenue);
        int totalTransactions = dailyPayments.size();

        // Calculate refund amount
        double refundAmount = calculateDailyRefunds(revenueDate);

        // Payment method breakdown
        String paymentMethodBreakdown = calculatePaymentMethodBreakdown(dailyPayments);

        revenue.setRevenueDate(revenueDate);
        revenue.setTotalRevenue(totalRevenue);
        revenue.setPlatformCommission(platformCommission);
        revenue.setShopEarnings(shopEarnings);
        revenue.setTaxAmount(taxAmount);
        revenue.setTotalTransactions(totalTransactions);
        revenue.setRefundAmount(refundAmount);
        revenue.setPaymentMethodBreakdown(paymentMethodBreakdown);

        dailyRevenueRepository.save(revenue);
    }

    public DailyRevenueDTO getRevenueForDate(LocalDate revenueDate) {
        DailyRevenue revenue = dailyRevenueRepository.findByRevenueDate(revenueDate)
                .orElseThrow(() -> new RuntimeException("Revenue stats not found for date: " + revenueDate));
        return mapToDailyRevenueDTO(revenue);
    }

    public List<DailyRevenueDTO> getRevenueForDateRange(LocalDate startDate, LocalDate endDate) {
        return dailyRevenueRepository.findByRevenueDateBetween(startDate, endDate).stream()
                .map(this::mapToDailyRevenueDTO)
                .collect(Collectors.toList());
    }

    public Double getTotalRevenueBetween(LocalDate startDate, LocalDate endDate) {
        Double revenue = dailyRevenueRepository.getTotalRevenueBetween(startDate, endDate);
        return revenue != null ? revenue : 0.0;
    }

    public Double getTotalPlatformCommissionBetween(LocalDate startDate, LocalDate endDate) {
        Double commission = dailyRevenueRepository.getTotalPlatformCommissionBetween(startDate, endDate);
        return commission != null ? commission : 0.0;
    }

    public Double getAverageDailyRevenue(LocalDate startDate, LocalDate endDate) {
        Double avgRevenue = dailyRevenueRepository.getAverageDailyRevenue(startDate, endDate);
        return avgRevenue != null ? avgRevenue : 0.0;
    }

    public List<Object[]> getRevenueTrend(LocalDate startDate, LocalDate endDate) {
        return dailyRevenueRepository.getRevenueTrend(startDate, endDate);
    }

    public RevenueSummary getRevenueSummary(LocalDate startDate, LocalDate endDate) {
        Double totalRevenue = getTotalRevenueBetween(startDate, endDate);
        Double platformCommission = getTotalPlatformCommissionBetween(startDate, endDate);
        Double avgDailyRevenue = getAverageDailyRevenue(startDate, endDate);

        List<Object[]> trendData = getRevenueTrend(startDate, endDate);

        // Calculate growth compared to previous period
        LocalDate previousStartDate = startDate.minusDays(endDate.toEpochDay() - startDate.toEpochDay() + 1);
        LocalDate previousEndDate = startDate.minusDays(1);

        Double previousRevenue = getTotalRevenueBetween(previousStartDate, previousEndDate);
        double growthRate = previousRevenue > 0 ?
                ((totalRevenue - previousRevenue) / previousRevenue) * 100 : 0;

        return new RevenueSummary(totalRevenue, platformCommission, avgDailyRevenue,
                growthRate, trendData);
    }

    @Scheduled(cron = "0 10 2 * * ?") // Run at 2:10 AM daily
    @Transactional
    public void generateYesterdayRevenueStats() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        generateDailyRevenueStats(yesterday);
    }

    private double calculateTaxAmount(double totalRevenue) {
        // Simplified tax calculation - 18% GST
        return totalRevenue * 0.18;
    }

    private double calculateDailyRefunds(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        return orderRepository.findAll().stream()
                .filter(order -> order.getCancelledAt() != null &&
                        order.getCancelledAt().isAfter(startOfDay) &&
                        order.getCancelledAt().isBefore(endOfDay) &&
                        order.isPaymentStatus())
                .mapToDouble(Order::getTotalAmount)
                .sum();
    }

    private String calculatePaymentMethodBreakdown(List<Payment> payments) {
        long upiCount = payments.stream().filter(p -> "UPI".equals(p.getMethod())).count();
        long cardCount = payments.stream().filter(p -> "CARD".equals(p.getMethod())).count();
        long netbankingCount = payments.stream().filter(p -> "NETBANKING".equals(p.getMethod())).count();
        long walletCount = payments.stream().filter(p -> "WALLET".equals(p.getMethod())).count();

        int total = payments.size();
        if (total == 0) {
            return "{\"upi\":0,\"card\":0,\"netbanking\":0,\"wallet\":0}";
        }

        int upiPercent = (int) ((upiCount * 100) / total);
        int cardPercent = (int) ((cardCount * 100) / total);
        int netbankingPercent = (int) ((netbankingCount * 100) / total);
        int walletPercent = (int) ((walletCount * 100) / total);

        return String.format("{\"upi\":%d,\"card\":%d,\"netbanking\":%d,\"wallet\":%d}",
                upiPercent, cardPercent, netbankingPercent, walletPercent);
    }

    private DailyRevenueDTO mapToDailyRevenueDTO(DailyRevenue revenue) {
        DailyRevenueDTO dto = new DailyRevenueDTO();
        dto.setId(revenue.getId());
        dto.setRevenueDate(revenue.getRevenueDate());
        dto.setTotalRevenue(revenue.getTotalRevenue());
        dto.setPlatformCommission(revenue.getPlatformCommission());
        dto.setShopEarnings(revenue.getShopEarnings());
        dto.setTaxAmount(revenue.getTaxAmount());
        dto.setTotalTransactions(revenue.getTotalTransactions());
        dto.setRefundAmount(revenue.getRefundAmount());
        dto.setPaymentMethodBreakdown(revenue.getPaymentMethodBreakdown());
        return dto;
    }

    // Inner class for revenue summary
    public static class RevenueSummary {
        private final Double totalRevenue;
        private final Double platformCommission;
        private final Double averageDailyRevenue;
        private final Double growthRate;
        private final List<Object[]> trendData;

        public RevenueSummary(Double totalRevenue, Double platformCommission,
                              Double averageDailyRevenue, Double growthRate,
                              List<Object[]> trendData) {
            this.totalRevenue = totalRevenue;
            this.platformCommission = platformCommission;
            this.averageDailyRevenue = averageDailyRevenue;
            this.growthRate = growthRate;
            this.trendData = trendData;
        }

        // Getters
        public Double getTotalRevenue() { return totalRevenue; }
        public Double getPlatformCommission() { return platformCommission; }
        public Double getAverageDailyRevenue() { return averageDailyRevenue; }
        public Double getGrowthRate() { return growthRate; }
        public List<Object[]> getTrendData() { return trendData; }
    }
}