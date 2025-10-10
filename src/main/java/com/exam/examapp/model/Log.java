package com.exam.examapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "logs")
public class Log {
    @Id
    private UUID id;

    @ManyToOne
    private User user;

    private String message;

    private Instant deletedAt;

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
