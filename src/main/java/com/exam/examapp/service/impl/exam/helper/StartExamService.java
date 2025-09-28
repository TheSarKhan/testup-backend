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
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StartExamService {
    private final StudentExamRepository studentExamRepository;

    private final UserService userService;

    private final ExamResultService examResultService;

    public StartExamResponse startExam(String studentName, Exam exam) {
        User user = userService.getCurrentUserOrNull();
        User examCreator = exam.getTeacher();

        if (user == null)
            return startExamWithoutLogin(studentName, exam.getId(), examCreator, exam);

        List<StudentExam> byExamAndStudent = studentExamRepository.getByExamAndStudent(exam, user);

        if (byExamAndStudent.isEmpty()) {
            updateExamStudentCount(exam.getId(), examCreator);

            return createStudentExamEntry(exam, user);
        } else if (byExamAndStudent.size() == 1
                && ExamStatus.ACTIVE.equals(byExamAndStudent.getFirst().getStatus())) {
            byExamAndStudent.getFirst().setStatus(ExamStatus.STARTED);
            byExamAndStudent.getFirst().setStartTime(Instant.now());

            List<CurrentExam> currentExams = user.getCurrentExams();
            currentExams.add(new CurrentExam(
                    Instant.now(),
                    exam.getDurationInSeconds(),
                    byExamAndStudent.getFirst().getId(),
                    exam.getId()));
            userService.save(user);

            return new StartExamResponse(
                    byExamAndStudent.getFirst().getId(),
                    ExamStatus.ACTIVE,
                    Map.of(),
                    Map.of(),
                    Instant.now(),
                    ExamMapper.toResponse(exam));
        } else if (byExamAndStudent.size() == 1
                && ExamStatus.STARTED.equals(byExamAndStudent.getFirst().getStatus())) {
            StudentExam first = byExamAndStudent.getFirst();

            if (first.getExam().getDurationInSeconds() == null) {
                return new StartExamResponse(
                        first.getId(),
                        ExamStatus.STARTED,
                        first.getQuestionIdToAnswerMap(),
                        first.getListeningIdToPlayTimeMap(),
                        first.getStartTime(),
                        ExamMapper.toResponse(exam));
            } else {
                if (Instant.now().plusSeconds(exam.getDurationInSeconds()).isBefore(first.getStartTime())) {
                    return new StartExamResponse(
                            first.getId(),
                            first.getStatus(),
                            first.getQuestionIdToAnswerMap(),
                            first.getListeningIdToPlayTimeMap(),
                            first.getStartTime(),
                            ExamMapper.toResponse(exam));
                } else {
                    first.setStatus(ExamStatus.EXPIRED);
                    first.setEndTime(Instant.now());
                    examResultService.calculateResult(first);
                    throw new ExamExpiredException("Exam has expired.");
                }
            }
        } else {
            return createStudentExamEntry(exam, user);
        }
    }

    private StartExamResponse startExamWithoutLogin(String studentName, UUID id, User examCreator, Exam exam) {
        updateExamStudentCount(id, examCreator);

        studentExamRepository.getByExamAndStudentName(exam, studentName).orElseThrow(() ->
                new BadRequestException("This student name already exists in this exam."));

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
                ExamStatus.ACTIVE,
                Map.of(),
                Map.of(),
                Instant.now(),
                ExamMapper.toResponse(exam));
    }

    private void updateExamStudentCount(UUID id, User examCreator) {
        Map<UUID, Integer> examToStudentCountMap = examCreator.getInfo().getExamToStudentCountMap();
        User currentUserOrNull = userService.getCurrentUserOrNull();
        if (examToStudentCountMap != null &&
                (!(Role.TEACHER.equals(examCreator.getRole()) ||
                        (currentUserOrNull != null && examCreator.getId().equals(currentUserOrNull.getId()))) &&
                        examToStudentCountMap.get(id) >= examCreator.getPack().getStudentPerExam()))
            throw new ReachedLimitException("You have reached the limit of students for this exam");

        examToStudentCountMap = examToStudentCountMap == null ? new HashMap<>() : examToStudentCountMap;
        examToStudentCountMap.put(id, examToStudentCountMap.getOrDefault(id, 0) + 1);

        userService.save(examCreator);
    }

    private StartExamResponse createStudentExamEntry(Exam exam, User user) {
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

        return new StartExamResponse(
                save.getId(),
                ExamStatus.ACTIVE,
                Map.of(),
                Map.of(),
                Instant.now(),
                ExamMapper.toResponse(exam));
    }
}
