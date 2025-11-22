package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.service.DocumentFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final DocumentFileService documentFileService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) {
        try {
            // Get current user ID from authentication
            Long userId = 1L; // Placeholder - get from authenticated user

            var uploadedFile = documentFileService.uploadFile(file, userId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "File uploaded successfully",
                    "data", uploadedFile
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/my-files")
    public ResponseEntity<?> getUserFiles(Authentication authentication) {
        try {
            Long userId = 1L; // Placeholder - get from authenticated user
            var files = documentFileService.getUserFiles(userId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", files
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<?> getFileById(@PathVariable Long fileId) {
        try {
            var file = documentFileService.getFileById(fileId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", file
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable Long fileId) {
        try {
            documentFileService.deleteFile(fileId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "File deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}