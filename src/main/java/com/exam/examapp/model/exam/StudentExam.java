package com.exam.examapp.model.exam;

import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.AnswerStatus;
import com.exam.examapp.model.enums.ExamStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "students_exams")
public class StudentExam {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User student;

    private String studentName;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Exam exam;

    @Enumerated(EnumType.STRING)
    private ExamStatus status;

    private double score;

    private int numberOfQuestions;

    private int numberOfCorrectAnswers;

    private int numberOfWrongAnswers;

    private int numberOfNotAnsweredQuestions;

    private int numberOfNotCheckedYetQuestions;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<UUID, String> questionIdToAnswerMap;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<UUID, Boolean> questionIdToIsAnswerPictureMap;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<UUID, AnswerStatus> questionIdToAnswerStatusMap;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<UUID, Integer> listeningIdToPlayTimeMap;

    private Instant startTime;

    private Instant endTime;

    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        id = UUID.randomUUID();
        status = ExamStatus.ACTIVE;
        createdAt = updatedAt = Instant.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}
