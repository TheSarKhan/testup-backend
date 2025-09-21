package com.exam.examapp.service.impl.exam;

import com.exam.examapp.model.exam.Exam;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class ExamSpecification {
    public static Specification<Exam> hasTag(UUID tagId) {
        return (root, query, cb) ->
                cb.equal(root.get("tags").get("id"), tagId);
    }
}
