package com.exam.examapp.service.impl.exam.helper;

import com.exam.examapp.dto.CurrentExam;
import com.exam.examapp.dto.response.exam.StartExamResponse;
import com.exam.examapp.exception.custom.BadRequestException;
import com.exam.examapp.exception.custom.ExamExpiredException;
import com.exam.examapp.exception.custom.ReachedLimitException;
import com.exam.examapp.mapper.ExamMapper;
import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.ExamStatus;
import com.exam.examapp.model.enums.Role;
import com.exam.examapp.model.exam.Exam;
import com.exam.examapp.model.exam.StudentExam;
import com.exam.examapp.repository.StudentExamRepository;
import com.exam.examapp.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StartExamService {
    private final StudentExamRepository studentExamRepository;

    private final UserService userService;

    private final ExamResultService examResultService;

    public StartExamResponse startExam(String studentName, Exam exam) {
        log.info("İmtahan id ilə başlayır: {}", exam.getId());
        User user = userService.getCurrentUserOrNull();
        User examCreator = exam.getTeacher();

        if (user == null)
            return startExamWithoutLogin(studentName, exam.getId(), examCreator, exam);

        List<StudentExam> byExamAndStudent = studentExamRepository.getByExamAndStudent(exam, user);
        List<StudentExam> activeStudentExamByExamAndStudent = byExamAndStudent.stream()
                .filter(studentExam -> ExamStatus.ACTIVE.equals(studentExam.getStatus()))
                .toList();

        List<StudentExam> startedStudentExamByExamAndStudent = byExamAndStudent.stream()
                .filter(studentExam -> ExamStatus.STARTED.equals(studentExam.getStatus()))
                .toList();

        if (byExamAndStudent.isEmpty()) {
            log.info("Tələbə imtahanı siyahısı boşdur");
            updateExamStudentCount(exam.getId(), examCreator);
            return createStudentExamEntry(exam, user);
        } else if (!activeStudentExamByExamAndStudent.isEmpty()) {
            log.info("Tələbənin aktiv imtahanı var");
            StudentExam studentExam = activeStudentExamByExamAndStudent.getFirst();
            studentExam.setStatus(ExamStatus.STARTED);
            studentExam.setStartTime(Instant.now());

            List<CurrentExam> currentExams = user.getCurrentExams();
            currentExams.add(new CurrentExam(
                    Instant.now(),
                    exam.getDurationInSeconds(),
                    studentExam.getId(),
                    exam.getId()));
            userService.save(user);

            return new StartExamResponse(
                    studentExam.getId(),
                    ExamStatus.STARTED,
                    Map.of(),
                    Map.of(),
                    Instant.now(),
                    ExamMapper.toResponse(exam));
        } else if (!startedStudentExamByExamAndStudent.isEmpty()) {
            log.info("Tələbə hazırda bu imtahanı işləyir");
            StudentExam first = startedStudentExamByExamAndStudent.getFirst();

            if (first.getExam().getDurationInSeconds() == null) {
                log.info("Hazırki imtahan başladılır. Vaxt limitsizdir.");
                return new StartExamResponse(
                        first.getId(),
                        ExamStatus.STARTED,
                        first.getQuestionIdToAnswerMap(),
                        first.getListeningIdToPlayTimeMap(),
                        first.getStartTime(),
                        ExamMapper.toResponse(exam));
            } else {
                if (Instant.now().plusSeconds(exam.getDurationInSeconds()).isBefore(first.getStartTime())) {
                    log.info("Hazırki imtahan başladılır");
                    return new StartExamResponse(
                            first.getId(),
                            first.getStatus(),
                            first.getQuestionIdToAnswerMap(),
                            first.getListeningIdToPlayTimeMap(),
                            first.getStartTime(),
                            ExamMapper.toResponse(exam));
                } else {
                    log.info("Imtahanın vaxtı bitib");
                    first.setStatus(ExamStatus.EXPIRED);
                    first.setEndTime(Instant.now());
                    examResultService.calculateResult(first);
                    throw new ExamExpiredException("Imtahanın vaxtı bitib");
                }
            }
        } else {
            return createStudentExamEntry(exam, user);
        }
    }

    private StartExamResponse startExamWithoutLogin(String studentName, UUID id, User examCreator, Exam exam) {
        log.info("Girişsiz imtahan başlayır");
        updateExamStudentCount(id, examCreator);

        studentExamRepository.getByExamAndStudentName(exam, studentName).orElseThrow(() ->
                new BadRequestException("Bu tələbə adı artıq bu imtahanda mövcuddur."));

        StudentExam save =
                studentExamRepository.save(
                        StudentExam.builder()
                                .numberOfQuestions(
                                        QuestionCountService.getQuestionCount(exam))
                                .status(ExamStatus.STARTED)
                                .studentName(studentName)
                                .startTime(Instant.now())
                                .exam(exam)
                                .build());

        return new StartExamResponse(
                save.getId(),
                ExamStatus.STARTED,
                Map.of(),
                Map.of(),
                Instant.now(),
                ExamMapper.toResponse(exam));
    }

    private void updateExamStudentCount(UUID id, User examCreator) {
        log.info("Tələbələrin iştirak sayını yeniləyir");

        Map<UUID, Integer> examToStudentCountMap = examCreator.getInfo().getExamToStudentCountMap();
        User currentUserOrNull = userService.getCurrentUserOrNull();
        if (examToStudentCountMap != null &&
                (!(Role.TEACHER.equals(examCreator.getRole()) ||
                        (currentUserOrNull != null && examCreator.getId().equals(currentUserOrNull.getId()))) &&
                        examToStudentCountMap.get(id) >= examCreator.getPack().getStudentPerExam()))
            throw new ReachedLimitException("Bu imtahan üçün tələbə limitinə çatdınız");

        examToStudentCountMap = examToStudentCountMap == null ? new HashMap<>() : examToStudentCountMap;
        examToStudentCountMap.put(id, examToStudentCountMap.getOrDefault(id, 0) + 1);

        userService.save(examCreator);
        log.info("Tələbələrin iştirak sayını yenilədi");
    }

    private StartExamResponse createStudentExamEntry(Exam exam, User user) {
        log.info("Tələbə imtahanı hazırlanır");
        StudentExam save =
                studentExamRepository.save(
                        StudentExam.builder()
                                .numberOfQuestions(
                                        QuestionCountService.getQuestionCount(exam))
                                .status(ExamStatus.STARTED)
                                .startTime(Instant.now())
                                .exam(exam)
                                .student(user)
                                .build());

        List<CurrentExam> currentExams = user.getCurrentExams();
        currentExams.add(new CurrentExam(
                Instant.now(),
                exam.getDurationInSeconds(),
                save.getId(),
                exam.getId()));
        userService.save(user);

        log.info("tələbə imtahanı hazırlandı");
        return new StartExamResponse(
                save.getId(),
                ExamStatus.STARTED,
                Map.of(),
                Map.of(),
                Instant.now(),
                ExamMapper.toResponse(exam));
    }
}
