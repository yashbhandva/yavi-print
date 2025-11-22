package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.PrintVolumeStatsDTO;
import com.web_app.yaviPrint.entity.Order;
import com.web_app.yaviPrint.entity.PrintVolumeStats;
import com.web_app.yaviPrint.repository.PrintVolumeStatsRepository;
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
public class PrintVolumeStatsService {

    private final PrintVolumeStatsRepository printVolumeStatsRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public void generatePrintVolumeStats(LocalDate statDate) {
        PrintVolumeStats stats = printVolumeStatsRepository.findByStatDate(statDate)
                .orElse(new PrintVolumeStats());

        LocalDateTime startOfDay = statDate.atStartOfDay();
        LocalDateTime endOfDay = statDate.plusDays(1).atStartOfDay();

        // Get all completed orders for the day
        List<Order> completedOrders = orderRepository.findAll().stream()
                .filter(order -> order.getStatus() == Order.OrderStatus.COMPLETED &&
                        order.getPrintedAt() != null &&
                        order.getPrintedAt().isAfter(startOfDay) &&
                        order.getPrintedAt().isBefore(endOfDay))
                .collect(Collectors.toList());

        int totalPagesPrinted = completedOrders.stream()
                .mapToInt(Order::getTotalPages)
                .sum();

        // Calculate breakdowns (simplified - in production would parse print settings)
        int bwPages = (int) (totalPagesPrinted * 0.7); // Assume 70% BW
        int colorPages = totalPagesPrinted - bwPages;
        int a4Pages = (int) (totalPagesPrinted * 0.9); // Assume 90% A4
        int a3Pages = totalPagesPrinted - a4Pages;
        int photoPages = (int) (totalPagesPrinted * 0.1); // Assume 10% photo

        // Hourly distribution (simplified)
        String hourlyDistribution = calculateHourlyDistribution(completedOrders);

        // Popular time slots
        String popularTimeSlots = calculatePopularTimeSlots(completedOrders);

        stats.setStatDate(statDate);
        stats.setTotalPagesPrinted(totalPagesPrinted);
        stats.setBwPages(bwPages);
        stats.setColorPages(colorPages);
        stats.setA4Pages(a4Pages);
        stats.setA3Pages(a3Pages);
        stats.setPhotoPages(photoPages);
        stats.setHourlyDistribution(hourlyDistribution);
        stats.setPopularTimeSlots(popularTimeSlots);

        printVolumeStatsRepository.save(stats);
    }

    public PrintVolumeStatsDTO getStatsForDate(LocalDate statDate) {
        PrintVolumeStats stats = printVolumeStatsRepository.findByStatDate(statDate)
                .orElseThrow(() -> new RuntimeException("Print volume stats not found for date: " + statDate));
        return mapToPrintVolumeStatsDTO(stats);
    }

    public List<PrintVolumeStatsDTO> getStatsForDateRange(LocalDate startDate, LocalDate endDate) {
        return printVolumeStatsRepository.findByStatDateBetween(startDate, endDate).stream()
                .map(this::mapToPrintVolumeStatsDTO)
                .collect(Collectors.toList());
    }

    public Long getTotalPagesPrintedBetween(LocalDate startDate, LocalDate endDate) {
        Long pages = printVolumeStatsRepository.getTotalPagesPrintedBetween(startDate, endDate);
        return pages != null ? pages : 0L;
    }

    public Long getTotalBWPagesBetween(LocalDate startDate, LocalDate endDate) {
        Long pages = printVolumeStatsRepository.getTotalBWPagesBetween(startDate, endDate);
        return pages != null ? pages : 0L;
    }

    public Long getTotalColorPagesBetween(LocalDate startDate, LocalDate endDate) {
        Long pages = printVolumeStatsRepository.getTotalColorPagesBetween(startDate, endDate);
        return pages != null ? pages : 0L;
    }

    public Double getAverageDailyPrintVolume(LocalDate startDate, LocalDate endDate) {
        Double avg = printVolumeStatsRepository.getAverageDailyPrintVolume(startDate, endDate);
        return avg != null ? avg : 0.0;
    }

    public PrintVolumeSummary getPrintVolumeSummary(LocalDate startDate, LocalDate endDate) {
        Long totalPages = getTotalPagesPrintedBetween(startDate, endDate);
        Long bwPages = getTotalBWPagesBetween(startDate, endDate);
        Long colorPages = getTotalColorPagesBetween(startDate, endDate);
        Double avgDailyVolume = getAverageDailyPrintVolume(startDate, endDate);

        double bwPercentage = totalPages > 0 ? (double) bwPages / totalPages * 100 : 0;
        double colorPercentage = totalPages > 0 ? (double) colorPages / totalPages * 100 : 0;

        // Calculate growth compared to previous period
        LocalDate previousStartDate = startDate.minusDays(endDate.toEpochDay() - startDate.toEpochDay() + 1);
        LocalDate previousEndDate = startDate.minusDays(1);

        Long previousTotalPages = getTotalPagesPrintedBetween(previousStartDate, previousEndDate);
        double growthRate = previousTotalPages > 0 ?
                ((double) (totalPages - previousTotalPages) / previousTotalPages) * 100 : 0;

        return new PrintVolumeSummary(totalPages, bwPages, colorPages, bwPercentage,
                colorPercentage, avgDailyVolume, growthRate);
    }

    @Scheduled(cron = "0 15 2 * * ?") // Run at 2:15 AM daily
    @Transactional
    public void generateYesterdayPrintVolumeStats() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        generatePrintVolumeStats(yesterday);
    }

    private String calculateHourlyDistribution(List<Order> orders) {
        int[] hourlyCounts = new int[24];

        for (Order order : orders) {
            if (order.getPrintedAt() != null) {
                int hour = order.getPrintedAt().getHour();
                hourlyCounts[hour]++;
            }
        }

        StringBuilder distribution = new StringBuilder("[");
        for (int i = 0; i < 24; i++) {
            distribution.append("{\"hour\":").append(i)
                    .append(",\"count\":").append(hourlyCounts[i]).append("}");
            if (i < 23) distribution.append(",");
        }
        distribution.append("]");

        return distribution.toString();
    }

    private String calculatePopularTimeSlots(List<Order> orders) {
        int morning = 0, afternoon = 0, evening = 0, night = 0;

        for (Order order : orders) {
            if (order.getPrintedAt() != null) {
                int hour = order.getPrintedAt().getHour();
                if (hour >= 6 && hour < 12) morning++;
                else if (hour >= 12 && hour < 17) afternoon++;
                else if (hour >= 17 && hour < 22) evening++;
                else night++;
            }
        }

        return String.format("{\"morning\":%d,\"afternoon\":%d,\"evening\":%d,\"night\":%d}",
                morning, afternoon, evening, night);
    }

    private PrintVolumeStatsDTO mapToPrintVolumeStatsDTO(PrintVolumeStats stats) {
        PrintVolumeStatsDTO dto = new PrintVolumeStatsDTO();
        dto.setId(stats.getId());
        dto.setStatDate(stats.getStatDate());
        dto.setTotalPagesPrinted(stats.getTotalPagesPrinted());
        dto.setBwPages(stats.getBwPages());
        dto.setColorPages(stats.getColorPages());
        dto.setA4Pages(stats.getA4Pages());
        dto.setA3Pages(stats.getA3Pages());
        dto.setPhotoPages(stats.getPhotoPages());
        dto.setHourlyDistribution(stats.getHourlyDistribution());
        dto.setPopularTimeSlots(stats.getPopularTimeSlots());
        return dto;
    }

    // Inner class for print volume summary
    public static class PrintVolumeSummary {
        private final Long totalPages;
        private final Long bwPages;
        private final Long colorPages;
        private final Double bwPercentage;
        private final Double colorPercentage;
        private final Double averageDailyVolume;
        private final Double growthRate;

        public PrintVolumeSummary(Long totalPages, Long bwPages, Long colorPages,
                                  Double bwPercentage, Double colorPercentage,
                                  Double averageDailyVolume, Double growthRate) {
            this.totalPages = totalPages;
            this.bwPages = bwPages;
            this.colorPages = colorPages;
            this.bwPercentage = bwPercentage;
            this.colorPercentage = colorPercentage;
            this.averageDailyVolume = averageDailyVolume;
            this.growthRate = growthRate;
        }

        // Getters
        public Long getTotalPages() { return totalPages; }
        public Long getBwPages() { return bwPages; }
        public Long getColorPages() { return colorPages; }
        public Double getBwPercentage() { return bwPercentage; }
        public Double getColorPercentage() { return colorPercentage; }
        public Double getAverageDailyVolume() { return averageDailyVolume; }
        public Double getGrowthRate() { return growthRate; }
    }
}