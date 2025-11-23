package com.exam.examapp.service.impl.question;

import com.exam.examapp.model.enums.Difficulty;
import com.exam.examapp.model.enums.QuestionType;
import com.exam.examapp.model.question.Question;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class QuestionStorageSpecification {
    public Specification<Question> filterGetQuestions(
            UUID teacherId,
            QuestionType type,
            Difficulty difficulty,
            UUID topicId
    ) {
        Specification<Question> spec = Specification.unrestricted();

        if (teacherId != null)
            spec = spec.and(hasTeacherId(teacherId));

        if (type != null)
            spec = spec.and(hasType(type));

        if (difficulty != null)
            spec = spec.and(hasDifficulty(difficulty));

        if (topicId != null)
            spec = spec.and(hasTopic(topicId));

        return spec;
    }

    public Specification<Question> hasTeacherId(UUID teacherId) {
        return (root, query, cb) -> cb.equal(root.get("teacher").get("id"), teacherId);
    }

    public Specification<Question> hasType(QuestionType type) {
        return (root, query, cb) -> cb.equal(root.get("type"), type);
    }

    public Specification<Question> hasDifficulty(Difficulty difficulty) {
        return (root, query, cb) -> cb.equal(root.get("difficulty"), difficulty);
    }

    public Specification<Question> hasTopic(UUID topicId) {
        return (root, query, cb) -> cb.equal(root.get("topic").get("id"), topicId);
    }
}
