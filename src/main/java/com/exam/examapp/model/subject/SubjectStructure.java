package com.exam.examapp.model.subject;

import com.exam.examapp.model.enums.QuestionType;
import com.exam.examapp.model.exam.Submodule;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "subject_structures")
public class SubjectStructure {
    @Id
    private UUID id;

    @ManyToOne
    private Subject subject;

    @ManyToOne
    private Submodule submodule;

    private boolean isFree;

    private int questionCount;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<QuestionType, Integer> questionTypeCountMap;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<QuestionType, Map<Integer, Map<QuestionType, List<Integer>>>> textListeningQuestionToCountMap;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<Integer, Integer> questionToPointMap;

    @Column(columnDefinition = "TEXT")
    private String formula;

    private boolean isActive;

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