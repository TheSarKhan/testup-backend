package com.exam.examapp.repository;

import com.exam.examapp.model.User;
import com.exam.examapp.model.exam.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExamRepository extends JpaRepository<Exam, UUID>, JpaSpecificationExecutor<Exam> {
    long countBySubjectStructureQuestions_IdIn(List<UUID> questionIds);

    List<Exam> getByTeacher(User teacher);

    @Query("from Exam order by createdAt desc ")
    List<Exam> getLastCreated();

    Optional<Exam> getExamByStartId(UUID startId);

    long countByCreatedAtAfter(Instant createdAtAfter);

    @Query("""
            select distinct ex
            from Exam ex join ExamTeacher ext
            on ex = ext.exam
            """)
    List<Exam> getTeacherCooperationExams();
}
