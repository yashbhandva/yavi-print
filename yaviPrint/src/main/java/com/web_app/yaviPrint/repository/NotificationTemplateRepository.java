package com.web_app.yaviPrint.repository;

import com.web_app.yaviPrint.entity.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {
    Optional<NotificationTemplate> findByTemplateName(String templateName);
    List<NotificationTemplate> findByTemplateType(String templateType);
    List<NotificationTemplate> findByIsActiveTrue();
    List<NotificationTemplate> findByLanguageAndIsActiveTrue(String language);
}