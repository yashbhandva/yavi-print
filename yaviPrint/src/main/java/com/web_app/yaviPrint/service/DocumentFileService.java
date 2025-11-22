package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.DocumentFileDTO;
import com.web_app.yaviPrint.entity.DocumentFile;
import com.web_app.yaviPrint.entity.User;
import com.web_app.yaviPrint.repository.DocumentFileRepository;
import com.web_app.yaviPrint.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentFileService {

    private final DocumentFileRepository documentFileRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public DocumentFileDTO uploadFile(MultipartFile file, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Validate file type
        String fileType = getFileType(file.getOriginalFilename());
        if (!isSupportedFileType(fileType)) {
            throw new RuntimeException("Unsupported file type: " + fileType);
        }

        // Upload file to storage
        String fileUrl = fileStorageService.storeFile(file);
        String fileHash = generateFileHash(file);

        DocumentFile documentFile = new DocumentFile();
        documentFile.setFileName(file.getOriginalFilename());
        documentFile.setFileUrl(fileUrl);
        documentFile.setFileType(fileType);
        documentFile.setFileSize(file.getSize());
        documentFile.setFileHash(fileHash);
        documentFile.setUploadedBy(user);
        documentFile.setUploadedAt(LocalDateTime.now());
        documentFile.setExpiresAt(LocalDateTime.now().plusDays(7)); // Files expire after 7 days

        DocumentFile savedFile = documentFileRepository.save(documentFile);
        return mapToDocumentFileDTO(savedFile);
    }

    public DocumentFileDTO getFileById(Long fileId) {
        DocumentFile file = documentFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found with id: " + fileId));
        return mapToDocumentFileDTO(file);
    }

    public List<DocumentFileDTO> getUserFiles(Long userId) {
        return documentFileRepository.findByUploadedById(userId).stream()
                .map(this::mapToDocumentFileDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteFile(Long fileId) {
        DocumentFile file = documentFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found with id: " + fileId));

        // Delete from storage
        fileStorageService.deleteFile(file.getFileUrl());

        documentFileRepository.delete(file);
    }

    @Transactional
    public void cleanupExpiredFiles() {
        LocalDateTime now = LocalDateTime.now();
        List<DocumentFile> expiredFiles = documentFileRepository.findExpiredFiles(now);

        for (DocumentFile file : expiredFiles) {
            fileStorageService.deleteFile(file.getFileUrl());
            documentFileRepository.delete(file);
        }
    }

    private String getFileType(String fileName) {
        if (fileName == null) return "UNKNOWN";
        int dotIndex = fileName.lastIndexOf(".");
        return dotIndex == -1 ? "UNKNOWN" : fileName.substring(dotIndex + 1).toUpperCase();
    }

    private boolean isSupportedFileType(String fileType) {
        return List.of("PDF", "DOC", "DOCX", "JPG", "JPEG", "PNG").contains(fileType);
    }

    private String generateFileHash(MultipartFile file) {
        // Simple hash generation for demo - in production use proper hashing
        return UUID.randomUUID().toString() + "-" + file.getSize();
    }

    private DocumentFileDTO mapToDocumentFileDTO(DocumentFile file) {
        DocumentFileDTO dto = new DocumentFileDTO();
        dto.setId(file.getId());
        dto.setFileName(file.getFileName());
        dto.setFileUrl(file.getFileUrl());
        dto.setFileType(file.getFileType());
        dto.setFileSize(file.getFileSize());
        dto.setFileHash(file.getFileHash());
        dto.setUploadedById(file.getUploadedBy().getId());
        dto.setUploadedAt(file.getUploadedAt());
        dto.setExpiresAt(file.getExpiresAt());
        dto.setCompressed(file.isCompressed());
        return dto;
    }
}