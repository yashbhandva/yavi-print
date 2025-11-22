package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.SystemSettingsDTO;
import com.web_app.yaviPrint.entity.SystemSettings;
import com.web_app.yaviPrint.repository.SystemSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemSettingsService {

    private final SystemSettingsRepository systemSettingsRepository;

    @Transactional
    public SystemSettingsDTO createOrUpdateSetting(SystemSettingsDTO settingDTO) {
        SystemSettings setting = systemSettingsRepository.findBySettingKey(settingDTO.getSettingKey())
                .orElse(new SystemSettings());

        setting.setSettingKey(settingDTO.getSettingKey());
        setting.setSettingValue(settingDTO.getSettingValue());
        setting.setDataType(settingDTO.getDataType());
        setting.setCategory(settingDTO.getCategory());
        setting.setDescription(settingDTO.getDescription());
        setting.setEditable(settingDTO.getEditable() != null ? settingDTO.getEditable() : true);

        SystemSettings savedSetting = systemSettingsRepository.save(setting);
        return mapToSystemSettingsDTO(savedSetting);
    }

    public SystemSettingsDTO getSettingByKey(String settingKey) {
        SystemSettings setting = systemSettingsRepository.findBySettingKey(settingKey)
                .orElseThrow(() -> new RuntimeException("Setting not found with key: " + settingKey));
        return mapToSystemSettingsDTO(setting);
    }

    public List<SystemSettingsDTO> getSettingsByCategory(String category) {
        return systemSettingsRepository.findByCategory(category).stream()
                .map(this::mapToSystemSettingsDTO)
                .collect(Collectors.toList());
    }

    public List<SystemSettingsDTO> getEditableSettings() {
        return systemSettingsRepository.findByIsEditableTrue().stream()
                .map(this::mapToSystemSettingsDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SystemSettingsDTO updateSettingValue(String settingKey, String settingValue) {
        SystemSettings setting = systemSettingsRepository.findBySettingKey(settingKey)
                .orElseThrow(() -> new RuntimeException("Setting not found with key: " + settingKey));

        if (!setting.isEditable()) {
            throw new RuntimeException("Setting is not editable: " + settingKey);
        }

        setting.setSettingValue(settingValue);
        SystemSettings updatedSetting = systemSettingsRepository.save(setting);
        return mapToSystemSettingsDTO(updatedSetting);
    }

    public String getSettingValue(String settingKey) {
        SystemSettings setting = systemSettingsRepository.findBySettingKey(settingKey)
                .orElseThrow(() -> new RuntimeException("Setting not found with key: " + settingKey));
        return setting.getSettingValue();
    }

    public Integer getSettingValueAsInteger(String settingKey) {
        String value = getSettingValue(settingKey);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Setting value is not an integer: " + settingKey);
        }
    }

    public Boolean getSettingValueAsBoolean(String settingKey) {
        String value = getSettingValue(settingKey);
        return Boolean.parseBoolean(value);
    }

    public Double getSettingValueAsDouble(String settingKey) {
        String value = getSettingValue(settingKey);
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Setting value is not a double: " + settingKey);
        }
    }

    @Transactional
    public void initializeDefaultSettings() {
        createDefaultSetting("app.name", "YaviPrint", "STRING", "GENERAL", "Application name", false);
        createDefaultSetting("app.version", "1.0.0", "STRING", "GENERAL", "Application version", false);
        createDefaultSetting("app.maintenance", "false", "BOOLEAN", "GENERAL", "Maintenance mode", true);

        createDefaultSetting("payment.platform.fee", "0.10", "NUMBER", "PAYMENT", "Platform commission fee", true);
        createDefaultSetting("payment.currency", "INR", "STRING", "PAYMENT", "Default currency", false);

        createDefaultSetting("notification.email.enabled", "true", "BOOLEAN", "NOTIFICATION", "Email notifications enabled", true);
        createDefaultSetting("notification.sms.enabled", "false", "BOOLEAN", "NOTIFICATION", "SMS notifications enabled", true);

        createDefaultSetting("file.max.size", "10485760", "NUMBER", "FILE", "Maximum file size in bytes", true);
        createDefaultSetting("file.allowed.types", "PDF,DOC,DOCX,JPG,JPEG,PNG", "STRING", "FILE", "Allowed file types", true);

        createDefaultSetting("order.pickup.expiry.hours", "72", "NUMBER", "ORDER", "Pickup code expiry in hours", true);
        createDefaultSetting("order.auto.cancel.hours", "24", "NUMBER", "ORDER", "Auto cancel pending orders after hours", true);
    }

    @Transactional
    public void setMaintenanceMode(boolean enabled) {
        updateSettingValue("app.maintenance", String.valueOf(enabled));
    }

    public boolean isMaintenanceMode() {
        return getSettingValueAsBoolean("app.maintenance");
    }

    public Double getPlatformFee() {
        return getSettingValueAsDouble("payment.platform.fee");
    }

    public Integer getMaxFileSize() {
        return getSettingValueAsInteger("file.max.size");
    }

    public Integer getPickupExpiryHours() {
        return getSettingValueAsInteger("order.pickup.expiry.hours");
    }

    private void createDefaultSetting(String key, String value, String dataType, String category, String description, boolean editable) {
        if (!systemSettingsRepository.existsBySettingKey(key)) {
            SystemSettings setting = new SystemSettings();
            setting.setSettingKey(key);
            setting.setSettingValue(value);
            setting.setDataType(dataType);
            setting.setCategory(category);
            setting.setDescription(description);
            setting.setEditable(editable);
            systemSettingsRepository.save(setting);
        }
    }

    private SystemSettingsDTO mapToSystemSettingsDTO(SystemSettings setting) {
        SystemSettingsDTO dto = new SystemSettingsDTO();
        dto.setId(setting.getId());
        dto.setSettingKey(setting.getSettingKey());
        dto.setSettingValue(setting.getSettingValue());
        dto.setDataType(setting.getDataType());
        dto.setCategory(setting.getCategory());
        dto.setDescription(setting.getDescription());
        dto.setEditable(setting.isEditable());
        return dto;
    }
}