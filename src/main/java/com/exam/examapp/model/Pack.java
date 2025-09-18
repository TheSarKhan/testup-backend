package com.exam.examapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "packs")
public class Pack {
    @Id
    private UUID id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String header;

    @Column(unique = true, nullable = false, columnDefinition = "TEXT")
    private String packName;

    private BigDecimal price;

    private int monthlyExamCount;

    private int questionCountPerExam;

    private int totalExamCount;

    private int studentPerExam;

    private boolean canAnalysisStudentResults;

    private boolean canEditExam;

    private boolean canAddPicture;

    private boolean canAddPdfSound;

    private boolean canShareViaCode;

    private boolean canDownloadExamAsPdf;

    private boolean canAddMultipleSubjectInOneExam;

    private boolean canUseExamTemplate;

    private boolean canAddManualCheckAutoQuestion;

    private boolean canSelectExamDuration;

    private boolean canUseQuestionDb;

    private boolean canPrepareQuestionsDb;

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
