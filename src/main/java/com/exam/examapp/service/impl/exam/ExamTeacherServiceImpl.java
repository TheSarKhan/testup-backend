package com.exam.examapp.service.impl.exam;

import com.exam.examapp.dto.request.exam.AddExamTeacherRequest;
import com.exam.examapp.dto.response.ExamTeacherResponse;
import com.exam.examapp.dto.response.TeacherResponse;
import com.exam.examapp.model.User;
import com.exam.examapp.model.exam.Exam;
import com.exam.examapp.model.exam.ExamTeacher;
import com.exam.examapp.model.subject.Subject;
import com.exam.examapp.model.subject.SubjectStructureQuestion;
import com.exam.examapp.repository.ExamTeacherRepository;
import com.exam.examapp.service.interfaces.LogService;
import com.exam.examapp.service.interfaces.UserService;
import com.exam.examapp.service.interfaces.exam.ExamService;
import com.exam.examapp.service.interfaces.exam.ExamTeacherService;
import com.exam.examapp.service.interfaces.subject.SubjectService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamTeacherServiceImpl implements ExamTeacherService {
    private final ExamTeacherRepository examTeacherRepository;

    private final ExamService examService;

    private final UserService userService;

    private final SubjectService subjectService;

    private final LogService logService;

    private static Map<String, Integer> getSubjectIntegerMap(Exam exam) {
        Map<String, Integer> subjectToQuestionCountMap = new HashMap<>();
        for (SubjectStructureQuestion structureQuestion : exam.getSubjectStructureQuestions()) {
            String subjectName = structureQuestion.getSubjectStructure().getSubject().getName();
            int size = structureQuestion.getQuestion().size();
            subjectToQuestionCountMap.put(subjectName, size);
        }
        return subjectToQuestionCountMap;
    }

    @Transactional
    public List<TeacherResponse> getTeachers(List<ExamTeacher> teachers) {
        return teachers.stream()
                .map(examTeacher -> {
                    User teacher = examTeacher.getTeacher();
                    return new TeacherResponse(
                            teacher.getId(),
                            teacher.getFullName(),
                            teacher.getProfilePictureUrl(),
                            teacher.getEmail(),
                            examTeacher.getSubject()
                    );
                })
                .toList();
    }

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
            if (examTeacherRepository.existsByExam_IdAndTeacher_Id(exam.getId(), teacher.getId())) {
                sb.append(emailListEntry.getKey()).append(" E-poçt ilə müəllim hazirda imthanda mövcuddur ").append("\n");
                continue;
            }

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
    @Transactional
    public ExamTeacherResponse getExamTeacher(UUID examId) {
        Exam exam = examService.getById(examId);
        List<ExamTeacher> teachers = examTeacherRepository.getByExam(exam);
        List<TeacherResponse> teacherResponses = getTeachers(teachers);
        Map<String, Integer> subjectToQuestionCountMap = getSubjectIntegerMap(exam);
        return new ExamTeacherResponse(
                examId,
                teacherResponses,
                subjectToQuestionCountMap
        );
    }

    @Override
    @Transactional
    public void removeExamTeacher(UUID examId, UUID teacherId) {
        log.info("Müəllim imtahandan kənarlaşdırılır");
        examTeacherRepository.deleteExamTeacherByExam_IdAndTeacher_Id(examId, teacherId);
        log.info("Müəllim imtahandan kənarlaşdırıldı. Id:{}", teacherId);
        logService.save("Müəllim imtahandan kənarlaşdırıldı. Id:" + teacherId, userService.getCurrentUserOrNull());
    }
}
