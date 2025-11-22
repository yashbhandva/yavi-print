package com.web_app.yaviPrint.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SupportMessageDTO {
    private Long id;
    private Long ticketId;
    private Long senderId;
    private Boolean isFromAdmin;
    private String message;
    private String attachmentUrl;
    private LocalDateTime sentAt;
    private Boolean isRead;
    private LocalDateTime readAt;
    private UserResponseDTO sender;

    public Boolean getFromAdmin() {
        return isFromAdmin;
    }

    public void setFromAdmin(Boolean fromAdmin) {
        isFromAdmin = fromAdmin;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }
}