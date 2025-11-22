package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.AdminActionLogDTO;
import com.web_app.yaviPrint.entity.AdminActionLog;
import com.web_app.yaviPrint.entity.AdminUser;
import com.web_app.yaviPrint.repository.AdminActionLogRepository;
import com.web_app.yaviPrint.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminActionLogService {

    private final AdminActionLogRepository adminActionLogRepository;
    private final AdminUserRepository adminUserRepository;

    @Transactional
    public AdminActionLogDTO logAction(Long adminUserId, String action, String description,
                                       String targetEntity, Long targetId, String ipAddress,
                                       String oldValues, String newValues) {
        AdminUser adminUser = adminUserRepository.findById(adminUserId)
                .orElseThrow(() -> new RuntimeException("Admin user not found with id: " + adminUserId));

        AdminActionLog actionLog = new AdminActionLog();
        actionLog.setAdminUser(adminUser);
        actionLog.setAction(action);
        actionLog.setDescription(description);
        actionLog.setTargetEntity(targetEntity);
        actionLog.setTargetId(targetId);
        actionLog.setIpAddress(ipAddress);
        actionLog.setActionTime(LocalDateTime.now());
        actionLog.setOldValues(oldValues);
        actionLog.setNewValues(newValues);

        AdminActionLog savedLog = adminActionLogRepository.save(actionLog);
        return mapToAdminActionLogDTO(savedLog);
    }

    public List<AdminActionLogDTO> getAdminActionLogs(Long adminUserId) {
        return adminActionLogRepository.findByAdminUserIdOrderByActionTimeDesc(adminUserId).stream()
                .map(this::mapToAdminActionLogDTO)
                .collect(Collectors.toList());
    }

    public List<AdminActionLogDTO> getActionLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return adminActionLogRepository.findByActionTimeBetween(startDate, endDate).stream()
                .map(this::mapToAdminActionLogDTO)
                .collect(Collectors.toList());
    }

    public List<AdminActionLogDTO> getActionLogsByEntity(String targetEntity, Long targetId) {
        return adminActionLogRepository.findByTargetEntityAndTargetId(targetEntity, targetId).stream()
                .map(this::mapToAdminActionLogDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void logUserManagementAction(Long adminUserId, String action, Long targetUserId,
                                        String ipAddress, String oldValues, String newValues) {
        logAction(adminUserId, action, "User management action", "USER", targetUserId,
                ipAddress, oldValues, newValues);
    }

    @Transactional
    public void logShopManagementAction(Long adminUserId, String action, Long targetShopId,
                                        String ipAddress, String oldValues, String newValues) {
        logAction(adminUserId, action, "Shop management action", "SHOP", targetShopId,
                ipAddress, oldValues, newValues);
    }

    @Transactional
    public void logOrderManagementAction(Long adminUserId, String action, Long targetOrderId,
                                         String ipAddress, String oldValues, String newValues) {
        logAction(adminUserId, action, "Order management action", "ORDER", targetOrderId,
                ipAddress, oldValues, newValues);
    }

    @Transactional
    public void logPaymentManagementAction(Long adminUserId, String action, Long targetPaymentId,
                                           String ipAddress, String oldValues, String newValues) {
        logAction(adminUserId, action, "Payment management action", "PAYMENT", targetPaymentId,
                ipAddress, oldValues, newValues);
    }

    public List<Object[]> getActionStatisticsByEntity() {
        return adminActionLogRepository.countActionsByEntityType();
    }

    private AdminActionLogDTO mapToAdminActionLogDTO(AdminActionLog actionLog) {
        AdminActionLogDTO dto = new AdminActionLogDTO();
        dto.setId(actionLog.getId());
        dto.setAdminUserId(actionLog.getAdminUser().getId());
        dto.setAction(actionLog.getAction());
        dto.setDescription(actionLog.getDescription());
        dto.setTargetEntity(actionLog.getTargetEntity());
        dto.setTargetId(actionLog.getTargetId());
        dto.setIpAddress(actionLog.getIpAddress());
        dto.setActionTime(actionLog.getActionTime());
        dto.setOldValues(actionLog.getOldValues());
        dto.setNewValues(actionLog.getNewValues());
        return dto;
    }
}