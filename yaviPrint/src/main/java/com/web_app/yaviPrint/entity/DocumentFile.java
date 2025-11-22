package com.web_app.yaviPrint.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "document_files")
public class DocumentFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String fileUrl;
    private String fileType; // PDF, DOC, JPG, PNG
    private Long fileSize;
    private int totalPages;
    private String fileHash; // For duplicate detection

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User uploadedBy;

    private LocalDateTime uploadedAt;
    private LocalDateTime expiresAt; // Auto-delete after certain period
    private boolean isCompressed;

    // Getters and setters
}