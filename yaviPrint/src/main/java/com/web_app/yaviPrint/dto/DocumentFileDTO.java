package com.web_app.yaviPrint.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DocumentFileDTO {
    private Long id;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private Integer totalPages;
    private String fileHash;
    private Long uploadedById;
    private LocalDateTime uploadedAt;
    private LocalDateTime expiresAt;
    private Boolean isCompressed;

    public Boolean getCompressed() {
        return isCompressed;
    }

    public void setCompressed(Boolean compressed) {
        isCompressed = compressed;
    }
}