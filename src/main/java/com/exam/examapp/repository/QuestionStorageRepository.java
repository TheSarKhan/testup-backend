package com.exam.examapp.repository;

import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.Difficulty;
import com.exam.examapp.model.enums.QuestionType;
import com.exam.examapp.model.question.Question;
import com.exam.examapp.model.question.QuestionStorage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionStorageRepository extends JpaRepository<QuestionStorage, UUID> {
  Optional<QuestionStorage> getByTeacher(User teacher);

    @Query("""
                SELECT DISTINCT qs.teacher
                FROM QuestionStorage qs
                JOIN qs.questions q
            """)
    List<User> findTeachersWithQuestions();

    @Query("""
            select q
            from QuestionStorage qs join qs.questions q
            where (:teacherId IS NULL OR qs.teacher.id = :teacherId)
                and (:type IS NULL OR q.type = :type)
                and (:difficulty IS NULL OR q.difficulty = :difficulty)
                and (:topicId IS NULL OR q.topic.id = :topicId)
            """)
    List<Question> findQuestionsFiltered(Pageable pageable, UUID teacherId, QuestionType type, Difficulty difficulty, UUID topicId);
}
