package com.exam.examapp.model.exam;

import com.exam.examapp.model.Tag;
import com.exam.examapp.model.User;
import com.exam.examapp.model.subject.SubjectStructureQuestion;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "exams")
public class Exam {
    @Id
    private UUID id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String examTitle;

    @ManyToOne(fetch = FetchType.LAZY)
    private User teacher;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<SubjectStructureQuestion> subjectStructureQuestions;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Tag> tags;

    private String explanationVideoUrl;

    @Column(columnDefinition = "TEXT")
    private String examDescription;

    private Integer durationInSeconds;

    private int numberOfQuestions;

    private BigDecimal cost;

    private boolean isReadyForSale;

    private boolean isHidden;

    private UUID startId;

    private double rating;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<UUID, Integer> userIdToRatingMap;

    private List<UUID> hasUncheckedQuestionStudentExamId;

    private boolean isDeleted;

    private Instant createdAt;

    private Instant updatedAt;

    private Instant deletedAt;

    @PrePersist
    void prePersist() {
        id = UUID.randomUUID();
        startId = UUID.randomUUID();
        userIdToRatingMap = new HashMap<>();
        createdAt = updatedAt = Instant.now();
        hasUncheckedQuestionStudentExamId = List.of();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }

    @Override
    public String toString() {
        return "Exam{" +
                "id=" + id +
                ", examTitle='" + examTitle + '\'' +
                ", teacher=" + teacher +
                ", subjectStructureQuestions=" + subjectStructureQuestions +
                ", tags=" + tags +
                ", explanationVideoUrl='" + explanationVideoUrl + '\'' +
                ", examDescription='" + examDescription + '\'' +
                ", durationInSeconds=" + durationInSeconds +
                ", numberOfQuestions=" + numberOfQuestions +
                ", cost=" + cost +
                ", isReadyForSale=" + isReadyForSale +
                ", isHidden=" + isHidden +
                ", startId=" + startId +
                ", rating=" + rating +
                ", userIdToRatingMap=" + userIdToRatingMap +
                ", hasUncheckedQuestionStudentExamId=" + hasUncheckedQuestionStudentExamId +
                ", isDeleted=" + isDeleted +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                '}';
    }
}
