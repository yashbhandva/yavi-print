package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.ShopReportDTO;
import com.web_app.yaviPrint.entity.Shop;
import com.web_app.yaviPrint.entity.ShopReport;
import com.web_app.yaviPrint.entity.User;
import com.web_app.yaviPrint.repository.ShopRepository;
import com.web_app.yaviPrint.repository.ShopReportRepository;
import com.web_app.yaviPrint.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopReportService {

    private final ShopReportRepository shopReportRepository;
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;

    @Transactional
    public ShopReportDTO createShopReport(Long shopId, Long reportedById, ShopReportDTO reportDTO) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found with id: " + shopId));

        User reportedBy = userRepository.findById(reportedById)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + reportedById));

        ShopReport report = new ShopReport();
        report.setShop(shop);
        report.setReportedBy(reportedBy);
        report.setReportType(reportDTO.getReportType());
        report.setDescription(reportDTO.getDescription());
        report.setStatus("PENDING");
        report.setReportedAt(LocalDateTime.now());

        ShopReport savedReport = shopReportRepository.save(report);
        return mapToShopReportDTO(savedReport);
    }

    public List<ShopReportDTO> getShopReports(Long shopId) {
        return shopReportRepository.findByShopId(shopId).stream()
                .map(this::mapToShopReportDTO)
                .collect(Collectors.toList());
    }

    public List<ShopReportDTO> getPendingShopReports() {
        return shopReportRepository.findPendingReports().stream()
                .map(this::mapToShopReportDTO)
                .collect(Collectors.toList());
    }

    public List<ShopReportDTO> getShopReportsByStatus(String status) {
        return shopReportRepository.findByStatus(status).stream()
                .map(this::mapToShopReportDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ShopReportDTO updateReportStatus(Long reportId, String status, String adminNotes, String processedBy) {
        ShopReport report = shopReportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Shop report not found with id: " + reportId));

        report.setStatus(status);
        report.setAdminNotes(adminNotes);

        if ("RESOLVED".equals(status)) {
            report.setResolvedAt(LocalDateTime.now());
        }

        ShopReport updatedReport = shopReportRepository.save(report);
        return mapToShopReportDTO(updatedReport);
    }

    @Transactional
    public ShopReportDTO investigateReport(Long reportId, String adminNotes, String processedBy) {
        return updateReportStatus(reportId, "INVESTIGATING", adminNotes, processedBy);
    }

    @Transactional
    public ShopReportDTO resolveReport(Long reportId, String adminNotes, String processedBy) {
        return updateReportStatus(reportId, "RESOLVED", adminNotes, processedBy);
    }

    @Transactional
    public void takeActionOnReportedShop(Long reportId, String action, String reason) {
        ShopReport report = shopReportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Shop report not found with id: " + reportId));

        Shop shop = report.getShop();

        switch (action.toUpperCase()) {
            case "SUSPEND":
                shop.setActive(false);
                shop.setOnline(false);
                break;
            case "WARN":
                // Send warning notification to shop owner
                break;
            case "VERIFY":
                shop.setVerified(true);
                break;
            case "UNVERIFY":
                shop.setVerified(false);
                break;
            default:
                throw new RuntimeException("Invalid action: " + action);
        }

        shopRepository.save(shop);

        // Update report status
        report.setStatus("ACTION_TAKEN");
        report.setAdminNotes("Action taken: " + action + ". Reason: " + reason);
        report.setResolvedAt(LocalDateTime.now());
        shopReportRepository.save(report);
    }

    public List<Object[]> getReportStatisticsByType() {
        return shopReportRepository.countReportsByType();
    }

    public long getPendingReportsCount() {
        return shopReportRepository.countByStatus("PENDING");
    }

    private ShopReportDTO mapToShopReportDTO(ShopReport report) {
        ShopReportDTO dto = new ShopReportDTO();
        dto.setId(report.getId());
        dto.setShopId(report.getShop().getId());
        dto.setReportedById(report.getReportedBy().getId());
        dto.setReportType(report.getReportType());
        dto.setDescription(report.getDescription());
        dto.setStatus(report.getStatus());
        dto.setAdminNotes(report.getAdminNotes());
        dto.setReportedAt(report.getReportedAt());
        dto.setResolvedAt(report.getResolvedAt());
        return dto;
    }
}