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

    private Double examRating;

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

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Map<Integer, String>> subjectToQuestionToAnswer;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Map<Integer, AnswerStatus>> subjectToQuestionToAnswerStatus;

    private Instant startTime;

    private Instant endTime;

    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        id = UUID.randomUUID();
        status = ExamStatus.ACTIVE;
        examRating = 0.0;
        createdAt = updatedAt = Instant.now();
        questionIdToAnswerMap = Map.of();
        questionIdToIsAnswerPictureMap = Map.of();
        questionIdToAnswerStatusMap = Map.of();
        listeningIdToPlayTimeMap = Map.of();
        subjectToQuestionToAnswer = Map.of();
        subjectToQuestionToAnswerStatus = Map.of();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}
