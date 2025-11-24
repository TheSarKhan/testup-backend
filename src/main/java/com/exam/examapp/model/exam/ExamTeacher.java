package com.exam.examapp.model.exam;


import com.exam.examapp.model.User;
import com.exam.examapp.model.subject.Subject;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "exam_teachers")
public class ExamTeacher {
    @Id
    UUID id;

    @ManyToOne
    Exam exam;

    @ManyToOne
    User teacher;

    @ManyToMany(fetch = FetchType.EAGER)
    List<Subject> subject;

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
