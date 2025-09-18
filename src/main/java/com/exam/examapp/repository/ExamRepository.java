package com.exam.examapp.repository;

import com.exam.examapp.model.User;
import com.exam.examapp.model.exam.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExamRepository extends JpaRepository<Exam, UUID>, JpaSpecificationExecutor<Exam> {
    @Query(
            """
                    SELECT e
                    FROM Exam e
                    LEFT JOIN FETCH e.teacher
                    LEFT JOIN FETCH e.subjectStructureQuestions ssq
                    LEFT JOIN FETCH ssq.subjectStructure s
                    LEFT JOIN FETCH s.subject
                    """)
    List<Exam> getByTeacher_Id(UUID teacherId);

    List<Exam> getByTeacher(User teacher);

    @Query("from Exam order by createdAt desc ")
    List<Exam> getLastCreated();
}
