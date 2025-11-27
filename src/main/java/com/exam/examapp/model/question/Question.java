package com.exam.examapp.model.question;

import com.exam.examapp.dto.QuestionDetails;
import com.exam.examapp.model.subject.Topic;
import com.exam.examapp.model.enums.Difficulty;
import com.exam.examapp.model.enums.QuestionType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

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
@Table(name = "questions")
public class Question {
    @Id
    private UUID id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String mathTitle;

    @Column(columnDefinition = "TEXT")
    private String titleDescription;

    private boolean isTitlePicture;

    @Enumerated(EnumType.STRING)
    private QuestionType type;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @ManyToOne
    private Topic topic;

    private String soundUrl;

    private int questionCount;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Question> questions;

    @Type(JsonBinaryType.class)
    @Column(nullable = false, columnDefinition = "JSONB")
    private QuestionDetails questionDetails;

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
