package com.exam.examapp.repository.question;

import com.exam.examapp.model.question.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID>, JpaSpecificationExecutor<Question> {
    @Query("""
    select (count(e) > 0)
    from Exam e
        join e.subjectStructureQuestions ssq
        join ssq.question q
        left join q.questions qq
    where q.id = :id or qq.id = :id
    """)
    boolean existsInExam(UUID id);
}
