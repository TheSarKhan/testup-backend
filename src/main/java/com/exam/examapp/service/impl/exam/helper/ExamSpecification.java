package com.exam.examapp.service.impl.exam.helper;

import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.ExamStatus;
import com.exam.examapp.model.exam.Exam;
import com.exam.examapp.model.exam.StudentExam;
import com.exam.examapp.repository.StudentExamRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExamSpecification {
    public static Specification<Exam> hasTag(UUID tagId) {
        return (root, query, cb) ->
                cb.equal(root.get("tags").get("id"), tagId);
    }

    public static Specification<Exam> hasTags(List<UUID> tagIds) {
        return (root, query, criteriaBuilder) -> {
            if (tagIds == null || tagIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            Join<Object, Object> tagsJoin = root.join("tags");

            CriteriaBuilder.In<UUID> inClause = criteriaBuilder.in(tagsJoin.get("id"));
            for (UUID tagId : tagIds) {
                inClause.value(tagId);
            }

            assert query != null;
            query.distinct(true);

            return inClause;
        };
    }

    public static Specification<Exam> hasRatingInRange(List<Integer> ratings) {
        return (root, query, criteriaBuilder) -> {
            if (ratings == null || ratings.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            Path<Double> ratingPath = root.get("rating");
            List<Predicate> predicates = new ArrayList<>();

            for (Integer r : ratings) {
                double lower = r;
                double upper = (r == 5) ? 5 : r + 1;

                if (r == 5) {
                    predicates.add(criteriaBuilder.equal(ratingPath, 5.0));
                } else {
                    predicates.add(criteriaBuilder.and(
                            criteriaBuilder.greaterThanOrEqualTo(ratingPath, lower),
                            criteriaBuilder.lessThan(ratingPath, upper)
                    ));
                }
            }
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Exam> hasCostBetween(Integer minCost, Integer maxCost) {
        return (root, query, criteriaBuilder) -> {
            Path<BigDecimal> costPath = root.get("cost");

            List<Predicate> predicates = new ArrayList<>();

            if (minCost != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(costPath, BigDecimal.valueOf(minCost)));
            }

            if (maxCost != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(costPath, BigDecimal.valueOf(maxCost)));
            }

            return predicates.isEmpty()
                    ? criteriaBuilder.conjunction()
                    : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Exam> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("examTitle")), "%" + name + "%");
    }

    public static Specification<Exam> hasType(ExamType type,
                                                User currentUser,
                                                StudentExamRepository studentExamRepository) {
        return (root, query, cb) -> {
            if (type == null || type == ExamType.ALL) {
                return cb.conjunction();
            }

            return switch (type) {
                case FREE -> cb.equal(root.get("cost"), BigDecimal.ZERO);
                case PAID -> cb.greaterThan(root.get("cost"), BigDecimal.ZERO);
                case BOUGHT -> {
                    List<StudentExam> studentExams = studentExamRepository.findByStudentAndStatus(
                            currentUser, ExamStatus.ACTIVE
                    );
                    List<UUID> boughtExamIds = studentExams.stream()
                            .map(studentExam -> studentExam.getExam().getId())
                            .toList();

                    if (boughtExamIds.isEmpty())
                        yield cb.disjunction();

                    yield root.get("id").in(boughtExamIds);
                }
                case FINISHED -> {
                    List<StudentExam> studentExams = studentExamRepository.findByStudentAndStatusNotIn(
                            currentUser, List.of(ExamStatus.ACTIVE, ExamStatus.STARTED)
                    );

                    List<UUID> finishedExamIds = studentExams.stream()
                            .map(studentExam -> studentExam.getExam().getId())
                            .toList();

                    if (finishedExamIds.isEmpty())
                        yield cb.disjunction();

                    yield root.get("id").in(finishedExamIds);
                }
                default -> cb.conjunction();
            };
        };
    }

}
