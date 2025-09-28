package com.exam.examapp.repository;

import com.exam.examapp.model.User;
import com.exam.examapp.model.exam.Exam;
import com.exam.examapp.model.exam.StudentExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentExamRepository extends JpaRepository<StudentExam, UUID> {
    List<StudentExam> getByExamAndStudent(Exam exam, User student);

    List<StudentExam> getByStudent(User student);

    Optional<StudentExam> getByExamAndStudentName(Exam exam, String studentName);

    List<StudentExam> getByExam_Id(UUID examId);
}
