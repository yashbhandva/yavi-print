package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.UserReportDTO;
import com.web_app.yaviPrint.entity.User;
import com.web_app.yaviPrint.entity.UserReport;
import com.web_app.yaviPrint.repository.UserRepository;
import com.web_app.yaviPrint.repository.UserReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserReportService {

    private final UserReportRepository userReportRepository;
    private final UserRepository userRepository;

    @Transactional
    public UserReportDTO createUserReport(Long reportedUserId, Long reportedById, UserReportDTO reportDTO) {
        User reportedUser = userRepository.findById(reportedUserId)
                .orElseThrow(() -> new RuntimeException("Reported user not found with id: " + reportedUserId));

        User reportedBy = userRepository.findById(reportedById)
                .orElseThrow(() -> new RuntimeException("Reporting user not found with id: " + reportedById));

        // Prevent self-reporting
        if (reportedUserId.equals(reportedById)) {
            throw new RuntimeException("You cannot report yourself");
        }

        UserReport report = new UserReport();
        report.setReportedUser(reportedUser);
        report.setReportedBy(reportedBy);
        report.setReportType(reportDTO.getReportType());
        report.setDescription(reportDTO.getDescription());
        report.setStatus("PENDING");
        report.setReportedAt(LocalDateTime.now());

        UserReport savedReport = userReportRepository.save(report);
        return mapToUserReportDTO(savedReport);
    }

    public List<UserReportDTO> getUserReportsByReportedUser(Long reportedUserId) {
        return userReportRepository.findByReportedUserId(reportedUserId).stream()
                .map(this::mapToUserReportDTO)
                .collect(Collectors.toList());
    }

    public List<UserReportDTO> getUserReportsByReporter(Long reportedById) {
        return userReportRepository.findByReportedById(reportedById).stream()
                .map(this::mapToUserReportDTO)
                .collect(Collectors.toList());
    }

    public List<UserReportDTO> getPendingUserReports() {
        return userReportRepository.findPendingUserReports().stream()
                .map(this::mapToUserReportDTO)
                .collect(Collectors.toList());
    }

    public List<UserReportDTO> getUserReportsByStatus(String status) {
        return userReportRepository.findByStatus(status).stream()
                .map(this::mapToUserReportDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserReportDTO updateUserReportStatus(Long reportId, String status, String adminNotes) {
        UserReport report = userReportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("User report not found with id: " + reportId));

        report.setStatus(status);
        // In a real implementation, you might want to add admin notes field

        UserReport updatedReport = userReportRepository.save(report);
        return mapToUserReportDTO(updatedReport);
    }

    @Transactional
    public void takeActionOnReportedUser(Long reportId, String action, String reason) {
        UserReport report = userReportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("User report not found with id: " + reportId));

        User reportedUser = report.getReportedUser();

        switch (action.toUpperCase()) {
            case "SUSPEND":
                reportedUser.setAccountNonLocked(false);
                break;
            case "WARN":
                // Send warning notification to user
                break;
            case "RESTRICT":
                // Restrict user capabilities
                break;
            default:
                throw new RuntimeException("Invalid action: " + action);
        }

        userRepository.save(reportedUser);

        // Update report status
        report.setStatus("ACTION_TAKEN");
        userReportRepository.save(report);
    }

    public List<Object[]> getUserReportStatisticsByType() {
        return userReportRepository.countUserReportsByType();
    }

    public long getPendingUserReportsCount() {
        return userReportRepository.countByStatus("PENDING");
    }

    private UserReportDTO mapToUserReportDTO(UserReport report) {
        UserReportDTO dto = new UserReportDTO();
        dto.setId(report.getId());
        dto.setReportedUserId(report.getReportedUser().getId());
        dto.setReportedById(report.getReportedBy().getId());
        dto.setReportType(report.getReportType());
        dto.setDescription(report.getDescription());
        dto.setStatus(report.getStatus());
        dto.setReportedAt(report.getReportedAt());
        return dto;
    }
}