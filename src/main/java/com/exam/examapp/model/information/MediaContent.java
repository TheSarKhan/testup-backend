package com.exam.examapp.model.information;

import com.exam.examapp.model.enums.PageType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "media_contents")
public class MediaContent {
    @Id
    private UUID id;

    @Column(unique = true, nullable = false, columnDefinition = "TEXT")
    private String text;

    private String pictureUrl;

    private String author;

    @Column(nullable = false)
    private String backgroundColor;

    @Column(nullable = false)
    private String textColor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PageType pageType;

    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        id = UUID.randomUUID();
        createdAt = updatedAt = Instant.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}
