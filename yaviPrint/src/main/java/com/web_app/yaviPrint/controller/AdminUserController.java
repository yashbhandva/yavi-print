package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.dto.AdminUserDTO;
import com.web_app.yaviPrint.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @PostMapping
    public ResponseEntity<?> createAdminUser(@Valid @RequestBody AdminUserDTO adminUserDTO) {
        try {
            var adminUser = adminUserService.createAdminUser(adminUserDTO);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Admin user created successfully",
                    "data", adminUser
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllAdminUsers() {
        try {
            var adminUsers = adminUserService.getAllAdminUsers();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", adminUsers
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/{adminId}")
    public ResponseEntity<?> getAdminUserById(@PathVariable Long adminId) {
        try {
            var adminUser = adminUserService.getAdminUserById(adminId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", adminUser
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PutMapping("/{adminId}")
    public ResponseEntity<?> updateAdminUser(
            @PathVariable Long adminId,
            @Valid @RequestBody AdminUserDTO adminUserDTO) {
        try {
            var adminUser = adminUserService.updateAdminUser(adminId, adminUserDTO);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Admin user updated successfully",
                    "data", adminUser
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{adminId}")
    public ResponseEntity<?> deleteAdminUser(@PathVariable Long adminId) {
        try {
            adminUserService.deleteAdminUser(adminId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Admin user deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveAdminUsers() {
        try {
            var adminUsers = adminUserService.getActiveAdminUsers();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", adminUsers
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<?> getAdminUsersByRole(@PathVariable String role) {
        try {
            var adminUsers = adminUserService.getAdminUsersByRole(role);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", adminUsers
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}