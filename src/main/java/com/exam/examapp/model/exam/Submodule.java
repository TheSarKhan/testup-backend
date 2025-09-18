package com.exam.examapp.model.exam;

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
@Table(name = "sub_modules")
public class Submodule {
    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToOne
    private Module module;

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
