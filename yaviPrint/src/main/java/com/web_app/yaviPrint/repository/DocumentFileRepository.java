package com.web_app.yaviPrint.repository;

import com.web_app.yaviPrint.entity.DocumentFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentFileRepository extends JpaRepository<DocumentFile, Long> {
    List<DocumentFile> findByUploadedById(Long userId);

    @Query("SELECT f FROM DocumentFile f WHERE f.expiresAt < :currentTime")
    List<DocumentFile> findExpiredFiles(LocalDateTime currentTime);

    Optional<DocumentFile> findByFileHash(String fileHash);

    @Query("SELECT SUM(f.fileSize) FROM DocumentFile f WHERE f.uploadedById = :userId")
    Long getTotalStorageUsedByUser(Long userId);
}