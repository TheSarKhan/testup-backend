package com.exam.examapp.model.exam;

import com.exam.examapp.model.subject.SubjectStructure;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "exams")
public class ExamTemplate {
    @Id
    private UUID id;

    @ManyToOne
    private Submodule submodule;

    @OneToOne
    private SubjectStructure subjectStructure;

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
