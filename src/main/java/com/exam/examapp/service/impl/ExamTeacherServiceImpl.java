package com.exam.examapp.service.impl;

import com.exam.examapp.dto.request.AddExamTeacherRequest;
import com.exam.examapp.model.User;
import com.exam.examapp.model.exam.Exam;
import com.exam.examapp.model.exam.ExamTeacher;
import com.exam.examapp.model.subject.Subject;
import com.exam.examapp.repository.ExamTeacherRepository;
import com.exam.examapp.service.interfaces.ExamService;
import com.exam.examapp.service.interfaces.ExamTeacherService;
import com.exam.examapp.service.interfaces.UserService;
import com.exam.examapp.service.interfaces.subject.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExamTeacherServiceImpl implements ExamTeacherService {
    private final ExamTeacherRepository examTeacherRepository;

    private final ExamService examService;

    private final UserService userService;

    private final SubjectService subjectService;

    @Override
    public String addExamTeacher(AddExamTeacherRequest request) {
        StringBuilder sb = new StringBuilder();
        Exam exam = examService.getById(request.examId());
        List<ExamTeacher> examTeachers = new ArrayList<>();
        for (Map.Entry<String, List<UUID>> emailListEntry : request.teacherEmailToSubjectIds().entrySet()) {
            if (!userService.existsByEmail(emailListEntry.getKey())) {
                sb.append("Teacher with email ").append(emailListEntry.getKey())
                        .append(" does not exist.").append("\n");
                continue;
            }

            User teacher = userService.getByEmail(emailListEntry.getKey());
            List<Subject> subjects =
                    emailListEntry.getValue().stream().map(subjectService::getById).toList();
            examTeachers.add(ExamTeacher.builder().exam(exam).teacher(teacher).subject(subjects).build());
        }
        examTeacherRepository.saveAll(examTeachers);

        return sb.isEmpty() ? sb.toString() : "Teacher(s) added successfully.";
    }

    @Override
    public void removeExamTeacher(UUID examId, UUID teacherId) {
        examTeacherRepository.deleteByTeacherAndExam(
                userService.getUserById(teacherId), examService.getById(examId));
    }
}
