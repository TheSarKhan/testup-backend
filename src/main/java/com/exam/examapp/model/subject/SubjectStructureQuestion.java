package com.exam.examapp.model.subject;

import com.exam.examapp.model.question.Question;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "subject_structures_questions")
public class SubjectStructureQuestion {
  @Id private UUID id;

  @ManyToOne private SubjectStructure subjectStructure;

  @ManyToMany(fetch = FetchType.EAGER)
  private List<Question> question;

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
