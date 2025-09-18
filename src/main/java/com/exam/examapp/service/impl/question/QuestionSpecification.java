package com.exam.examapp.service.impl.question;

import com.exam.examapp.model.enums.Difficulty;
import com.exam.examapp.model.enums.QuestionType;
import com.exam.examapp.model.question.Question;
import com.exam.examapp.model.subject.Topic;
import org.springframework.data.jpa.domain.Specification;

public class QuestionSpecification {
    public static Specification<Question> hasTopic(Topic topic) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("topic"), topic);
    }

    public static Specification<Question> hasType(QuestionType type) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("type"), type);
    }

    public static Specification<Question> hasDifficulty(Difficulty difficulty) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("difficulty"), difficulty);
    }
}
