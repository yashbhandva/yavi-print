package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.AdminUserDTO;
import com.web_app.yaviPrint.entity.AdminUser;
import com.web_app.yaviPrint.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AdminUserDTO createAdminUser(AdminUserDTO adminUserDTO) {
        if (adminUserRepository.existsByUsername(adminUserDTO.getUsername())) {
            throw new RuntimeException("Username already exists: " + adminUserDTO.getUsername());
        }

        if (adminUserRepository.existsByEmail(adminUserDTO.getEmail())) {
            throw new RuntimeException("Email already exists: " + adminUserDTO.getEmail());
        }

        AdminUser adminUser = new AdminUser();
        adminUser.setUsername(adminUserDTO.getUsername());
        adminUser.setEmail(adminUserDTO.getEmail());
        adminUser.setPassword(passwordEncoder.encode(adminUserDTO.getPassword())); // ✅ NOW WORKS
        adminUser.setRole(adminUserDTO.getRole());
        adminUser.setActive(true);
        adminUser.setPermissions(adminUserDTO.getPermissions());

        AdminUser savedAdmin = adminUserRepository.save(adminUser);
        return mapToAdminUserDTO(savedAdmin);
    }
    public AdminUserDTO getAdminUserById(Long id) {
        AdminUser adminUser = adminUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin user not found with id: " + id));
        return mapToAdminUserDTO(adminUser);
    }

    public AdminUserDTO getAdminUserByUsername(String username) {
        AdminUser adminUser = adminUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Admin user not found with username: " + username));
        return mapToAdminUserDTO(adminUser);
    }

    public List<AdminUserDTO> getAllAdminUsers() {
        return adminUserRepository.findAll().stream()
                .map(this::mapToAdminUserDTO)
                .collect(Collectors.toList());
    }

    public List<AdminUserDTO> getAdminUsersByRole(String role) {
        return adminUserRepository.findByRole(role).stream()
                .map(this::mapToAdminUserDTO)
                .collect(Collectors.toList());
    }

    public List<AdminUserDTO> getActiveAdminUsers() {
        return adminUserRepository.findByIsActiveTrue().stream()
                .map(this::mapToAdminUserDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AdminUserDTO updateAdminUser(Long id, AdminUserDTO adminUserDTO) {
        AdminUser adminUser = adminUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin user not found with id: " + id));

        if (adminUserDTO.getEmail() != null && !adminUser.getEmail().equals(adminUserDTO.getEmail())) {
            if (adminUserRepository.existsByEmail(adminUserDTO.getEmail())) {
                throw new RuntimeException("Email already exists: " + adminUserDTO.getEmail());
            }
            adminUser.setEmail(adminUserDTO.getEmail());
        }

        if (adminUserDTO.getRole() != null) {
            adminUser.setRole(adminUserDTO.getRole());
        }

        if (adminUserDTO.getPassword() != null) {
            adminUser.setPassword(passwordEncoder.encode(adminUserDTO.getPassword()));
        }

        if (adminUserDTO.getActive() != null) {
            adminUser.setActive(adminUserDTO.getActive());
        }

        if (adminUserDTO.getPermissions() != null) {
            adminUser.setPermissions(adminUserDTO.getPermissions());
        }

        AdminUser updatedAdmin = adminUserRepository.save(adminUser);
        return mapToAdminUserDTO(updatedAdmin);
    }

    @Transactional
    public void deleteAdminUser(Long id) {
        AdminUser adminUser = adminUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin user not found with id: " + id));
        adminUserRepository.delete(adminUser);
    }

    @Transactional
    public void recordAdminLogin(String username) {
        AdminUser adminUser = adminUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Admin user not found with username: " + username));

        adminUser.setLastLogin(LocalDateTime.now());
        adminUserRepository.save(adminUser);
    }

    public boolean hasPermission(Long adminId, String permission) {
        AdminUser adminUser = adminUserRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin user not found with id: " + adminId));

        if (adminUser.getPermissions() == null) return false;

        // Simple permission check - in production use proper permission system
        return adminUser.getPermissions().contains(permission);
    }

    private AdminUserDTO mapToAdminUserDTO(AdminUser adminUser) {
        AdminUserDTO dto = new AdminUserDTO();
        dto.setId(adminUser.getId());
        dto.setUsername(adminUser.getUsername());
        dto.setEmail(adminUser.getEmail());
        dto.setRole(adminUser.getRole());
        dto.setActive(adminUser.isActive());
        dto.setLastLogin(adminUser.getLastLogin());
        dto.setPermissions(adminUser.getPermissions());
        return dto;
    }
}