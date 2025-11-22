package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.NotificationPreferencesDTO;
import com.web_app.yaviPrint.entity.NotificationPreferences;
import com.web_app.yaviPrint.entity.User;
import com.web_app.yaviPrint.repository.NotificationPreferencesRepository;
import com.web_app.yaviPrint.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationPreferencesService {

    private final NotificationPreferencesRepository notificationPreferencesRepository;
    private final UserRepository userRepository;

    @Transactional
    public NotificationPreferencesDTO createOrUpdatePreferences(Long userId, NotificationPreferencesDTO preferencesDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        NotificationPreferences preferences = notificationPreferencesRepository.findByUserId(userId)
                .orElse(new NotificationPreferences());

        preferences.setUser(user);
        preferences.setEmailNotifications(preferencesDTO.getEmailNotifications() != null ? preferencesDTO.getEmailNotifications() : true);
        preferences.setSmsNotifications(preferencesDTO.getSmsNotifications() != null ? preferencesDTO.getSmsNotifications() : false);
        preferences.setPushNotifications(preferencesDTO.getPushNotifications() != null ? preferencesDTO.getPushNotifications() : true);
        preferences.setOrderUpdates(preferencesDTO.getOrderUpdates() != null ? preferencesDTO.getOrderUpdates() : true);
        preferences.setPromotional(preferencesDTO.getPromotional() != null ? preferencesDTO.getPromotional() : false);
        preferences.setPriceAlerts(preferencesDTO.getPriceAlerts() != null ? preferencesDTO.getPriceAlerts() : true);
        preferences.setShopUpdates(preferencesDTO.getShopUpdates() != null ? preferencesDTO.getShopUpdates() : true);

        NotificationPreferences savedPreferences = notificationPreferencesRepository.save(preferences);
        return mapToNotificationPreferencesDTO(savedPreferences);
    }

    public NotificationPreferencesDTO getUserPreferences(Long userId) {
        NotificationPreferences preferences = notificationPreferencesRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultPreferences(userId));
        return mapToNotificationPreferencesDTO(preferences);
    }

    @Transactional
    public NotificationPreferencesDTO updatePreference(Long userId, String preferenceType, Boolean value) {
        NotificationPreferences preferences = notificationPreferencesRepository.findByUserId(userId)
                .orElse(createDefaultPreferences(userId));

        switch (preferenceType.toUpperCase()) {
            case "EMAIL":
                preferences.setEmailNotifications(value);
                break;
            case "SMS":
                preferences.setSmsNotifications(value);
                break;
            case "PUSH":
                preferences.setPushNotifications(value);
                break;
            case "ORDER_UPDATES":
                preferences.setOrderUpdates(value);
                break;
            case "PROMOTIONAL":
                preferences.setPromotional(value);
                break;
            case "PRICE_ALERTS":
                preferences.setPriceAlerts(value);
                break;
            case "SHOP_UPDATES":
                preferences.setShopUpdates(value);
                break;
            default:
                throw new RuntimeException("Invalid preference type: " + preferenceType);
        }

        NotificationPreferences updatedPreferences = notificationPreferencesRepository.save(preferences);
        return mapToNotificationPreferencesDTO(updatedPreferences);
    }

    public boolean isNotificationEnabled(Long userId, String notificationType) {
        NotificationPreferences preferences = notificationPreferencesRepository.findByUserId(userId)
                .orElse(createDefaultPreferences(userId));

        switch (notificationType.toUpperCase()) {
            case "ORDER_UPDATE":
                return preferences.isOrderUpdates();
            case "PROMOTIONAL":
                return preferences.isPromotional();
            case "PRICE_ALERT":
                return preferences.isPriceAlerts();
            case "SHOP_UPDATE":
                return preferences.isShopUpdates();
            default:
                return true; // Default enabled for unknown types
        }
    }

    private NotificationPreferences createDefaultPreferences(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        NotificationPreferences preferences = new NotificationPreferences();
        preferences.setUser(user);
        preferences.setEmailNotifications(true);
        preferences.setSmsNotifications(false);
        preferences.setPushNotifications(true);
        preferences.setOrderUpdates(true);
        preferences.setPromotional(false);
        preferences.setPriceAlerts(true);
        preferences.setShopUpdates(true);

        return notificationPreferencesRepository.save(preferences);
    }

    private NotificationPreferencesDTO mapToNotificationPreferencesDTO(NotificationPreferences preferences) {
        NotificationPreferencesDTO dto = new NotificationPreferencesDTO();
        dto.setId(preferences.getId());
        dto.setUserId(preferences.getUser().getId());
        dto.setEmailNotifications(preferences.isEmailNotifications());
        dto.setSmsNotifications(preferences.isSmsNotifications());
        dto.setPushNotifications(preferences.isPushNotifications());
        dto.setOrderUpdates(preferences.isOrderUpdates());
        dto.setPromotional(preferences.isPromotional());
        dto.setPriceAlerts(preferences.isPriceAlerts());
        dto.setShopUpdates(preferences.isShopUpdates());
        return dto;
    }
}