package com.web_app.yaviPrint.repository;

import com.web_app.yaviPrint.entity.NotificationPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationPreferencesRepository extends JpaRepository<NotificationPreferences, Long> {
    Optional<NotificationPreferences> findByUserId(Long userId);
    List<NotificationPreferences> findByEmailNotificationsTrue();
    List<NotificationPreferences> findByPushNotificationsTrue();
}