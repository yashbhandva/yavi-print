package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.SupportCategoryDTO;
import com.web_app.yaviPrint.entity.SupportCategory;
import com.web_app.yaviPrint.repository.SupportCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupportCategoryService {

    private final SupportCategoryRepository supportCategoryRepository;

    @Transactional
    public SupportCategoryDTO createCategory(SupportCategoryDTO categoryDTO) {
        if (supportCategoryRepository.findByName(categoryDTO.getName()).isPresent()) {
            throw new RuntimeException("Category with name '" + categoryDTO.getName() + "' already exists");
        }

        SupportCategory category = new SupportCategory();
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        category.setDepartment(categoryDTO.getDepartment() != null ? categoryDTO.getDepartment() : "GENERAL");
        category.setSlaHours(categoryDTO.getSlaHours() != null ? categoryDTO.getSlaHours() : 24);
        category.setActive(true);

        SupportCategory savedCategory = supportCategoryRepository.save(category);
        return mapToSupportCategoryDTO(savedCategory);
    }

    public SupportCategoryDTO getCategoryById(Long categoryId) {
        SupportCategory category = supportCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Support category not found with id: " + categoryId));
        return mapToSupportCategoryDTO(category);
    }

    public SupportCategoryDTO getCategoryByName(String name) {
        SupportCategory category = supportCategoryRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Support category not found with name: " + name));
        return mapToSupportCategoryDTO(category);
    }

    public List<SupportCategoryDTO> getCategoriesByDepartment(String department) {
        return supportCategoryRepository.findByDepartment(department).stream()
                .map(this::mapToSupportCategoryDTO)
                .collect(Collectors.toList());
    }

    public List<SupportCategoryDTO> getActiveCategories() {
        return supportCategoryRepository.findByIsActiveTrue().stream()
                .map(this::mapToSupportCategoryDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SupportCategoryDTO updateCategory(Long categoryId, SupportCategoryDTO categoryDTO) {
        SupportCategory category = supportCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Support category not found with id: " + categoryId));

        if (categoryDTO.getName() != null && !category.getName().equals(categoryDTO.getName())) {
            if (supportCategoryRepository.findByName(categoryDTO.getName()).isPresent()) {
                throw new RuntimeException("Category with name '" + categoryDTO.getName() + "' already exists");
            }
            category.setName(categoryDTO.getName());
        }

        if (categoryDTO.getDescription() != null) {
            category.setDescription(categoryDTO.getDescription());
        }

        if (categoryDTO.getDepartment() != null) {
            category.setDepartment(categoryDTO.getDepartment());
        }

        if (categoryDTO.getSlaHours() != null) {
            category.setSlaHours(categoryDTO.getSlaHours());
        }

        if (categoryDTO.getActive() != null) {
            category.setActive(categoryDTO.getActive());
        }

        SupportCategory updatedCategory = supportCategoryRepository.save(category);
        return mapToSupportCategoryDTO(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        SupportCategory category = supportCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Support category not found with id: " + categoryId));
        supportCategoryRepository.delete(category);
    }

    @Transactional
    public void deactivateCategory(Long categoryId) {
        SupportCategory category = supportCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Support category not found with id: " + categoryId));
        category.setActive(false);
        supportCategoryRepository.save(category);
    }

    public boolean isCategoryActive(String categoryName) {
        return supportCategoryRepository.findByName(categoryName)
                .map(SupportCategory::isActive)
                .orElse(false);
    }

    public Integer getCategorySla(String categoryName) {
        SupportCategory category = supportCategoryRepository.findByName(categoryName)
                .orElseThrow(() -> new RuntimeException("Support category not found with name: " + categoryName));
        return category.getSlaHours();
    }

    @Transactional
    public void initializeDefaultCategories() {
        createDefaultCategory("TECHNICAL_ISSUE", "Technical problems with the app or website", "TECHNICAL", 12);
        createDefaultCategory("PAYMENT_ISSUE", "Problems with payments or refunds", "BILLING", 6);
        createDefaultCategory("PRINT_QUALITY", "Issues with print quality or output", "QUALITY", 24);
        createDefaultCategory("ORDER_STATUS", "Questions about order status or tracking", "ORDER", 12);
        createDefaultCategory("ACCOUNT_ISSUE", "Problems with user account or login", "ACCOUNT", 6);
        createDefaultCategory("SHOP_REGISTRATION", "Questions about shop registration or verification", "SHOP", 48);
        createDefaultCategory("GENERAL_INQUIRY", "General questions or feedback", "GENERAL", 24);
    }

    private void createDefaultCategory(String name, String description, String department, Integer slaHours) {
        if (!supportCategoryRepository.findByName(name).isPresent()) {
            SupportCategory category = new SupportCategory();
            category.setName(name);
            category.setDescription(description);
            category.setDepartment(department);
            category.setSlaHours(slaHours);
            category.setActive(true);
            supportCategoryRepository.save(category);
        }
    }

    private SupportCategoryDTO mapToSupportCategoryDTO(SupportCategory category) {
        SupportCategoryDTO dto = new SupportCategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setDepartment(category.getDepartment());
        dto.setSlaHours(category.getSlaHours());
        dto.setActive(category.isActive());
        return dto;
    }
}