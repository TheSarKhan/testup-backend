package com.exam.examapp.service.impl.exam;

import com.exam.examapp.dto.request.exam.AddExamTeacherRequest;
import com.exam.examapp.model.User;
import com.exam.examapp.model.exam.Exam;
import com.exam.examapp.model.exam.ExamTeacher;
import com.exam.examapp.model.subject.Subject;
import com.exam.examapp.repository.ExamTeacherRepository;
import com.exam.examapp.service.interfaces.LogService;
import com.exam.examapp.service.interfaces.UserService;
import com.exam.examapp.service.interfaces.exam.ExamService;
import com.exam.examapp.service.interfaces.exam.ExamTeacherService;
import com.exam.examapp.service.interfaces.subject.SubjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamTeacherServiceImpl implements ExamTeacherService {
    private final ExamTeacherRepository examTeacherRepository;

    private final ExamService examService;

    private final UserService userService;

    private final SubjectService subjectService;

    private final LogService logService;

    @Override
    public String addExamTeacher(AddExamTeacherRequest request) {
        log.info("İmtahana müəllim əlavə olunur");
        StringBuilder sb = new StringBuilder();
        Exam exam = examService.getById(request.examId());
        List<ExamTeacher> examTeachers = new ArrayList<>();
        for (Map.Entry<String, List<UUID>> emailListEntry : request.teacherEmailToSubjectIds().entrySet()) {
            if (!userService.existsByEmail(emailListEntry.getKey())) {
                sb.append(emailListEntry.getKey()).append(" E-poçt ilə müəllim mövcud deyil").append("\n");
                continue;
            }

            User teacher = userService.getByEmail(emailListEntry.getKey());
            List<Subject> subjects =
                    emailListEntry.getValue().stream().map(subjectService::getById).toList();
            examTeachers.add(ExamTeacher.builder().exam(exam).teacher(teacher).subject(subjects).build());
        }
        examTeacherRepository.saveAll(examTeachers);
        String message = !sb.isEmpty() ? sb.toString() : "Müəllim(lər) uğurla əlavə edildi";
        log.info(message);
        logService.save(message, userService.getCurrentUserOrNull());
        return message;
    }

    @Override
    public void removeExamTeacher(UUID examId, UUID teacherId) {
        log.info("Müəllim imtahandan kənarlaşdırılır");
        examTeacherRepository.deleteByTeacherAndExam(
                userService.getUserById(teacherId), examService.getById(examId));
        log.info("Müəllim imtahandan kənarlaşdırıldı. Id:{}", teacherId);
        logService.save("Müəllim imtahandan kənarlaşdırıldı. Id:" + teacherId, userService.getCurrentUserOrNull());
    }
}
