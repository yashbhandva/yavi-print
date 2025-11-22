package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.dto.UserUpdateDTO;
import com.web_app.yaviPrint.dto.UserProfileDTO;
import com.web_app.yaviPrint.service.UserService;
import com.web_app.yaviPrint.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserProfileService userProfileService;

    @GetMapping("/profile")
    public ResponseEntity<?> getCurrentUserProfile(Authentication authentication) {
        try {
            String email = authentication.getName();
            var user = userService.getUserByEmail(email);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", user
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(
            Authentication authentication,
            @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        try {
            String email = authentication.getName();
            var user = userService.getUserByEmail(email);
            var updatedUser = userService.updateUser(user.getId(), userUpdateDTO);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Profile updated successfully",
                    "data", updatedUser
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/profile/details")
    public ResponseEntity<?> getUserProfileDetails(Authentication authentication) {
        try {
            String email = authentication.getName();
            var user = userService.getUserByEmail(email);
            var profile = userProfileService.getUserProfile(user.getId());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", profile
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PutMapping("/profile/details")
    public ResponseEntity<?> updateUserProfileDetails(
            Authentication authentication,
            @Valid @RequestBody UserProfileDTO profileDTO) {
        try {
            String email = authentication.getName();
            var user = userService.getUserByEmail(email);
            var updatedProfile = userProfileService.createOrUpdateUserProfile(user.getId(), profileDTO);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Profile details updated successfully",
                    "data", updatedProfile
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        try {
            var user = userService.getUserById(userId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", user
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/profile")
    public ResponseEntity<?> deleteUserAccount(Authentication authentication) {
        try {
            String email = authentication.getName();
            var user = userService.getUserByEmail(email);
            userService.deleteUser(user.getId());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Account deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}