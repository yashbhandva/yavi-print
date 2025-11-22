package com.web_app.yaviPrint.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewReplyDTO {
    private Long id;
    private Long reviewId;
    private Long shopOwnerId;
    private String replyMessage;
    private LocalDateTime replyDate;
    private Boolean isEdited;
    private LocalDateTime editedAt;
    private UserResponseDTO shopOwner;

    public Boolean getEdited() {
        return isEdited;
    }

    public void setEdited(Boolean edited) {
        isEdited = edited;
    }
}