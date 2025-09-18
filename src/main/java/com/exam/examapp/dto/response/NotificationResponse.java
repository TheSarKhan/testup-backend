package com.exam.examapp.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record NotificationResponse(
        UUID id,
        String title,
        String messages,
        Boolean isRead,
        String toEmail,
        Instant createAt,
        Instant updatedAt
) {
    public NotificationResponse(UUID id,
                                String title,
                                String messages,
                                Boolean isRead,
                                String toEmail,
                                Instant createAt,
                                Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.messages = messages;
        this.isRead = isRead;
        this.toEmail = toEmail;
        this.createAt = createAt;
        this.updatedAt = updatedAt;
    }

    public NotificationResponse(UUID id,
                                String title,
                                String messages,
                                Boolean isRead,
                                Instant createAt,
                                Instant updatedAt) {
        this(id, title, messages, isRead, null, createAt, updatedAt);
    }
}
