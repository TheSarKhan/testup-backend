package com.exam.examapp.model.exam;

import com.exam.examapp.model.Tag;
import com.exam.examapp.model.User;
import com.exam.examapp.model.subject.SubjectStructureQuestion;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "exams")
public class Exam {
  @Id private UUID id;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String examTitle;

  @ManyToOne(fetch = FetchType.LAZY)
  private User teacher;

  @OneToMany(cascade = CascadeType.ALL)
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

  private boolean isActive;

  private Instant createdAt;

  private Instant updatedAt;

  private Instant deletedAt;

  @PrePersist
  void prePersist() {
    id = UUID.randomUUID();
    startId = UUID.randomUUID();
    userIdToRatingMap = new HashMap<>();
    createdAt = updatedAt = Instant.now();
  }

  @PreUpdate
  void preUpdate() {
    updatedAt = Instant.now();
  }
}
