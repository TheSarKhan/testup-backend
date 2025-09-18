package com.exam.examapp.model.subject;

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
@Table(name = "subjects")
public class Subject {
    @Id
    private UUID id;

    @Column(unique = true, nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(nullable = false)
    private String logoUrl;

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
