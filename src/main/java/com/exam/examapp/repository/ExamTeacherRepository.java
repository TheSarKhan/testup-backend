package com.exam.examapp.repository;

import com.exam.examapp.model.User;
import com.exam.examapp.model.exam.Exam;
import com.exam.examapp.model.exam.ExamTeacher;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamTeacherRepository extends JpaRepository<ExamTeacher, UUID> {
    List<ExamTeacher> getByExam(Exam exam);

    boolean existsByExam_IdAndTeacher_Id(UUID examId, UUID teacherId);

    List<ExamTeacher> getByTeacherOrderByCreatedAtDesc(User teacher);

    void deleteExamTeacherByExam_IdAndTeacher_Id(UUID examId, UUID teacherId);
}
