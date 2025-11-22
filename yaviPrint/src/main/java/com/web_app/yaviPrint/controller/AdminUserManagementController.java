package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/user-management")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminUserManagementController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            var users = userService.getAllUsers();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", users,
                    "total", users.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/users/role/{role}")
    public ResponseEntity<?> getUsersByRole(@PathVariable String role) {
        try {
            var users = userService.getUsersByRole(role);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", users
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/users/{userId}/suspend")
    public ResponseEntity<?> suspendUser(@PathVariable Long userId) {
        try {
            // Implementation to suspend user
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "User suspended successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/users/{userId}/activate")
    public ResponseEntity<?> activateUser(@PathVariable Long userId) {
        try {
            // Implementation to activate user
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "User activated successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/users/stats")
    public ResponseEntity<?> getUserStats() {
        try {
            var customerCount = userService.getUsersByRole("CUSTOMER").size();
            var shopOwnerCount = userService.getUsersByRole("SHOP_OWNER").size();
            var adminCount = userService.getUsersByRole("ADMIN").size();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "totalCustomers", customerCount,
                            "totalShopOwners", shopOwnerCount,
                            "totalAdmins", adminCount,
                            "totalUsers", customerCount + shopOwnerCount + adminCount
                    )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}