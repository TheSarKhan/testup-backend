package com.exam.examapp.model.question;

import com.exam.examapp.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "question_storages")
public class QuestionStorage {
    @Id
    private UUID id;

    @ManyToOne
    private User teacher;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Question> questions;

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
