package com.exam.examapp.model.subject;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "topics")
public class Topic {
    @Id
    private UUID id;

    @Column(unique = true, nullable = false, columnDefinition = "TEXT")
    private String name;

    @ManyToOne
    private Subject subject;

    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        id = UUID.randomUUID();
        createdAt = updatedAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
