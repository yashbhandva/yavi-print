package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.NotificationTemplateDTO;
import com.web_app.yaviPrint.entity.NotificationTemplate;
import com.web_app.yaviPrint.repository.NotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationTemplateService {

    private final NotificationTemplateRepository notificationTemplateRepository;

    @Transactional
    public NotificationTemplateDTO createTemplate(NotificationTemplateDTO templateDTO) {
        if (notificationTemplateRepository.findByTemplateName(templateDTO.getTemplateName()).isPresent()) {
            throw new RuntimeException("Template with name '" + templateDTO.getTemplateName() + "' already exists");
        }

        NotificationTemplate template = new NotificationTemplate();
        template.setTemplateName(templateDTO.getTemplateName());
        template.setTemplateType(templateDTO.getTemplateType());
        template.setSubject(templateDTO.getSubject());
        template.setContent(templateDTO.getContent());
        template.setVariables(templateDTO.getVariables());
        template.setActive(true);
        template.setLanguage(templateDTO.getLanguage() != null ? templateDTO.getLanguage() : "en");

        NotificationTemplate savedTemplate = notificationTemplateRepository.save(template);
        return mapToNotificationTemplateDTO(savedTemplate);
    }

    public NotificationTemplateDTO getTemplateById(Long templateId) {
        NotificationTemplate template = notificationTemplateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found with id: " + templateId));
        return mapToNotificationTemplateDTO(template);
    }

    public NotificationTemplateDTO getTemplateByName(String templateName) {
        NotificationTemplate template = notificationTemplateRepository.findByTemplateName(templateName)
                .orElseThrow(() -> new RuntimeException("Template not found with name: " + templateName));
        return mapToNotificationTemplateDTO(template);
    }

    public List<NotificationTemplateDTO> getTemplatesByType(String templateType) {
        return notificationTemplateRepository.findByTemplateType(templateType).stream()
                .map(this::mapToNotificationTemplateDTO)
                .collect(Collectors.toList());
    }

    public List<NotificationTemplateDTO> getActiveTemplates() {
        return notificationTemplateRepository.findByIsActiveTrue().stream()
                .map(this::mapToNotificationTemplateDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public NotificationTemplateDTO updateTemplate(Long templateId, NotificationTemplateDTO templateDTO) {
        NotificationTemplate template = notificationTemplateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found with id: " + templateId));

        if (templateDTO.getTemplateName() != null) {
            template.setTemplateName(templateDTO.getTemplateName());
        }
        if (templateDTO.getTemplateType() != null) {
            template.setTemplateType(templateDTO.getTemplateType());
        }
        if (templateDTO.getSubject() != null) {
            template.setSubject(templateDTO.getSubject());
        }
        if (templateDTO.getContent() != null) {
            template.setContent(templateDTO.getContent());
        }
        if (templateDTO.getVariables() != null) {
            template.setVariables(templateDTO.getVariables());
        }
        if (templateDTO.getActive() != null) {
            template.setActive(templateDTO.getActive());
        }
        if (templateDTO.getLanguage() != null) {
            template.setLanguage(templateDTO.getLanguage());
        }

        NotificationTemplate updatedTemplate = notificationTemplateRepository.save(template);
        return mapToNotificationTemplateDTO(updatedTemplate);
    }

    @Transactional
    public void deleteTemplate(Long templateId) {
        NotificationTemplate template = notificationTemplateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found with id: " + templateId));
        notificationTemplateRepository.delete(template);
    }

    public String renderTemplate(String templateName, Object variables) {
        NotificationTemplate template = notificationTemplateRepository.findByTemplateName(templateName)
                .orElseThrow(() -> new RuntimeException("Template not found with name: " + templateName));

        if (!template.isActive()) {
            throw new RuntimeException("Template is not active: " + templateName);
        }

        String content = template.getContent();
        // Simple variable replacement - in production use a proper template engine
        if (variables instanceof java.util.Map) {
            java.util.Map<String, Object> varMap = (java.util.Map<String, Object>) variables;
            for (java.util.Map.Entry<String, Object> entry : varMap.entrySet()) {
                String placeholder = "{{" + entry.getKey() + "}}";
                String value = entry.getValue() != null ? entry.getValue().toString() : "";
                content = content.replace(placeholder, value);
            }
        }

        return content;
    }

    private NotificationTemplateDTO mapToNotificationTemplateDTO(NotificationTemplate template) {
        NotificationTemplateDTO dto = new NotificationTemplateDTO();
        dto.setId(template.getId());
        dto.setTemplateName(template.getTemplateName());
        dto.setTemplateType(template.getTemplateType());
        dto.setSubject(template.getSubject());
        dto.setContent(template.getContent());
        dto.setVariables(template.getVariables());
        dto.setActive(template.isActive());
        dto.setLanguage(template.getLanguage());
        return dto;
    }
}