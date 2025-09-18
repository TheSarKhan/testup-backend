package com.exam.examapp.repository;

import com.exam.examapp.dto.response.ExamBlockResponse;
import com.exam.examapp.model.User;
import com.exam.examapp.model.exam.Exam;
import com.exam.examapp.model.exam.ExamTeacher;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamTeacherRepository extends JpaRepository<ExamTeacher, UUID> {
    void deleteByTeacherAndExam(User teacher, Exam exam);

    List<ExamTeacher> getByTeacher(User teacher);
}
